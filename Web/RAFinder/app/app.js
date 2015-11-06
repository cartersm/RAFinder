'use strict';

// Declare app level module which depends on views, and components
angular.module('RAFinder', [
    'ngRoute',
    'RAFinder.home',
    'RAFinder.register',
    'RAFinder.employees'
])
    .config(['$routeProvider', function ($routeProvider) {

        // Home Page
        $routeProvider.otherwise({redirectTo: '/home'});
    }])
    .controller('BlogNavCtrl', ['$scope', '$location',
        function ($scope, $location) {
            $scope.isActive = function (viewLocation) {
                return viewLocation === $location.path();
            }
        }
    ]);