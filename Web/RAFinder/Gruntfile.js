module.exports = function (grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        bowercopy: {
            options: {
                runBower: false
            },
            libs: {
                options: {
                    destPrefix: 'intermediate/libs'
                },
                files: {
                    'angular.js': 'angular/angular.js',
                    'rosefire-angular.min.js': 'rosefire-js-sdk/dist/js/rosefire-angular.min.js',
                    'angular-route.js': 'angular-route/angular-route.js',
                    'firebase.js': 'firebase/firebase.js',
                    'angularfire.min.js': 'angularfire/dist/angularfire.min.js',
                    'jquery.min.js': 'jquery/dist/jquery.min.js',
                    'bootstrap.min.js': 'bootstrap/dist/js/bootstrap.min.js',
                    'angular-animate.js': 'angular-animate/angular-animate.js',
                    'ui-bootstrap.min.js': 'angular-bootstrap/ui-bootstrap.min.js',
                    'ui-bootstrap-tpls.min.js': 'angular-bootstrap/ui-bootstrap-tpls.min.js',
                    'hotkeys.min.js': 'angular-hotkeys/build/hotkeys.min.js',
                    'modernizr-2.8.3.min.js': 'html5-boilerplate/dist/js/vendor/modernizr-2.8.3.min.js'
                }
            },
            fonts: {
                options: {
                    destPrefix: 'dist'
                },
                files: {
                    'fonts': 'bootstrap/fonts'
                }
            },
            css: {
                options: {
                    destPrefix: 'intermediate/css'
                },
                files: {
                    'html5/normalize.css': 'html5-boilerplate/dist/css/normalize.css',
                    'html5/main.css': 'html5-boilerplate/dist/css/main.css',
                    'bootstrap.min.css': 'bootstrap/dist/css/bootstrap.css',
                    'ui-bootstrap-csp.css': 'angular-bootstrap/ui-bootstrap-csp.css'
                }
            }
        },
        browserify: {
            main: {
                src: './app/services/file_reader/fileReader.js',
                dest: './app/services/file_reader/fileReader.browserify.js',
                options: {
                    banner: '// Source changes should be made in fileReader.js, then compiled with \'grunt browserify\'.',
                    require: ['stream', 'csv-parser']
                }
            }
        },
        clean: {
            build: {
                src: ['intermediate']
            },
            bootstrap: {
                src: ['bower_components/bootstrap/dist']
            },
            dist: {
                src: ['dist']
            }
        },
        concat: {
            source: {
                src: ['intermediate/src/**/*.js', '!intermediate/src/fileReader.browserify.js'],
                dest: 'intermediate/<%= pkg.name %>.js'
            },
            libs: {
                // explicitly source libraries to maintain order (some deps have deps higher in the list)
                src: [
                    'intermediate/libs/angular.js',
                    'intermediate/libs/rosefire-angular.min.js',
                    'intermediate/libs/angular-route.js',
                    'intermediate/libs/firebase.js',
                    'intermediate/libs/angularfire.min.js',
                    'intermediate/libs/jquery.min.js',
                    'intermediate/libs/bootstrap.min.js',
                    'intermediate/libs/angular-animate.js',
                    'intermediate/libs/ui-bootstrap.min.js',
                    'intermediate/libs/ui-bootstrap-tpls.min.js',
                    'intermediate/libs/hotkeys.min.js'
                ],
                dest: 'intermediate/libs.js'
            }
        },
        connect: {
            dev: {
                options: {
                    hostname: 'localhost',
                    port: 8000
                }
            },
            dist: {
                options: {
                    hostname: 'localhost',
                    port: 8000,
                    base: 'dist'
                }
            }
        },
        copy: {
            rosefire: {
                files: [
                    {
                        expand: true,
                        cwd: 'node_modules/rosefire-js-sdk/',
                        src: ['**'],
                        dest: 'bower_components/rosefire-js-sdk'
                    }
                ]
            },
            source: {
                files: [
                    {
                        // source js
                        expand: true,
                        src: ['app/**/*.js', '!app/services/file_reader/fileReader.js'],
                        dest: 'intermediate/src',
                        filter: 'isFile',
                        flatten: true
                    },
                    {
                        // source html (excluding index.html)
                        expand: true,
                        cwd: 'app',
                        src: '*/**/*.html',
                        dest: 'intermediate/html',
                        filter: 'isFile'
                    },
                    {
                        // index.html
                        expand: true,
                        cwd: 'app',
                        src: 'index.html',
                        dest: 'intermediate/html'
                    }
                ],
                options: {
                    process: function (content, srcPath) {
                        if (srcPath.includes('login')) {
                            return content.replace('bootstrap-signin.css', 'bootstrap-signin.min.css')
                        } else if (srcPath.includes('index.html')) {
                            return content
                                .replace(/<!-- Modernizr -->[\s\S]*?<!-- End Modernizr -->/, '<script src="modernizr-2.8.3.min.js"></script>')
                                .replace(/<!-- CSS -->[\s\S]*?<!-- End CSS -->/, '<link rel="stylesheet" href="app.min.css">')
                                .replace(/<!-- Libraries -->[\s\S]*?<!-- End Libraries -->/, '<script src="libs.min.js"></script>')
                                .replace(/<!-- Sources -->[\s\S]*?<!-- End Sources -->/, '<script src="ra-finder.min.js"></script>\n<script src=fileReader.browserify.js></script>');
                        }
                        return content;
                    }
                }
            },
            dist: {
                files: [
                    {
                        src: 'intermediate/src/fileReader.browserify.js',
                        dest: 'dist/fileReader.browserify.js'
                    },
                    {
                        expand: true,
                        cwd: 'intermediate/html',
                        src: '**/*.html',
                        dest: 'dist',
                        filter: 'isFile'
                    },
                    {
                        src: 'intermediate/libs/modernizr-2.8.3.min.js',
                        dest: 'dist/modernizr-2.8.3.min.js'
                    },
                    {
                        cwd: 'app',
                        src: 'favicon.ico',
                        dest: 'dist/favicon.ico'
                    }
                ]
            }
        },
        cssmin: {
            dist: {
                files: {
                    'dist/app.min.css': ['intermediate/css/**/*.css', 'app/app.css'],
                    'dist/bootstrap-signin.min.css': ['app/bootstrap-signin.css']
                }
            }
        },
        htmlmin: {
            dist: {
                options: {
                    removeComments: true,
                    collapseWhitespace: true,
                    conservativeCollapse: true,
                    collapseBooleanAttributes: true,
                    quoteCharacter: '"'
                },
                files: [
                    {
                        expand: true,
                        cwd: 'intermediate/html',
                        src: ['**/*.html'],
                        dest: 'dist'
                    }
                ]
            }
        },
        jshint: {
            options: {
                force: false,
                ignores: ['app/services/file_reader/fileReader.browserify.js', 'app/config.js'],
                jshintrc: true

            },
            beforeconcat: ['app/**/*.js']
        },
        ngconstant: {
            options: {
                name: 'RAFinder.config',
                dest: './app/config.js',
                wrap: '\'use strict\';\n\n{%= __ngModule %}',
                space: '    '
            },
            dev: {
                constants: {
                    ENV: 'dev',
                    REGISTRY_TOKEN: '931baaeed4ddf12032ed98e45edc2f769ba8be1874e2c062f58437e42ae848997a76' +
                    'f1d4ecfeb10e45a6b5cf422d87f7xrEHABH6J/MMf5QA3sxjZChYEuY90FpsAgFK5MgM' +
                    'a0vqD4Q/nNzPrzmaoYNGkRZDPTnlXdn0gE206vrqVkltEw=='

                }
            },
            prod: {
                options: {
                    name: 'RAFinder.config',
                    dest: './app/config.js',
                    wrap: '\'use strict\';\n\n{%= __ngModule %}',
                    space: '    '
                },
                constants: {
                    ENV: 'prod',
                    REGISTRY_TOKEN: 'bf2f8ab7c101995e3ae8669d1dd467592dec77d1158673685f9b0ba2efbe6ba55081ba228ff365d' +
                    'fa68f9beeba5fd196adXg/W9RjB0YrQHQHIw3dCOyVQXrXrboe7TEqu/6U8C1U4J9wnGQa0BFW+X3duaugCr2TybsIt42XZ' +
                    'SjZmkvgQ=='
                }
            }
        },
        uglify: {
            options: {
                mangle: false
            },
            dist: {
                files: {
                    'dist/<%= pkg.name %>.min.js': ['intermediate/<%= pkg.name %>.js'],
                    'dist/libs.min.js': ['intermediate/libs.js']
                }
            }
        },
        watch: {
            dev: {
                files: ['Gruntfile.js', 'app/**/*.js', 'app/**/*.html'],
                options: {
                    atBegin: true
                }
            },
            dist: {
                files: ['dist/<%= pkg.name %>.min.js', 'dist/**/*.html', 'Gruntfile.js']
            }
        },
        unzip: {
            'bower_components/bootstrap': 'bootstrap.zip'
        }
    });
    grunt.loadNpmTasks('grunt-bowercopy');
    grunt.loadNpmTasks('grunt-browserify');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-connect');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-htmlmin');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-ng-constant');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-zip');

    // one-time setup to install custom bootstrap
    grunt.registerTask('setup', [
        'clean:bootstrap',
        'unzip'
    ]);

    // run development build locally
    grunt.registerTask('dev', [
        'copy:rosefire',
        'ngconstant:dev',
        'jshint',
        'connect:dev',
        'watch:dev'
    ]);

    // run production build locally
    grunt.registerTask('prod', [
        'copy:rosefire',
        'ngconstant:prod',
        'jshint',
        'connect:dev',
        'watch:dev'
    ]);

    // run development distribution build locally
    // use before deploying if you changed any of the grunt tasks
    grunt.registerTask('dist', [
        'copy:rosefire',
        'ngconstant:dev',
        'build',
        'connect:dist',
        'watch:dist'
    ]);

    // browserify fileReader.js
    // CONSIDER: this seems to be broken on Win10 or with some part of the local build.
    // if it hangs, use ```browserify app/services/file_reader/fileReader.js -o app/services/file_reader/fileReader.browserify.js``` from the command line
    grunt.registerTask('browserify', [
        'browserify:main'
    ]);

    // build the distribution version of the app
    grunt.registerTask('build', [
        'bowercopy',
        'copy:source',
        'concat',
        'uglify',
        'cssmin',
        'htmlmin',
        'copy:dist',
        'clean:build'
    ]);

    // prep the dev distribution for deployment
    // don't run this on its own. Use ```npm run-script deploy-dev```
    grunt.registerTask('dev-deploy', [
        'copy:rosefire',
        'ngconstant:dev',
        'build'
    ]);

    // prep the prod distribution for deployment
    // don't run this on its own. Use ```npm run-script deploy-prod```
    grunt.registerTask('prod-deploy', [
        'copy:rosefire',
        'ngconstant:prod',
        'build'
    ]);

    // run ```grunt jshint``` on its own to lint the app directory
    // run ```grunt clean:dist``` to clean out the dist directory
};
