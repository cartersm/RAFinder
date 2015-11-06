'use strict';

angular.module('RAFinder.employees', [
    'ngRoute',
    "firebase"
])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/employees', {
            templateUrl: 'employees/employees.html',
            controller: 'EmployeesCtrl'
        });
    }])
    .controller('EmployeesCtrl', ["$scope", "$firebaseAuth", "$location", "CommonProp", "$firebaseArray",
        function ($scope, $firebaseAuth, $location, CommonProp, $firebaseArray) {
            var firebase = new Firebase("https://ra-finder.firebaseio.com");
            var authObj = $firebaseAuth(firebase);

            $firebaseArray(firebase.child("Employees/Resident Assistants"))
                .$loaded()
                .then(function (data) {
                    $scope.raData = data;
                });
            $firebaseArray(firebase.child("Employees/Sophomore Advisors"))
                .$loaded()
                .then(function (data) {
                    $scope.saData = data;
                });
            $firebaseArray(firebase.child("Employees/Graduate Assistants"))
                .$loaded()
                .then(function (data) {
                    $scope.gaData = data;
                });
            $firebaseArray(firebase.child("Employees/Administrators"))
                .$loaded()
                .then(function (data) {
                    $scope.adminData = data;
                });

            if (authObj === null || authObj.$getAuth() === null) {
                $location.path("home");
            }

            CommonProp.setUser(authObj.$getAuth().password.email);
            $scope.username = CommonProp.getUser();

            $scope.logout = function () {
                CommonProp.logoutUser();
            };

            $('[data-toggle="popover"]').popover({
                html: true,
                content: function () {
                    return $('#addEmployee').html();
                }
            }).on('show.bs.popover', function () {

            });

            // Stuff for adding/deleting employees

            $scope.user = {};
            $scope.employeeType = "";

            $scope.resHalls = [];
            $firebaseArray(firebase.child("ResHalls"))
                .$loaded()
                .then(function (data) {
                    $scope.resHalls = data;
                });
            $scope.employeeTypes = ["Resident Assistant", "Sophomore Advisor", "Graduate Assistant", "Administrator"];
            $scope.addEmployee = function (user) {
                // Set some defaults
                user["status"] = "In My Room";
                user["statusDetail"] = "";
                user["profilePicture"] = "";
                user["hall"] = user["hall"].$id;

                console.log("adding new " + $scope.employeeType + ": ");
                console.log(user);
                $scope.employeeType += "s";

                // FIXME: maybe have the admin specify a custom password, then immediately send an email with "temporary credentials"?
                authObj.$createUser({email: $scope.user.email, password: "test1234"})
                    .then(function (authData) {
                        console.log("successfully created user: " + authData.uid);
                        firebase.child("Employees/" + $scope.employeeType + "/" + authData.uid)
                            .set($scope.user, function (error) {
                                if (error != null) {
                                    // TODO: figure out how to send feedback to the GUI
                                    // TODO: if address already in use, invalidate the email field
                                    console.error(error);
                                    // FIXME: push first, then add auth'ed user
                                } else {
                                    $("#addEmployee").modal("hide");
                                }
                            });
                    }, function (error) {
                        console.log("Error creating user: " + error);
                    });
            };

            $scope.deleteEmployee = function (type, person) {
                // TODO: clunky; I'd prefer to have it in Bootstrap style
                // UPDATE: can use a modal, but will come back to that later.
                var confirmed = window.confirm("are you sure you want to delete employee " + person.email + "?");

                if (confirmed) {
                    console.warn("Deleting user " + person.email);
                    var data;
                    switch (type) {
                        case "ra":
                            data = $scope.raData;
                            break;
                        case "sa":
                            data = $scope.saData;
                            break;
                        case "ga":
                            data = $scope.gaData;
                            break;
                        case "admin":
                            data = $scope.adminData;
                            break;
                    }
                    data.$remove(person);
                    if (person.email.endsWith("@test.com")) {
                        // This is a test entity created by a demo of the app; remove it from the Firebase's authorized users
                        authObj.$removeUser({email: person.email, password: "test1234"});
                    }
                }
            }
        }]);