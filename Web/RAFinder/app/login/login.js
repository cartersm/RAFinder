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
        '$firebaseAuth',
        '$location',
        '$window',
        'AuthService',
        function ($scope, $firebaseAuth, $location, $window, AuthService) {
            $scope.signinFailed = false;
            $scope.isEmployee = true;

            AuthService.checkAuth(function () {
                $window.location.reload();
                $location.path('/employees');
            });

            var authObj = AuthService.getAuthObject();

            $scope.SignIn = function (event) {
                event.preventDefault();
                var username = $scope.user.email;
                var password = $scope.user.password;

                authObj.$authWithPassword({
                    email: username,
                    password: password
                }).then(function (authData) {
                    AuthService.checkAuth(function () {
                        $scope.signinFailed = false;
                        $scope.isEmployee = AuthService.isEmployee();
                        if ($scope.isEmployee) {
                            AuthService.setUser(authData.password.email);
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

                }).catch(function (error) {
                    console.error('Authentication failed: ', error);
                    $scope.signinFailed = true;
                });
            };
        }
    ]);
