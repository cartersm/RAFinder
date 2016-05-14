# RAFinder
## Android Development README

###### _Application Authors:_ Sean Carter, Laura Davey

### First Things First

1. Make sure you're using Android Studio. Hopefully this is a given for an Android Project.

2. From Android Studio's "Build variants" menu, select "devDebug".

3. Either connect a device or use an emulator running API17+ (target is API23).

4. Run it! You should see a welcome/login screen. _Note: in the production version, the login screen will display only momentarily before launching your browser for Rosefire authentication._

### Basics
1. This app has two distinct "flavors", as Gradle calls them. Dev and Prod. These two connect to different databases, with different authentication methods, and different data. A devDebug build is what you'll be using most of the time, and a prodRelease build is what will be given to end users as an APK. 

2. The main gotcha of the multi-flavor format is that Android Studio's default Android project view _hides directories unrelated to the build variant you're telling it to run_. This means that it is easy to forget to add something to BOTH different versions of a file. Thankfully, the only split file is the global constants.

3. All classes are Javadoc-commented, and all methods are Javadoc-commented or (always) descriptively named. Use this to your advantage.

### Dependencies
- ```joda-time:joda-time``` is basically the Java 7- DateTime libraries on steroids. [See here](http://www.joda.org/joda-time/) for details.

- ```com.android.support:support-v4``` is the standard v4 support library.

- ```com.firebase:firebase-client-android``` is the official Firebase API for Android. [See here](https://www.firebase.com/docs/android/quickstart.html) for details.

- ```eu.ocathain.com.sun.mail:javax.mail``` is an email client that allows the app to send emails directly. This is part of the "anonymous feedback" feature.

- ```edu.rosehulman.csse.rosefire:rosefire-android-client``` is used to authenticate users with Rose-Hulman's KERBEROS authentication system, and use that login token to log into the Firebase database.

### Roadmap
Now for the fun part: the code walkthrough. This will be structured in the same way as the Android project view.

** *app* **. The root directory.

- ** *manifests* **. Contains the app's one and only manifest.
- ** *java* **. Contains java sources.
  - ** *edu.rosehulman.rafinder* **. The root package.
    - ** *adapter* **. ListAdapters for the various list views.
    - ** *controller* **. Activities and fragments, except for MainActivity.
    - ** *loader* **. These classes front-load all Firebase data in daisy chain, in order to force a synchronous load.
    - ** *model* **. All models required by the app. These are, for the most part, POJOs.
      - ** *person* **. The hierarchy of models specific to people.
      - ** *reshall* ** The hierarchy of models specific to Residence Halls, mostly the Hall Roster view.
  - **JSSEProvider, GmailSender**. Used to send anonymous feedback via a dedicated Gmail account.
  - ** *edu.rosehulman.rafinder (dev)* **. Same package structure as the main root package; contains java sources unique to the dev build.
    - **ConfigKeys**. This is the only file that differs between the two environments.
  - ** *edu.rosehulman.rafinder (prod)* **. Same package structure as the main root package; contains java sources unique to the prod build.
- ** *res* **. Pretty self-explanatory.
  - ** *layout* **. 4 distinct "types" of layouts here:
    1. **activity\*.html**. Activity views.
    2. **dialog\*.html**. Dialog views.
    3. **fragment\*.html**. Fragment views. Imagine that.
    4. **layout\*.html**. Smaller pieces of views. Mostly list view items.

### Closing comments
Hopefully this app will be relatively straightforward for you, but,
##### If you are, for any reason, still floundering after reading this README, or have problems with anything, feel free to contact me at sean47933@gmail.com.