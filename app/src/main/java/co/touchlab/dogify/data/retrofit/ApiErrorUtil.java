package co.touchlab.dogify.data.retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;

import co.touchlab.dogify.data.entities.ApiError;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiErrorUtil {
    Retrofit retrofit;

    public ApiErrorUtil(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public ApiError parseError(Response<?> response) {
        Converter<ResponseBody, ApiError> converter =
                retrofit.responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException | NullPointerException e) {
            return new ApiError();
        }

        return error;
    }
}
