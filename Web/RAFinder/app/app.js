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
    .controller('BlogNavCtrl', ['$scope', '$location', "CommonProp",
        function ($scope, $location, CommonProp) {
            $scope.isActive = function (viewLocation) {
                return viewLocation === $location.path();
            };

            $scope.logout = function () {
                CommonProp.logoutUser();
            };

            $scope.hasAuth = function () {
                var isAuthed = !(CommonProp.getUser() === '');
                if (isAuthed) {
                    // the navbar is forcibly hidden by default so that
                    //     it doesn't appear on initial load.
                    // Remove the 'hidden' class so ngShow can take over.
                    $("#primary-navbar").removeClass("hidden");
                }
                return isAuthed;
            }
        }
    ]);