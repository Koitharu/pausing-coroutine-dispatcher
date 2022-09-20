# pausing-coroutine-dispatcher

A Kotlin Multiplatform library (Jvm and Android are supported as well) that provide a
flexible way to temporary pause and resume coroutines.

---
### Getting started

1. Add it in your root build.gradle at the end of repositories:

   ```groovy
   allprojects {
	   repositories {
		   ...
		   maven { url 'https://jitpack.io' }
	   }
   }
   ```

2. Add the dependency

    ```groovy
    dependencies {
        implementation("com.github.Koitharu:pausing-coroutine-dispatcher:$version")
    }
    ```

   Look for versions at [JitPack](https://jitpack.io/#Koitharu/pausing-coroutine-dispatcher)


### Usage 

Start a coroutine that can be paused:
```kotlin
val pausingJob = scope.launchPausing {
    // do some long-running job
}
```
or
```kotlin
val pausingDeferred = scope.asyncPausing {
    // do some long-running job
}
```

Then pause a launched coroutine:

```kotlin
pausingJob.pause()
```
and resume
```kotlin
pausingJob.resume()
```

If you switch a dispatcher, use `pausing()` function to make it support pausing too:
```kotlin
withContext(Dispatchers.IO.pausing()) {
    // I/O tasks
}
``` 

Nested coroutines are supported as well.
```kotlin
val pausingJob = scope.launchPausing {
    // do some long-running job
    val deferred = async {
        // this coroutine also will be paused within parent 
    }
   // or
   launch(Dispatchers.Main.pausing()) {
       // TODO
   }
}
```

- Use standard `launch` and `async` coroutines builder to make nested coroutines pauses within the parent one. Do not forget to call `.pausing()` on new Dispatcher if you passing it.
- Use `launchPausing` and `asyncPausing` to enable nested coroutines to be paused regardless of the parent. Calling `.pausing()` is unwanted in this case even if you pass a Dispatcher.
- Use `launch(NonPausing)` and `async(NonPausing)` to disable nested coroutines to be paused within the parent.

Note that a coroutine can only be paused at suspension points.
If you want to add these checks, use the `ensureNotPaused` function, that is more 
efficient comparing to `yield`.