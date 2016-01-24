'use strict';

angular.module('RAFinder.login', [
    'ngRoute',
    "firebase"
])
    .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.when('/login', {
                templateUrl: 'login/login.html',
                controller: 'LoginCtrl'
            });
        }])
    .controller('LoginCtrl', ["$scope", "$firebaseAuth", "$location", "AuthService",
        function ($scope, $firebaseAuth, $location, AuthService) {
            $scope.signinFailed = false;
            $scope.isEmployee = true;

            AuthService.checkAuth(function () {
                $location.path('/employees');
            });

            var firebase = new Firebase("https://ra-finder.firebaseio.com");
            var authObj = $firebaseAuth(firebase);
            $scope.user = {};

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
                            console.log("auth check successful");
                            $location.path("employees");
                        } else {
                            console.warn("auth check failure");
                        }
                    });

                }).catch(function (error) {
                    console.error("Authentication failed: ", error);
                    $scope.signinFailed = true;
                });

            };
        }
    ])
;