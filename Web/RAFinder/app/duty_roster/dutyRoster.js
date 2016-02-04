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
        'Auth',
        'Modal',
        'Database',
        function ($scope, $location, Auth, Modal, Database) {
            Auth.checkAuth(function () {
                if (!Auth.isEmployee()) {
                    $location.path('/login');
                }
            });

            $scope.rosterData = [];

            var loadRosters = function () {
                Database.getDutyRosters(function (data) {
                    $scope.rosterData = data;
                    $scope.rosters = $scope.getRosters();
                });
            };

            loadRosters();

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

            $scope.showAddDutyRosterItemModal = function () {
                var modalDefaults = {
                    templateUrl: 'duty_roster/addDutyRosterItem.html',
                    controller: 'AddDutyRosterItemCtrl'
                };
                var modalOptions = {
                    headerText: 'Add a Duty Roster item',
                    actionButtonText: 'Add Item',
                    closeButtonText: 'Cancel'
                };

                Modal.showModal(modalDefaults, modalOptions)
                    .then(function (successResult) {
                        console.log(successResult);
                        $scope.addDutyRosterItem(successResult.date, successResult.roster);
                        loadRosters();
                    });
            };

            $scope.canAddDutyRosterItem = function () {
                return Auth.isAdmin(); // TODO: give GA privileges as well
            };

            $scope.addDutyRosterItem = function (date, roster) {
                Database.addDutyRosterItem(date, roster);
            };
        }
    ])
    .controller('AddDutyRosterItemCtrl', [
        '$scope',
        '$uibModalInstance',
        'Database',
        function ($scope, $uibModalInstance, Database) {
            $scope.today = function () {
                $scope.dt = new Date();
            };
            $scope.today();

            $scope.disabled = function (date, mode) {
                return mode === 'day' && (date.getDay() !== 5 && date.getDay() !== 6);
            };

            $scope.open = function () {
                $scope.popup.opened = true;
            };

            $scope.popup = {
                opened: false
            };

            $scope.setDate = function (year, month, day) {
                $scope.dt = new Date(year, month, day);
            };

            $scope.dateOptions = {
                formatYear: 'yy',
                startingDay: 1
            };

            $scope.format = 'yyyy-MM-dd';
            $scope.altInputFormats = [
                'M!/d!/yyyy',
                'dd-MM-yyyy',
                'yyyy/MM/dd',
                'dd.MM.yyyy',
                'MMMM dd, yyyy',
                'shortDate'];

            $scope.resHalls = [];
            $scope.roster = {};

            Database.getResHalls(function (data) {
                angular.forEach(data, function (resHall) {
                    $scope.resHalls.push(resHall.$id);
                    $scope.roster[resHall.$id] = '';
                });
            });

            Database.getRAs(function (data) {
                $scope.ras = data;
            });

            $scope.getRAsForHall = function (hall) {
                var ras = [];
                angular.forEach($scope.ras, function (ra) {
                    if (ra.hall === hall) {
                        ras.push(ra);
                    }
                });
                return ras;
            };

            $scope.modalOptions.ok = function (date, roster) {
                var result = {};

                var dateObj = new Date(Date.parse(date));
                var month = (dateObj.getMonth() + 1);
                var monthStr;
                if (month < 10) {
                    monthStr = '0' + month;
                } else {
                    monthStr = '' + month;
                }
                var dateOfMonth = dateObj.getDate();
                var dateOfMonthStr;
                if (dateOfMonth < 10) {
                    dateOfMonthStr = '0' + dateOfMonth;
                } else {
                    dateOfMonthStr = '' + dateOfMonth;
                }
                date = dateObj.getFullYear() + '-' + monthStr + '-' + dateOfMonthStr;

                angular.forEach(roster, function (value, key) {
                    roster[key] = {
                        email: value.email,
                        name: value.name,
                        phoneNumber: value.phoneNumber,
                        uid: value.$id
                    };
                });

                result.roster = roster;
                result.date = date;
                $uibModalInstance.close(result);
            };
            $scope.modalOptions.close = function (result) {
                $uibModalInstance.dismiss('cancel');
            };
        }
    ]);
