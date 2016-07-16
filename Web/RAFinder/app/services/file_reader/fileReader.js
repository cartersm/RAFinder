'use strict';
angular.module('RAFinder.services.fileReader', [])
    .service('FileReader', [
        function () {
            /**
             * Reads the given string of file data and parses it as CSV into a JSON object.
             * Calls the given dataCallback on each line's worth of data.
             * Calls the given endCallback when all data has been read.
             */
            this.readCsv = function (data, dataCallback, endCallback) {
                var stream = require('stream');
                var csv = require('csv-parser');
                var s = new stream.Readable();
                s._read = function noop() {
                };
                s.push(data);

                s.push(null);
                s.pipe(csv())
                    .on('data', dataCallback)
                    .once('end', endCallback);
            };

            this.readLines = function (data, dataCallback, endCallback) {
                var lines;
                if (data.indexOf('\r\n') >= 0) {
                    lines = data.split('\r\n');
                } else if (data.indexOf('\n') >= 0) {
                    lines = data.split('\n');
                } else {
                    lines = data.split('\r');
                }
                angular.forEach(lines, function (line) {
                    if (dataCallback) {
                        dataCallback(line);
                    }
                });
                if (endCallback) {
                    endCallback();
                }
            };
        }
    ]);
