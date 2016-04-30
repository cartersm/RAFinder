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
                wrap: '"use strict";\n\n{%= __ngModule %}',
                space: '    '
            },
            dev: {
                constants: {
                    ENV: 'dev'
                }
            },
            prod: {
                options: {
                    name: 'RAFinder.config',
                    dest: './app/config.js',
                    wrap: '"use strict";\n\n{%= __ngModule %}',
                    space: '    '
                },
                constants: {
                    ENV: 'prod'
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
