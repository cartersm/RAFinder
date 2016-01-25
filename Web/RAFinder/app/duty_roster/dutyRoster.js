angular.module('RAFinder.dutyRoster', [
    'ngRoute',
    'firebase'
])
    .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.when('/dutyRoster', {
                templateUrl: 'duty_roster/dutyRoster.html',
                controller: 'DutyRosterCtrl'
            });
        }
    ])
    .controller('DutyRosterCtrl', [
        '$scope',
        '$location',
        '$firebaseAuth',
        '$firebaseObject',
        '$firebaseArray',
        'AuthService',
        function ($scope, $location, $firebaseAuth, $firebaseObject, $firebaseArray, AuthService) {
            AuthService.checkAuth(function () {
                if (!AuthService.isEmployee()) {
                    $location.path('/login');
                }
            });

            $scope.rosterData = [];
            var firebase = new Firebase('https://ra-finder.firebaseio.com');

            $firebaseArray(firebase.child('DutyRosters'))
                .$loaded()
                .then(function (data) {
                    $scope.rosterData = data;
                    $scope.rosters = $scope.getRosters();
                });

            $scope.getRosters = function () {
                var retArray = [];
                angular.forEach($scope.rosterData, function (value) {
                    var foo = {
                        key: value.$id,
                        entries: []
                    };
                    angular.forEach(value, function (value2, key2) {
                        if (key2.startsWith('$')) return;

                        foo.entries.push({
                            hall: key2,
                            employee: {
                                name: value2.name,
                                email: value2.email,
                                phoneNumber: value2.phoneNumber
                            }
                        });
                    });
                    retArray.push(foo);
                });
                return retArray;
            };

            $scope.getDate = function (dateString) {
                var date = new Date(dateString);
                var oneDayMs = 1000 * 60 * 60 * 24;
                // add one day to fix UTC offset
                date.setTime(date.getTime() + oneDayMs);
                return date;
            };
        }
    ]);
