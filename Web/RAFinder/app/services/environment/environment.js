angular.module('RAFinder.services.environment', [])
    .factory('EnvConfig', ['ENV', function (ENV) {
        var dev = {
            'url': 'https://ra-finder-dev.firebaseio.com'
        };
        var prod = {
            'url': 'https://ra-finder.firebaseio.com'
        };

        var configs = {'dev': dev, 'prod': prod};
        return configs[ENV];
    }]);
