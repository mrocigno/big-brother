# BigBrother library

Created to improve the manual testing, the BigBrother lib is a tool that allows you to create another tools or use the provided by default.

* Network - provide an interceptor (to use with OkHttp3) and an interface to see all requests made in during a session
* Log - provide an interface to see the logs
* Report - a report generator. It will record a timeline using data provided by the other modules (or custom)


## Getting started

To use BigBrother in the app, implements the following dependencies in `build.gradle` on app module:
```groovy
dependencies {
    def bigbrother_last_release = '0.0.1'
    
    // The version definition is required only on core implementation
    implementation "io.github.mrocigno:big-brother-core:$bigbrother_last_release"
    
    // == optional ==
    // To record network interaction (need OkHttp3)
    implementation "io.github.mrocigno:big-brother-network"

    // To record logs
    implementation "io.github.mrocigno:big-brother-log"
    
    // To create a report with all recorded data
    implementation "io.github.mrocigno:big-brother-report"
}
```

### Setup

Now with de dependencies correctly implemented, we will configure to start using.
In your `:app` module, create a class that extends `BigBrotherProvider`:
```kotlin
/*
* As a ContentProvider, this class will run before the application class
* So be careful using DI here
*/
class BigBrotherCustom : BigBrotherProvider() {
    
    /*
    * Here you can create a logic to enable or disable the BigBrother.
    * You also can disable in AndroidManifest.xml as we will see soon
    */
    override val isEnabled get() = true

    // Here we will configure all pages that will be rendered in the BigBrother container
    override fun setupPages() {
        
        // To add a page that will be displayed in all activities
        addPage("Tool name") {
            MyToolFragment()
        }

        // To add a page that will be displayed in a specific activity
        addPage(MainActivity::class) {
            page("Tool name") {
                MyToolFragment()
            }
        }
        
        // If the big-brother-network was implemented in build.grade
        addNetworkPage()
        
        // If the big-brother-log was implemented in build.grade
        addLogPage()
    }
}
```

After creating the class, we will configure it in the manifest:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application>

        <!-- here in AndroidManifest we also can enable/disable the provider using manifestPlaceholder -->
        <provider
            android:authorities="${applicationId}.BIGBROTHER"
            android:name=".BigBrotherCustom"
            android:enabled="true"
            android:exported="false"/>

    </application>

</manifest>
```

> Example of enable/disable with manifestPlaceholder
> in `build.gradle`
> ```groovy
> android {
>    defaultConfig {
>        applicationId "example.manifest.app"
>    }
>    buildTypes {
>        debug {
>            manifestPlaceholders.bigbrother = "true"
>        }
>        release {
>            manifestPlaceholders.bigbrother = "false"
>        }
>    }
> }
> ```
>
> in `AndroidManifest.xml`
> ```xml
> <provider
>    android:authorities="${applicationId}.BIGBROTHER"
>    android:name=".BigBrotherCustom"
>    android:enabled="${bigbrother}"
>    android:exported="false"/>
> ```

Then finally we start the watch in Application class
```kotlin
class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Optional configuration to bubble
        BigBrother.config {
            iconRes = R.drawable.bigbrother_ic_main
            size = 200
            disabledAlpha = .3f
            initialLocation = PointF(500f, 500f)
        }
        
        // Start the watch
        BigBrother.watch(this, isBubbleEnabled = true)
    }
}
```

### And that's it! You are ready to create and use tools in BigBrother

## Network

> TODO: add prints and usage

## Log

> TODO: add prints and usage

## Report

> TODO: add prints and usage