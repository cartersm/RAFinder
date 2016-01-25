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
            $scope.employeeTypes = ['Resident Assistant', 'Sophomore Advisor', 'Graduate Assistant', 'Administrator'];

            $scope.showAddEmployeeModal = function () {
                var modalDefaults = {
                    templateUrl: 'employees/addEmployee.html'
                };
                var modalOptions = {
                    headerText: 'Add an Employee',
                    actionButtonText: 'Add Employee',
                    closeButtonText: 'Cancel'
                };

                ModalService.showModal(modalDefaults, modalOptions)
                    .then(function (successResult) {
                        $scope.addEmployee($scope.user);
                    }, function (cancelResult) {
                        $scope.user = {};
                    });
            };

            $scope.addEmployee = function (user) {
                // Set some defaults
                user.status = 'In My Room';
                user.statusDetail = '';
                user.profilePicture = '';
                user.hall = user.hall.$id;

                console.log('adding new ' + $scope.employeeType + ': ');
                console.log(user);
                $scope.employeeType += 's';

                // FIXME: randomly generated password, then immediately send a "temporary credentials" email
                authObj.$createUser({email: $scope.user.email, password: 'test1234'})
                    .then(function (authData) {
                        console.log('successfully created user: ' + authData.uid);
                        firebase.child('Employees/' + $scope.employeeType + '/' + authData.uid)
                            .set($scope.user, function (error) {
                                if (error != null) {
                                    // TODO: figure out how to send feedback to the GUI
                                    // TODO: if address already in use, invalidate the email field
                                    console.error(error);
                                    // FIXME: push first, then add auth'ed user
                                } else {
                                    $('#addEmployee').modal('hide');
                                }
                            });
                    }, function (error) {
                        console.log('Error creating user: ' + error);
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
                            authObj.$removeUser({email: person.email, password: 'test1234'});
                        }
                    });
            };

            $scope.isUserInvalid = function (employeeType, user) {
                return !employeeType ||
                    !user.name ||
                    !user.email ||
                    !user.phoneNumber ||
                    !user.hall ||
                    !user.floor ||
                    !user.room;
            };

            $scope.isAdmin = function () {
                return AuthService.isAdmin();
            };
        }
    ]);
