module.exports = function(grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        watch: {
            app: {
                files: ['app/*.js', 'app/*.html']
            },
            dev: {
                files: [ 'Gruntfile.js', 'app/*.js', 'app/*.html'],
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
        }
    });
    grunt.loadNpmTasks('grunt-contrib-connect');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-ng-constant');

    grunt.registerTask('dev', ['ngconstant:dev', 'connect:server', 'watch:app']);
    grunt.registerTask('prod', ['ngconstant:prod', 'connect:server', 'watch:app']);
    // CONSIDER whether we can do firebase here
};