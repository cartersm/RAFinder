'use strict';

angular.module('RAFinder', [
    'ngRoute',
    'ui.bootstrap',
    'RAFinder.services.auth',
    'RAFinder.services.modal',
    'RAFinder.services.database',
    'RAFinder.login',
    'RAFinder.employees',
    'RAFinder.hallRoster',
    'RAFinder.dutyRoster'
])
    .config(['$routeProvider',
        function ($routeProvider) {
            // Always redirect to login page
            // Login will forward to home page if logged in
            $routeProvider.otherwise({redirectTo: '/login'});
        }
    ])
    .controller('RootCtrl', [
        '$scope',
        '$location',
        'Auth',
        function ($scope, $location, Auth) {
            $scope.isActive = function (viewLocation) {
                return viewLocation === $location.path();
            };

            if ($location.path() !== '/employees') {
                $('#default-link').removeClass('active');
            }

            Auth.checkAuth(function () {
                // the navbar is forcibly hidden by default so that
                //     it doesn't appear on initial load.
                // Remove the 'hidden' class so ngShow can take over.
                $('#page-header').removeClass('hidden');
                $('#nav-bar').removeClass('hidden');
                $scope.username = Auth.getUser();
            });

            $scope.logout = function () {
                Auth.logoutUser();
            };

            $scope.hasAuth = function () {
                return Auth.isEmployee();
            };
        }
    ])
    /* Borrowed from http://www.tutorialspoint.com/angularjs/angularjs_upload_file.htm
     * For watching the value of a file input and parsing it
     */
    .directive('fileModel', ['$parse', function ($parse) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var model = $parse(attrs.fileModel);
                var modelSetter = model.assign;

                element.bind('change', function () {
                    scope.$apply(function () {
                        modelSetter(scope, element[0].files[0]);
                    });
                });
            }
        };
    }]);
