# RAFinder
## Web Development README

###### _Application Authors:_ Sean Carter

### First Things First

1. Make sure you're using WebStorm (for which you can get an educational license [here](https://www.jetbrains.com/student/), or at least IntelliJ IDEA Community Edition, if you want to make life easier on yourself. Technically you can develop with whatever tool you prefer for Web, but the project is already set up for JetBrains.

2. Make sure you have Node.js installed for NPM. You can acquire it [here](https://nodejs.org/en/).

3. The all-important

	```npm install```

	Run this in the command line from the root directory, 

    ```RAFinder\Web\RAFinder```

    This will install all of the necessary dependencies for the project, including AngularJS itself and a few goodies to make development easier.

4. It is recommended to install grunt, flatten-packages, and firebase-tools globally with the -g flag. If you do not do this, these commands (grunt, flatten-packages, firebase) should be prefaced by ```.\node_modules\.bin\```.

5. If developing on Windows, I recommend flattening your node modules. Windows can get cranky otherwise because of path lengths. This is a one-time operation per computer or added top-level dependency.

  1. Run ```flatten-packages``` from the root directory.
  2. JSCS will complain that it can't find things, as it doesn't like having its dependencies flattened. To fix this, run ```npm uninstall jscs jscs-angular```, then run ```npm install``` again.

6. To run the app. run ```npm start```, then navigate to http://localhost:8000/app/index.html. You should see a login screen welcoming you to RAFinder.

### Basics
This app can seem daunting at first. Here are a few tips:

1. The directory structure is _by module, **not** by file type_ as in traditional web development structures. Most of the many subdirectories of "app" actually only contain 1-3 smallish files.

2. As this is an angular-routed Single Page Application (SPA), All HTML files except for index.html are written as templates. This means that _you only need to write what would be within the body element, and links to any special CSS you'll need._

3. Corollary to (2). index.html should never need to be modified EXCEPT:
  - To add global stylesheets.
  - To add global scripts (i.e., new sources or dependencies).
  - To add or edit globally-visible HTML, such as the navbar.

4. Another corollary to (2). app.js should never need to be modified except under similar circumstances to (3).

5. The services are there for a reason. If you have an operation that involves authentication or the authenticated user's data, go through the auth module. If you're reading or writing data from the Database, go through the database module to get it. Environment-specific details? The environment module. Modals (i.e. popups within the page)? The modal module.

6. NPM, Bower, and Grunt are your friends. Let them do the heavy lifting for you, and just focus on what you need to know for the things you're developing.

**If you don't care about the dependencies and want to dive straight in, scroll to "Road Map". However, I recommend that you at least skim "NPM Scripts" and "Grunt Tasks", as you will need them later.**

### Dependencies
The core dependencies of this project are as follows, and can be found in ```package.json``` and ```bower.json```:

#### Development Dependencies

- ```bower``` is used to install front-end tools, mostly angular tools and firebase/angularfire.

- ```browserify``` is used to inject nodeJS dependencies into angular modules. This is used exclusively for the FileReader module, discussed later.

- ```firebase-tools``` is the Firebase command-line client, used for deploying the app to Firebase hosting.

- ```flatten-packages``` flattens your "node\_modules" folder so Windows doesn't complain about path lengths. Basically, it moves all dependencies to the top level.

- ```grunt``` and ```grunt-*``` are used to run just that. Grunt is a build tool that helps remove the headache from complicated builds such as those with multiple environments. It will be discussed in more detail later.

- ```jscs``` and ```jscs-angular``` are part of the "JavaScript Code Style checker". Basically a style guide that can integrate into WebStorm and provide style hints in the editor.

#### NPM Production Dependencies

- ```csv-parser``` does exactly what it says: parse CSV. This is a NodeJS dependency used with FileReader.

- ```lodash``` is incredibly important for a lot of things in NodeJS. It is installed as an explicit dependency to resolve compatibility issues I was having.

- ```stream``` is another NodeJS dependency for FileReader, which allows me to stream the file line-by-line in the browser the same way I would in a NodeJS backend.

- ```Rosefire``` is not listed in package.json, as it is not on NPM. It is used for authenticating with Rose-Hulman's KERBEROS system and using that login token to log into the Firebase database.

#### Bower Components

- ```angular``` AngularJS itself.

- ```angular-animate``` From https://docs.angularjs.org/api/ngAnimate: "The ngAnimate module provides support for CSS-based animations (keyframes and transitions) as well as JavaScript-based animations via callback hooks."

- ```angular-bootstrap``` A set of directives built by the Angular team to integrate Bootstrap styling and functionality with AngularJS. [See here](https://angular-ui.github.io/bootstrap/).

- ```angular-route``` provides routing (url/paths/to/things) for single-page applications.

- ```angularfire``` provides an AngularJS API for Firebase. [See here](https://www.firebase.com/docs/web/libraries/angular/quickstart.html).

- ```bootstrap``` "is the most popular HTML, CSS, and JS framework for developing responsive, mobile first projects on the web." More to the point, it's a BUNCH of CSS classes and directives to make your web page look pretty. [See here](http://getbootstrap.com/).

- ```firebase``` is the base NodeJS API for Firebase. [See here](https://www.firebase.com/docs/web/quickstart.html).

- ```html5-boilerplate``` is exactly what it sounds like, and more. Basically adds a bunch of useful things to make HTML less stone-agey, [as seen here](https://html5boilerplate.com/).

- ```jquery``` allows you to manipulate DOM elements (HTML snippets) from JavaScript code.

- ```angular-hotkeys``` adds super-simple hotkey support. Currently this application only uses it for quick login, but it can be easily expanded to a wide range of functions.

### NPM Scripts
While we're on the subject of NPM, we'll talk about the scripts that are in place.

#### Internal Scripts
These shouldn't be called by the user.

- ```test``` is currently an empty shell, as I have not constructed unit tests as yet. Once those are implemented, this would run the command to trigger them.

- ```postinstall``` runs after a call to ```npm install``` and installs bower dependencies.

- ```prestart``` runs *before* the start command when executing ```npm start```, and runs an NPM install.

#### External scripts
These should be called by the user.

- ```start``` starts the app, leveraging a grunt task to automatically run within the dev environment. Can be called directly with ```npm start```.

- ```start-prod``` starts the app in the production environment. Must be called with ```npm run-script start-prod```.

- ```deploy-dev``` deploys the app to Firebase hosting in the dev environment, at https://ra-finder-dev.firebaseapp.com. Must be called with ```npm run-script deploy-dev```.

- ```deploy-prod``` is the same as ```deploy-dev```, but deploys to the production environment at https://ra-finder.firebaseapp.com. Must be called with ```npm run-script deploy-prod```.

### Grunt Tasks
Grunt also has a set of scripts to work with, called tasks. You can see the developer-defined tasks in Gruntfile.js, as well as the configs for them, but here are the basics:

- ```grunt dev``` runs the app in the development environment. ```npm start``` is just a shortcut to this. This task leverages:
  - ```ngConstant:dev``` to set up the environment variables
  - ```connect:server``` to set up and serve the app on localhost
  - ```watch:app``` to watch files for changes and automatically update the server when changes are saved to files being served

- ```grunt prod``` is very similar to ```grunt dev```, but sets up the environment variables for the production environment.

- ```grunt browserify``` runs the browserify task for FileReader as mentioned above.

### Roadmap
Now for the interesting part: walking through the code. This part is organized just like the project files.

A note on the project hierarchy: it's not structured like a traditional Web Application, with js in one directory and html in another and css in a third. It's organized by modules, which I find more intuitive as well as quicker and easier to work with. What little CSS I've had to write myself is stored in a single file.

** *RAFinder* **. The root of the project.

- ** *app* **. The public directory that will be deployed and contains all sources.

  - ** *bower_components* **. Bower's version of node\_modules. Can be safely ignored.
  - ** *duty_roster* **. Contains sources for Duty Roster-related views and operations.
  - ** *employees* **. Contains sources for employee-related views and operations.
  - ** *hall_roster* **. Contains sources for Hall Roster-related views and operations. Sensing a pattern?
  - ** *login* **. Contains sources for Login-related views and operations, as well as a CSS file with definitions borrowed from bootstrap and used exclusively for sign-in views.
  - ** *services* **. Throwing you a curveball. This directory has even more directories! It contains all service modules.
    - ** *auth* **. Contains a facade service for authenticating a user and maintaining data about the authenticated user. If you need to get information about the authenticated user, _do so through this service_.

    - ** *database* **. Contains a facade service for all database operations. If you're doing something with the data stored in the database, _do so through this service_.

    - ** *environment* **. Contains a service from which to access various environment configurations. If you need access to something that's unique to a particular environment (dev or prod, for example), _do so through this service_.

    - ** *file_reader* **. This one is interesting, as it contains two source files. _fileReader.js is the file where changes should be made,_ and you should run ```grunt browserify``` after making these changes so that they compile into fileReader.browserify.js and actually appear in the app.

    - ** *modal* **. Contains a service for constructing and showing modal views. If you want to have a nice-looking popup that is easy to use the result of, use this service.
  - **app.css**. Contains any custom CSS that I have written. If you have a different style for how you structure your CSS files, go for it.
  - **app.js**. This script gets the ball rolling. Sets up dependencies for the other modules, the default route, a controller for the navbar, and a directive for a file chooser.
  - **config.js**. A separate file for declaring environment variables defined by grunt-ng-constant. _Changes to this file are ignored by git_.
  - **favicon.ico**. The little icon that shows up in the address bar/favorites/bookmarks. This is in a couple of places; I don't remember which is correct.
  - **index.html**. The root page for the app. All other views are HTML templates, and are loaded dynamically by the router into the element with the ```ng-view``` attribute.
- ** *dist* **. Future home of minified sources (using grunt), and future deployment directory.

- ** *node_modules* **. The library root for all node modules.

- **.bowerrc**. A JSON file specifying configurations for bower. All it has is a pointer to the app's bower install directory.

- **.jshintrc**. Like .bowerrc, it is a configuration file. Currently set to "use strict" globally and ignore things JSHint doesn't know exist, like the angular keyword and unit testing keywords.

- **bower.json**. Bower's package file. The bower equivalent to package.json.

- **favicon.ico**. The little icon that shows up in the address bar/favorites/bookmarks. This is in a couple of places; I don't remember which is correct.

- **firebase.json**. A configuration file for the firebase command-line tools. Specifies the database to use by default, the public folder to deploy, and folders to ignore when deploying.

- **Gruntfile.js**. As noted above, this file configures grunt and the tasks available to it.

- **jscs.json**. Configuration file for JSCS. Allows detailed configuration of nearly every inspection.

- **LICENSE**. The license file. Standard auto-generated MIT license.

- **package.json**. NPM's package file, and the primary package file for the app as a whole. Specifies name, version, description, dependencies, scripts, repository, author, license...

- **RAFinder.iml**. JetBrains' config file for the project. Don't mess with it.

- **webDevREADME.md**. Do you want an infinite recursion error? Because this is how you get an infinite recursion error.

### Closing Remarks
Whew! You made it! I know that was a lot of explanation, but it's better than leaving you floundering. Of course, that assumes you actually read the instructions (given that you're most likely a CSSE student or prof, I'll assume you did).

##### If you are, for any reason, still floundering after reading this README, or have problems with anything, feel free to contact me at sean47933@gmail.com.