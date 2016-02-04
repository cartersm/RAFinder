'use strict';

angular.module('RAFinder.employees', [
    'ngRoute',
    'firebase'
])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/employees', {
            templateUrl: 'employees/employees.html',
            controller: 'EmployeesCtrl'
        });
    }])
    .controller('EmployeesCtrl', [
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

            $scope.accordionData = [];

            // Populate employee data
            Database.getRAs(function (data) {
                $scope.raData = data;
                $scope.accordionData.push({
                    heading: 'Resident Assistants',
                    type: 'ra',
                    data: $scope.raData,
                    showOnLoad: true
                });
            });

            Database.getSAs(function (data) {
                $scope.saData = data;
                $scope.accordionData.push({
                    heading: 'Sophomore Advisors',
                    type: 'sa',
                    data: $scope.saData,
                    showOnLoad: false
                });
            });

            Database.getGAs(function (data) {
                $scope.gaData = data;
                $scope.accordionData.push({
                    heading: 'Graduate Assistants',
                    type: 'ga',
                    data: $scope.gaData,
                    showOnLoad: false
                });
            });

            Database.getAdmins(function (data) {
                $scope.adminData = data;
                $scope.accordionData.push({
                    heading: 'Administrators',
                    type: 'admin',
                    data: $scope.adminData,
                    showOnLoad: false
                });
            });

            // adding/deleting employees
            // TODO: separate these into their own controllers
            $scope.user = {};
            $scope.employeeType = '';

            // Populate ResHall names
            $scope.resHalls = [];
            Database.getResHalls(function (data) {
                $scope.resHalls = data;
            });

            $scope.showAddEmployeeModal = function () {
                var modalDefaults = {
                    templateUrl: 'employees/addEmployee.html'
                };
                var modalOptions = {
                    headerText: 'Add an Employee',
                    actionButtonText: 'Add Employee',
                    closeButtonText: 'Cancel',
                    employeeTypes: ['Resident Assistant', 'Sophomore Advisor', 'Graduate Assistant', 'Administrator'],
                    resHalls: $scope.resHalls,
                    isUserInvalid: function (employeeType, user) {
                        return !employeeType || !user.name || !user.email || !user.phoneNumber || !user.hall ||
                            (!user.floor && user.floor !== 0) || !user.room;
                    }
                };

                Modal.showModal(modalDefaults, modalOptions)
                    .then(function (successResult) {
                        console.log(successResult);
                        $scope.addEmployee(successResult.type, successResult.user);
                    });
            };

            $scope.addEmployee = function (employeeType, user) {
                // Set some defaults
                user.status = 'In My Room';
                user.statusDetail = '';
                user.profilePicture = '';
                user.hall = user.hall.$id;

                console.log('adding new ' + employeeType + ': ');
                console.log(user);
                employeeType += 's';

                Database.addEmployee(employeeType, user);
            };

            // FIXME: replace this with edit.
            // We don't want to delete except when we overwrite with the CSV.
            $scope.deleteEmployee = function (type, person) {
                var modalDefaults = {
                    templateUrl: 'employees/deleteEmployee.html'
                };

                var modalOptions = {
                    headerText: 'Delete Employee?',
                    bodyText: 'Are you sure you want to delete ' + person.email + '?',
                    actionButtonText: 'Confirm',
                    closeButtonText: 'Cancel'
                };

                Modal.showModal(modalDefaults, modalOptions)
                    .then(function (successResult) {
                        console.warn('Deleting user ' + person.email);
                        Database.removeEmployee(type, person);
                    });
            };

            $scope.isAdmin = function () {
                return Auth.isAdmin();
            };
        }
    ]);
