angular.module('RAFinder.services.fileReader', [])
    .service('FileReader', [
        function () {
            /**
             * Reads the given string of file data and parses it as CSV into a JSON object.
             * Calls the given dataCallback on each line's worth of data.
             * Calls the given endCallback when all data has been read.
             */
            this.readFile = function (data, dataCallback, endCallback) {
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
        }
    ]);
