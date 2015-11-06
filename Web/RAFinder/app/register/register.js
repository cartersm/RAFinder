'use strict';

angular.module('RAFinder.register', [
    "ngRoute",
    "firebase"
])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/register', {
            templateUrl: 'register/register.html',
            controller: 'RegisterCtrl'
        });
    }])

    .controller('RegisterCtrl', ['$scope', "$firebaseAuth", function ($scope, $firebaseAuth) {
        var firebase = new Firebase("https://ra-finder.firebaseio.com");
        var auth = $firebaseAuth(firebase);
        $scope.signUp = function () {
            if ($scope.regForm.$invalid) return;
            var email = $scope.user.email;
            var password = $scope.user.password;
            var raEmail = $scope.user.raEmail;

            if (!email || !password || !raEmail) return;
            firebase.child("Employees/Resident Assistants").once("value", function (data) {
                data.forEach(function (child) {
                    if (child.child("email").val().equals($scope.user.raEmail)) {
                        auth.$createUser({email: email, password: password})
                            .then(function (authData) {
                                console.log("successfully created user: " + authData.uid);
                                firebase.child("Residents/" + authData.uid).set({myRA: raEmail}, function (error) {
                                    if (error != null) {
                                        // TODO: figure out how to sebd feedback to the GUI
                                        console.error(e);
                                    }
                                });
                            }, function (error) {
                                console.log("Error creating user: " + error);
                            });
                    }
                });
            });


        }
    }]);