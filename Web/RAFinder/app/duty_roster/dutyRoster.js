angular.module('RAFinder.dutyRoster', [
    'ngRoute',
    'firebase'
])
    .config(["$routeProvider",
        function ($routeProvider) {
            $routeProvider.when("/dutyRoster", {
                templateUrl: "duty_roster/dutyRoster.html",
                controller: "DutyRosterCtrl"
            });
        }
    ])
    .controller("DutyRosterCtrl", ["$scope", '$location', '$firebaseAuth', '$firebaseObject', '$firebaseArray', 'CommonProp',
        function ($scope, $location, $firebaseAuth, $firebaseObject, $firebaseArray, CommonProp) {
            var firebase = new Firebase("https://ra-finder.firebaseio.com");
            var authObj = $firebaseAuth(firebase);

            // check auth    TODO: find out whether this can be moved to app.js
            if (authObj === null || authObj.$getAuth() === null) {
                $location.path("login");
            }
            CommonProp.setUser(authObj.$getAuth().password.email);
            $scope.username = CommonProp.getUser();

            $scope.rosterData = [];

            $firebaseArray(firebase.child("DutyRosters"))
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
                        if (key2.startsWith("$")) return;

                        foo.entries.push({
                            hall: key2,
                            employee: {
                                name: value2.name,
                                email: value2.email,
                                phoneNumber: value2.phoneNumber
                            }
                        });
                    });
                    console.log(foo);
                    retArray.push(foo);
                });
                return retArray;
            };

            $scope.getDate = function (dateString) {
                return new Date(dateString);
            };
        }
    ]);
