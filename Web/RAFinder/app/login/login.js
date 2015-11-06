'use strict';

angular.module('RAFinder.home', [
    'ngRoute',
    "firebase"
])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/home', {
            templateUrl: 'login/login.html',
            controller: 'HomeCtrl'
        });
    }])
    .controller('HomeCtrl', ["$scope", "$firebaseAuth", "$location", "CommonProp",
        function ($scope, $firebaseAuth, $location, CommonProp) {
            var firebase = new Firebase("https://ra-finder.firebaseio.com");
            var authObj = $firebaseAuth(firebase);
            $scope.user = {};

            if (authObj.$getAuth() !== null) {
                $location.path("employees");
            }

            $scope.SignIn = function (event) {
                event.preventDefault();
                var username = $scope.user.email;
                var password = $scope.user.password;

                authObj.$authWithPassword({
                    email: username,
                    password: password
                }).then(function (authData) {
                    console.log("Logged in as: " + authData.uid);
                    $location.path("employees");
                    CommonProp.setUser($scope.user.email);
                }).catch(function (error) {
                    console.error("Authentication failed: ", error);
                });

            }
        }])
    .service('CommonProp', ["$location", "$firebaseAuth", function ($location, $firebaseAuth) {
        var user = "";
        var firebase = new Firebase("https://ra-finder.firebaseio.com");
        var authObj = $firebaseAuth(firebase);

        return {
            getUser: function () {
                return user;
            },
            setUser: function (value) {
                user = value;
            },
            logoutUser: function () {
                authObj.$unauth();
                console.log("Logout complete");
                $location.path("/home");
            }
        }
    }]);