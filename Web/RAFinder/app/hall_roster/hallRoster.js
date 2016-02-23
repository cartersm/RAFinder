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
        }
    ]);
