# Film-Finder
Film finder is an application to discover popular movies. Try it out!

**How to build and try it!**

To build it, you require Android Studio and the latest Gradle version.
To run on an emulator (click here if you need help) just press the run app button on Android Studio.

**Architecture**

This app is a single activity application, with a single module. The reason behind that is that there are not as of yet extra modules like a debug library.

The app uses MVVM to keep logic away from the view layer, have the data outlive lifecycle changes, and make business logic easier to unit test.

This app also uses other jetpack components like live data and navigation because of the reduction in boilerplate code it allows for. 

The app also uses Diff Util to calculate differences between lists for the main screen’s recycler view

For unit testing, I used mockK as the mocking library and JUnit as the framework;  for dependency injection, Dagger2.

**Third-party libraries**

  **Shimmer**: for the loading states of the screens, because of how it looks compared to a progress bar.
  **Retrofit**: nice wrapper that reduces the boilerplate code of using okHttp
  **OkHttp**’s LoggingInterceptor: Used for the ability to debug network issues. 
  **Glide**: Used for its convenience when loading images
  **Gson**: Convenient library to transform from and into JSON objects and data classes.
