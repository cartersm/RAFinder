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
    .controller('HallRosterCtrl', ['$scope', '$location', '$firebaseAuth', '$firebaseObject', '$firebaseArray', 'AuthService',
        function ($scope, $location, $firebaseAuth, $firebaseObject, $firebaseArray, AuthService) {
            AuthService.checkAuth(function () {
                if (!AuthService.isEmployee()) {
                    $location.path('/login');
                }
            });

            var firebase = new Firebase('https://ra-finder.firebaseio.com');

            // Populate Hall Data
            var hallsRef = firebase.child('ResHalls');
            $firebaseArray(hallsRef)
                .$loaded()
                .then(function (data) {
                    $scope.hallData = data;
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
