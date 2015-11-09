'use strict';

angular.module('RAFinder', [
    'ngRoute',
    'RAFinder.login',
    'RAFinder.register',
    'RAFinder.employees'
])
    .config(['$routeProvider',
        function ($routeProvider) {
            // Home Page
            $routeProvider.otherwise({redirectTo: '/login'});
        }])
    .controller('BlogNavCtrl', ['$scope', '$location', "CommonProp",
        function ($scope, $location, CommonProp) {
            $scope.isActive = function (viewLocation) {
                return viewLocation === $location.path();
            };

            $scope.logout = function () {
                CommonProp.logoutUser();
            };

            $scope.hasAuth = function () {
                return !(CommonProp.getUser() === '');
            }
        }
    ])
    .service('CommonProp', ["$firebaseAuth", "$window",
        function ($firebaseAuth, $window) {
            var user = "";
            var firebase = new Firebase("https://ra-finder.firebaseio.com");
            var authObj = $firebaseAuth(firebase);

            var auth = authObj.$getAuth();
            if (auth !== null) {
                this.user = auth.password.email;
            }

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
                    // Force reload to hide the navbar
                    $window.location.reload();
                }
            }
        }]);