'use strict';
/**
 * Controls the HalLRoster view.
 */
angular.module('RAFinder.hallRoster', [
    'ngRoute',
    'firebase'
])
    .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.when('/hallRoster', {
                templateUrl: 'hall_roster/hallRoster.html',
                controller: 'HallRosterCtrl'
            });
        }
    ])
    .controller('HallRosterCtrl', [
        '$scope',
        '$location',
        'Auth',
        'Database',
        function ($scope, $location, Auth, Database) {
            $scope.isLoading = true;
            Auth.checkAuth(function () {
                if (!Auth.isEmployee()) {
                    $location.path('/login');
                }
            });

            // CSV upload
            $scope.onCsvLoaded = function () {
                Database.parseHallRosterCsv($scope.file.data);
            };

            $scope.onCsvError = function (error) {
                console.error('problem uploading CSV', error);
                // TODO: show an error (toast?) to the user
            };

            // Populate Hall Data
            Database.getResHalls(function (data) {
                    $scope.hallData = data;
                    $scope.isLoading = false;
                });

            $scope.getResidents = function (room) {
                var residents = [];
                angular.forEach(room, function (value, key) {
                    if (key === '$id' || key === 'number') return;
                    residents.push(key);
                });
                return residents;
            };

            $scope.isAdmin = function () {
                return Auth.isAdmin();
            };
        }
    ]);
