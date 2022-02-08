# Dogify
This app simply fetches the name and picture of a bunch of different dog breeds from [here](https://dog.ceo/dog-api/documentation/) and displays them in a grid.

#### Uses MVVM pattern and leverages remote and local data sources with `LiveData`, `Glide`, `RecyclerView` and `Room` for offline use
- Using Androidx Libraries and a Target SDK Level of 30 (Android 11)

### Image Loading
- Executor service is used for loading images, awaits list of requests for final image urls
- Glide chosen over picasso for smaller/faster cache, and simpler use of rounded corner transformation
- All images are fetched to force Glide to store in cache

### Data
- Repository class merges data streams from Local and Remote data sources with `MediatorLiveData`
- Retrofit `DogService` is used for fetching form the Dog.ceo API
- Errors in remote data source fetching will default to the local RoomDb data source

### UI
- Only has one Activity `MainActivity` which loads a list of dog breed names and images from the network and displays them in a grid.
- Swiping down will refresh the list using a `SwiperRefreshLayout`

### Optimizations
- Currently the app will wait for the full list of urls before loading the `RecyclerView`. This could be benchmarked and potentially optimized. An `ExecutorCompletionService` can be used to load images as soon as the url is fetched. Need to maintain list order and map calls back.
- Fetching with glide may be fighting with the `RecyclerView` for resources on loading, I added a timer to delay the fetch for the initial `RecyclerView` load, but this should be optimized.