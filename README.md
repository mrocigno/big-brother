# Sandman library

Created to improve the manual testing, the Sandman lib is a tool that allows you to create another tools or use the provided by default.

* Corinthian - provide an interceptor (to use with OkHttp3) and an interface to see all requests made in during a session
* Matthew - provide an interface to see the logs
* Lucien - a report generator. Lucien will record a timeline using data provided by the other nightmares (or custom)


## Getting started

To use Sandman in the app, implements the following dependencies in `build.gradle` on app module:
```groovy
dependencies {
    def sandman_last_release = '0.0.2-SNAPSHOT'
    
    // The version definition is required only on core implementation
    implementation "io.github.mrocigno:sandman-core:$sandman_last_release"
    
    // == optional ==
    // To record network interaction (need OkHttp3)
    implementation "io.github.mrocigno:sandman-corinthian"

    // To record logs
    implementation "io.github.mrocigno:sandman-matthew"
    
    // To create a report with all recorded data
    implementation "io.github.mrocigno:sandman-lucien"
}
```

### Setup

Now with de dependencies correctly implemented, we will configure to start using.
In your `:app` module, create a class that extends `TheDreamingProvider`:
```kotlin
/*
* As a ContentProvider, this class will run before the application class
* So be careful using DI here
*/
class TheDreamingCustom : TheDreamingProvider() {
    
    /*
    * Here you can create a logic to enable or disable the Sandman.
    * You also can disable in AndroidManifest.xml as we will see soon
    */
    override val isEnabled get() = true

    // Here we will configure all pages that will be rendered in the sandman container
    override fun setupTheDreaming() {
        
        // To add a page that will be displayed in all activities
        addNightmare("Tool name") {
            MyToolFragment()
        }

        // To add a page that will be displayed in a specific activity
        addLocation(MainActivity::class) {
            dream("Tool name") {
                MyToolFragment()
            }
        }
        
        // If the sandman-corinthian was implemented in build.grade
        addCorinthian()
        
        // If the sandman-matthew was implemented in build.grade
        addMatthew()
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
            android:authorities="${applicationId}.SANDMAN"
            android:name=".TheDreamingCustom"
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
>            manifestPlaceholders.sandman = "true"
>        }
>        release {
>            manifestPlaceholders.sandman = "false"
>        }
>    }
> }
> ```
>
> in `AndroidManifest.xml`
> ```xml
> <provider
>    android:authorities="${applicationId}.SANDMAN"
>    android:name=".TheDreamingCustom"
>    android:enabled="${sandman}"
>    android:exported="false"/>
> ```

### And that's it! You are ready to create and use tools in Sandman

## Corinthian

> TODO: add prints and usage

## Matthew

> TODO: add prints and usage

## Lucien

> TODO: add prints and usage