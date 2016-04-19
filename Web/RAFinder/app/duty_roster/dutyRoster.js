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

            Database.getDutyRosters(function (data) {
                $scope.rosterData = data;
            });

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
                        $scope.addDutyRosterItem(successResult.date, successResult.roster);
                    });
            };

            $scope.canAddDutyRosterItem = function () {
                return Auth.isAdmin() || Auth.isGA();
            };

            $scope.addDutyRosterItem = function (date, roster) {
                Database.addDutyRosterItem(date, roster);
            };

            $scope.file = {
                data: ''
            };
            $scope.overwriteRosters = false;

            $scope.onFileLoaded = function () {
                Database.parseDutyRosterFile($scope.file.data, $scope.overwriteRosters);
            };

            $scope.onFileError = function (error) {
                console.error('Error uploading Duty Roster', error);
            };

            $scope.showEditDutyRosterItemModal = function (roster) {
                var modalDefaults = {
                    templateUrl: 'duty_roster/addDutyRosterItem.html',
                    controller: 'AddDutyRosterItemCtrl'
                };
                var modalOptions = {
                    headerText: 'Edit a Duty Roster item',
                    actionButtonText: 'Commit Edit',
                    closeButtonText: 'Cancel',
                    roster: roster
                };

                Modal.showModal(modalDefaults, modalOptions)
                    .then(function (successResult) {
                        $scope.editDutyRosterItem(successResult);
                    });
            };

            $scope.editDutyRosterItem = function (roster) {
                Database.editDutyRosterItem(roster);
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

            if ($scope.modalOptions.roster) {
                $scope.dt = new Date($scope.modalOptions.roster.date);
                $scope.dt.setDate($scope.dt.getDate() + 1);
            } else {
                $scope.today();
            }

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

            var constructEditRoster = function () {
                if (!$scope.modalOptions.roster) {
                    $scope.roster = {};
                    return;
                }
                $scope.roster = {};
                $scope.hallToUidMap = {};
                angular.forEach($scope.modalOptions.roster.roster, function (value, key) {
                    $scope.roster[value.hall] = $scope.ras.$getRecord(value.uid) ||
                        $scope.gas.$getRecord(value.uid) ||
                        $scope.admins.$getRecord(value.uid);
                    $scope.hallToUidMap[value.hall] = key;
                });
            };

            var isTriplet = function (hall) {
                return (hall.toLowerCase() === 'mees' ||
                hall.toLowerCase() === 'blumberg' ||
                hall.toLowerCase() === 'scharpenberg');
            };

            $scope.resHalls = [];

            Database.getRAs(function (data) {
                $scope.ras = data;
                Database.getAdmins(function (data) {
                    $scope.admins = data;
                    Database.getGAs(function (data) {
                        $scope.gas = data;
                        constructEditRoster();
                    });
                });
            });

            Database.getResHalls(function (data) {
                angular.forEach(data, function (resHall) {
                    if (isTriplet(resHall.hall)) return;
                    $scope.resHalls.push(resHall.hall);
                    if (!$scope.modalOptions.roster) {
                        $scope.roster[resHall.hall] = '';
                    }
                });
                angular.forEach(['Triplets 1', 'Triplets 2', 'OCP', 'GA'], function (hall) {
                    $scope.resHalls.push(hall);
                    if (!$scope.modalOptions.roster) {
                        $scope.roster[hall] = '';
                    }
                });
            });

            $scope.getRAsForHall = function (hall) {
                if (hall === 'OCP') {
                    return $scope.admins;
                } else if (hall === 'GA') {
                    return $scope.gas;
                }
                var ras = [];
                angular.forEach($scope.ras, function (ra) {
                    if (ra.hall === hall ||
                        (isTriplet(ra.hall) &&
                        (hall.toLowerCase() === 'triplets 1' || hall.toLowerCase() === 'triplets 2'))) {
                        ras.push(ra);
                    }
                });
                return ras;
            };

            $scope.modalOptions.ok = function (date, roster) {
                var result = $scope.modalOptions.roster || {};

                date = Database.formatDate(date);

                var resultRoster;

                if (!$scope.modalOptions.roster) {
                    resultRoster = [];
                    angular.forEach(roster, function (value, key) {
                        resultRoster.push({
                            hall: key,
                            email: value.email,
                            name: value.name,
                            phoneNumber: value.phoneNumber,
                            uid: value.$id
                        });
                    });
                } else {
                    resultRoster = result.roster;
                    angular.forEach(roster, function (value, key) {
                        resultRoster[$scope.hallToUidMap[value.hall]] = {
                            hall: key,
                            email: value.email,
                            name: value.name,
                            phoneNumber: value.phoneNumber,
                            uid: value.$id || value.uid
                        };
                    });
                    // CONSIDER: The "Triplets 2" RA was being re-added under the key "undefined".
                    // This deletes that bad data from the result.
                    delete resultRoster.undefined;
                }

                result.roster = resultRoster;
                result.date = date;
                $uibModalInstance.close(result);
            };
            $scope.modalOptions.close = function (result) {
                $uibModalInstance.dismiss('cancel');
            };
        }
    ]);
