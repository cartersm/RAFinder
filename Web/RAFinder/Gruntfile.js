module.exports = function (grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        watch: {
            app: {
                files: ['app/*.js', 'app/*.html']
            },
            dev: {
                files: ['Gruntfile.js', 'app/*.js', 'app/*.html'],
                options: {
                    atBegin: true
                }
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
        }
    });
    grunt.loadNpmTasks('grunt-contrib-connect');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-ng-constant');
    grunt.loadNpmTasks('grunt-browserify');

    grunt.registerTask('dev', [
        'ngconstant:dev',
        'connect:server',
        'watch:app'
    ]);
    grunt.registerTask('prod', [
        'ngconstant:prod',
        'connect:server',
        'watch:app'
    ]);
    grunt.registerTask('browserify', [
        'browserify:main'
    ]);
};
