'use strict';

angular.module('RAFinder', [
    'ngRoute',
    'ui.bootstrap',
    'RAFinder.services',
    'RAFinder.login',
    'RAFinder.employees',
    "RAFinder.hallRoster",
    "RAFinder.dutyRoster"
])
    .config(['$routeProvider',
        function ($routeProvider) {
            // Always redirect to login page
            // Login will forward to home page if logged in
            $routeProvider.otherwise({redirectTo: '/login'});
        }
    ])
    .controller('RootCtrl', ['$scope', '$location', "AuthService",
        function ($scope, $location, AuthService) {
            $scope.isActive = function (viewLocation) {
                return viewLocation === $location.path();
            };

            if ($location.path() !== "/employees") {
                $("#default-link").removeClass("active");
            }

            AuthService.checkAuth(function () {
                // the navbar is forcibly hidden by default so that
                //     it doesn't appear on initial load.
                // Remove the 'hidden' class so ngShow can take over.
                $("#page-header").removeClass("hidden");
                $("#nav-bar").removeClass("hidden");
                $scope.username = AuthService.getUser();
            });

            $scope.logout = function () {
                AuthService.logoutUser();
            };

            $scope.hasAuth = function () {
                return AuthService.isEmployee();
            };
        }
    ]);