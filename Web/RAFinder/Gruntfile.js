module.exports = function (grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        watch: {
            dev: {
                files: ['Gruntfile.js', 'app/**/*.js', 'app/**/*.html'],
                options: {
                    atBegin: true
                }
            },
            dist: {
                files: ['dist/<%= pkg.name %>.min.js', 'dist/**/*.html']
            }
        },
        connect: {
            server: {
                options: {
                    hostname: 'localhost',
                    port: 8000
                }
            }
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
        copy: {
            rosefire: {
                files: [
                    // includes files within path and its sub-directories
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
                        expand: true,
                        src: 'app/**/*.js',
                        dest: 'intermediate/src',
                        noProcess: 'app/services/file_reader/fileReader.js'
                    }
                ]
            },
            html: {
                files: [
                    {
                        expand: true,
                        cwd: 'app',
                        src: '*/**/*.html',
                        dest: 'intermediate/html',
                        filter: 'isFile'
                    }
                ]
            },
            dist_html: {
                files: [
                    {
                        expand: true,
                        cwd: 'intermediate/html',
                        src: '**/*.html',
                        dest: 'dist',
                        filter: 'isFile'
                    }
                ]
            }
        },
        jshint: {
            options: {
                force: false,
                ignores: 'app/services/file_reader/fileReader.browserify.js',
                jshintrc: true

            },
            beforeconcat: ['app/**/*.js']
        },
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
        concat: {
            source: {
                src: ['app/**/*.js', '!app/services/file_reader/fileReader.js'],
                dest: 'intermediate/<%= pkg.name %>.js'

            },
            libs: {
                src: ['intermediate/libs/**/*.js'],
                dest: 'intermediate/libs.js'
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
        cssmin: {
            dist: {
                files: {
                    'dist/app.min.css': ['app/**/*.css', 'intermediate/css/**/*.css']
                }
            }
        },
        clean: {
            build: {
                src: ['intermediate']
            },
            bootstrap: {
                src: ['bower_components/bootstrap/dist']
            }
        },
        unzip: {
            'bower_components/bootstrap': 'bootstrap.zip'
        }
    });
    grunt.loadNpmTasks('grunt-contrib-connect');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-ng-constant');
    grunt.loadNpmTasks('grunt-browserify');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-bowercopy');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-zip');

    grunt.registerTask('setup', [
        'clean:bootstrap',
        'unzip'
    ]);

    grunt.registerTask('dev', [
        'copy:rosefire',
        'ngconstant:dev',
        'jshint',
        'connect:server',
        'watch:dev'
    ]);

    grunt.registerTask('prod', [
        'copy:rosefire',
        'ngconstant:prod',
        'jshint',
        'connect:server',
        'watch:dev'
    ]);

    grunt.registerTask('dist_test', [
        'copy:rosefire',
        'ngconstant:dev',
        'build',
        'connect:server',
        'watch:dist'
    ]);

    grunt.registerTask('browserify', [
        'browserify:main'
    ]);

    grunt.registerTask('build', [
        // 'jshint',
        'bowercopy',
        'copy:source',
        'copy:html',
        'concat',
        'uglify',
        'cssmin',
        'copy:dist_html',
        'clean:build'
    ]);

    grunt.registerTask('dev-deploy', [
        'copy:rosefire',
        'ngconstant:dev',
        'build'
    ]);

    grunt.registerTask('prod-deploy', [
        'copy:rosefire',
        'ngconstant:prod',
        'build'
    ]);
};
