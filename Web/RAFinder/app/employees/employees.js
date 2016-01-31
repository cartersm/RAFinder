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
        '$firebaseAuth',
        '$location',
        'AuthService',
        '$firebaseArray',
        'ModalService',
        function ($scope, $firebaseAuth, $location, AuthService, $firebaseArray, ModalService) {
            AuthService.checkAuth(function () {
                if (!AuthService.isEmployee()) {
                    $location.path('/login');
                }
            });

            var firebase = new Firebase('https://ra-finder.firebaseio.com');
            $scope.accordionData = [];

            // Populate employee data
            $firebaseArray(firebase.child('Employees/Administrators'))
                .$loaded()
                .then(function (data) {
                    $scope.adminData = data;
                    $scope.accordionData.push({
                        heading: 'Administrators',
                        type: 'admin',
                        data: $scope.adminData,
                        showOnLoad: false
                    });
                });
            $firebaseArray(firebase.child('Employees/Resident Assistants'))
                .$loaded()
                .then(function (data) {
                    $scope.raData = data;
                    $scope.accordionData.push({
                        heading: 'Resident Assistants',
                        type: 'ra',
                        data: $scope.raData,
                        showOnLoad: true
                    });
                });
            $firebaseArray(firebase.child('Employees/Sophomore Advisors'))
                .$loaded()
                .then(function (data) {
                    $scope.saData = data;
                    $scope.accordionData.push({
                        heading: 'Sophomore Advisors',
                        type: 'sa',
                        data: $scope.saData,
                        showOnLoad: false
                    });
                });
            $firebaseArray(firebase.child('Employees/Graduate Assistants'))
                .$loaded()
                .then(function (data) {
                    $scope.gaData = data;
                    $scope.accordionData.push({
                        heading: 'Graduate Assistants',
                        type: 'ga',
                        data: $scope.gaData,
                        showOnLoad: false
                    });
                });

            // adding/deleting employees
            // CONSIDER separating these into their own controllers
            $scope.user = {};
            $scope.employeeType = '';

            // Populate ResHall names
            $scope.resHalls = [];
            $firebaseArray(firebase.child('ResHalls'))
                .$loaded()
                .then(function (data) {
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
                        return !employeeType ||
                            !user.name ||
                            !user.email ||
                            !user.phoneNumber ||
                            !user.hall ||
                            (!user.floor && user.floor !== 0) ||
                            !user.room;
                    }
                };

                ModalService.showModal(modalDefaults, modalOptions)
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

                // FIXME: randomly generated password, then immediately send a "temporary credentials" email
                var authObj = AuthService.getAuthObject();
                authObj.$createUser({email: user.email, password: 'test1234'})
                    .then(function (authData) {
                        console.log('successfully created user: ' + authData.uid);
                        firebase.child('Employees/' + employeeType + '/' + authData.uid)
                            .set(user, function (error) {
                                if (error != null) {
                                    // TODO: modal with error
                                    // TODO: look into Rockwood's validation
                                    console.error(error);
                                }
                            });
                    }, function (error) {
                        console.error('Error creating user: ' + error);
                    });
            };

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

                ModalService.showModal(modalDefaults, modalOptions)
                    .then(function (successResult) {
                        console.warn('Deleting user ' + person.email);
                        var data;
                        switch (type) {
                            case 'ra':
                                data = $scope.raData;
                                break;
                            case 'sa':
                                data = $scope.saData;
                                break;
                            case 'ga':
                                data = $scope.gaData;
                                break;
                            case 'admin':
                                data = $scope.adminData;
                                break;
                        }
                        data.$remove(person);
                        if (person.email.endsWith('@test.com')) {
                            // This is a test entity created by a demo of the app; remove it from the Firebase's authorized users
                            var authObj = AuthService.getAuthObject();
                            authObj.$removeUser({email: person.email, password: 'test1234'});
                        }
                    });
            };

            $scope.isAdmin = function () {
                return AuthService.isAdmin();
            };
        }
    ]);
