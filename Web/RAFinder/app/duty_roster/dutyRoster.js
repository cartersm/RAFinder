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
                        console.log(successResult);
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
                    $scope.resHalls.push(resHall.hall);
                    $scope.roster[resHall.hall] = '';
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

                date = Database.formatDate(date);

                result.roster = [];

                angular.forEach(roster, function (value, key) {
                    result.roster.push({
                        hall: key,
                        email: value.email,
                        name: value.name,
                        phoneNumber: value.phoneNumber,
                        uid: value.$id
                    });
                });

                result.date = date;
                $uibModalInstance.close(result);
            };
            $scope.modalOptions.close = function (result) {
                $uibModalInstance.dismiss('cancel');
            };
        }
    ]);
