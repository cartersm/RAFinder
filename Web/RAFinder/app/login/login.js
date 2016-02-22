'use strict';

angular.module('RAFinder.login', [
    'ngRoute',
    'firebase'
])
    .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.when('/login', {
                templateUrl: 'login/login.html',
                controller: 'LoginCtrl'
            });
        }])
    .controller('LoginCtrl', [
        '$scope',
        '$location',
        '$window',
        'Auth',
        function ($scope, $location, $window, Auth) {
            $scope.signinFailed = false;
            $scope.isEmployee = true;

            Auth.checkAuth(function () {
                $window.location.reload();
                $location.path('/employees');
            });

            $scope.SignIn = function (event) {
                event.preventDefault();
                var username = $scope.user.email;
                var password = $scope.user.password;

                Auth.auth(username, password,
                    function (err, authData) {
                        if (err) {
                            console.error('Authentication failed: ', err);
                            $scope.signinFailed = true;
                            return;
                        }
                        Auth.checkAuth(function () {
                            $scope.signinFailed = false;
                            $scope.isEmployee = Auth.isEmployee();
                            if ($scope.isEmployee) {
                                Auth.setUser(authData.password.email);
                                $('#page-header').removeClass('hidden');
                                $('#nav-bar').removeClass('hidden');
                                $window.location.reload();
                                $location.path('/employees');
                            } else {
                                console.warn('auth check failure');
                            }
                        }, function (error) {
                            console.warn(error);
                            $scope.isEmployee = false;
                        });
                    });
            };
        }
    ]);
