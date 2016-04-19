'use strict';

angular.module('RAFinder', [
        'ngRoute',
        'ui.bootstrap',
        'cfp.hotkeys',
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
    /* borrowed from http://stackoverflow.com/a/34001254 */
    .directive('fileSelect', ['$window', function ($window) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, el, attr, ctrl) {
                var fileReader = new $window.FileReader();

                fileReader.onload = function () {
                    ctrl.$setViewValue(fileReader.result);

                    if ('fileLoaded' in attr) {
                        scope.$eval(attr.fileLoaded);
                    }
                };

                fileReader.onprogress = function (event) {
                    if ('fileProgress' in attr) {
                        scope.$eval(attr.fileProgress, {'$total': event.total, '$loaded': event.loaded});
                    }
                };

                fileReader.onerror = function () {
                    if ('fileError' in attr) {
                        scope.$eval(attr.fileError, {'$error': fileReader.error});
                    }
                };

                var fileType = attr.fileSelect;

                el.bind('change', function (e) {
                    var fileName = e.target.files[0];

                    fileReader.readAsText(fileName);
                });
            }
        };
    }]);
