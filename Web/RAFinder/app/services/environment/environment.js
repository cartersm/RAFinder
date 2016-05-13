angular.module('RAFinder.services.environment', [])
    .factory('EnvConfig', ['ENV', "REGISTRY_TOKEN", function (ENV, REGISTRY_TOKEN) {
        var dev = {
            'env': 'dev',
            'url': 'https://ra-finder-dev.firebaseio.com',
            'token': REGISTRY_TOKEN
        };
        var prod = {
            'env': 'prod',
            'url': 'https://ra-finder.firebaseio.com',
            'token': REGISTRY_TOKEN
        };

        var configs = {'dev': dev, 'prod': prod};
        return configs[ENV];
    }]);
