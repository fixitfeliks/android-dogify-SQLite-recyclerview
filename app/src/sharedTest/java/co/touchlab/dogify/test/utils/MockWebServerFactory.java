package co.touchlab.dogify.test.utils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import co.touchlab.dogify.data.retrofit.resultmodels.ErrorResult;
import co.touchlab.dogify.data.retrofit.resultmodels.ImageResult;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.RecordedRequest;
import mockwebserver3.MockWebServer;

public class MockWebServerFactory {
    private static final Gson gson = new Gson();

    public static MockWebServer getNoResponse() {
        return new MockWebServer();
    }

    public static MockWebServer getEmptyErrorResponse() {
        ErrorResult errorResultExpected = new ErrorResult();
        errorResultExpected.status = "error";
        errorResultExpected.message = "Error getting breed data";
        errorResultExpected.code = 0;

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(getErrorMockResponse(gson.toJson(errorResultExpected)));
        return mockWebServer;
    }

    public static MockWebServer getErrorResponse() throws FileNotFoundException {
        ErrorResult errorResult = gson.fromJson(new FileReader((EntityFilepaths.FILEPATH_ERROR_RESULT_SAVED_RESPONSE)), ErrorResult.class);
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(getErrorMockResponse(gson.toJson(errorResult)));
        return mockWebServer;
    }

    private static MockResponse getErrorMockResponse(String body) {
        return new MockResponse()
                .setResponseCode(404)
                .setBody(body)
                .addHeader("Content-Type", "application/json");
    }

    public static MockWebServer getSuccessfulBreedCallFlow() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();
        System.out.println(new File(".").getAbsoluteFile());
        List<String> imageCallList = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_IMAGE_URL_CALLLIST_EXPECTED_SAVED_RESPONSE), List.class);
        Type imageResultsListType = new TypeToken<List<ImageResult>>(){}.getType();
        List<ImageResult> imageResults = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_IMAGES_RESULT_LIST_SAVED_RESPONSE), imageResultsListType);
        byte[] nameResultJson = Files.readAllBytes(Paths.get(EntityFilepaths.FILEPATH_NAME_RESULT_SAVED_RESPONSE));

        HashMap<String, String> imageUrlLookup = imageResultListToMap(imageResults, imageCallList);
        Dispatcher dispatcher = new Dispatcher() {
            @NonNull
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String parsedBreedPath = parseUrlPathToCallLitItem(Objects.requireNonNull(request.getPath()));
                if ("/breeds/list/all".equals(parsedBreedPath)) {
                    return getMockResponse(nameResultJson);
                }
                if (imageUrlLookup.containsKey(parsedBreedPath)) {
                    String url = parseUrlPathToCallLitItem(parsedBreedPath);
                    return getMockResponse(imageUrlLookup.get(url));
                } else {
                    return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        return mockWebServer;
    }

    private static HashMap<String, String> imageResultListToMap(List<ImageResult> imageResults, List<String> imageCallList) {
        HashMap<String, String> urlLookUp = new HashMap<>();
        for (int i = 0; i < imageResults.size(); i++) {
            urlLookUp.put(imageCallList.get(i), gson.toJson(imageResults.get(i)));
        }
        return urlLookUp;
    }

    private static String parseUrlPathToCallLitItem(String path) {
        int startIndex = path.indexOf("/",1) + 1;
        int endIndex = path.indexOf("/images/random");

        String substr;
        try {
            substr = path.substring(startIndex, endIndex);
        } catch (IndexOutOfBoundsException e) {
            return path;
        }
        return substr;
    }

    private static MockResponse getMockResponse(Object obj) {
        if (obj instanceof String) {
            return new MockResponse()
                    .setResponseCode(200)
                    .setBody((String)obj)
                    .addHeader("Content-Type", "application/json");
        } else if (obj instanceof byte[]) {
            return new MockResponse()
                    .setResponseCode(200)
                    .setBody(new String((byte[]) obj, StandardCharsets.US_ASCII))
                    .addHeader("Content-Type", "application/json");
        }
        return new MockResponse().setResponseCode(404);
    }
}
