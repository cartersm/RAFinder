'use strict';
/**
 * A factory that returns the current execution environment of the app.
 */
angular.module('RAFinder.services.environment', [])
    .factory('EnvConfig', ['ENV', function (ENV) {
        var dev = {
            'env': 'dev',
            'url': 'https://ra-finder-dev.firebaseio.com'
        };
        var prod = {
            'env': 'prod',
            'url': 'https://ra-finder.firebaseio.com'
        };

        var configs = {'dev': dev, 'prod': prod};
        return configs[ENV];
    }]);
