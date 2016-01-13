'use strict';

angular.module('RAFinder', [
    'ngRoute',
    'ui.bootstrap',
    'RAFinder.login',
    'RAFinder.employees',
    "RAFinder.hallRoster"
])
    .config(['$routeProvider',
        function ($routeProvider) {
            // Always redirect to login page
            // Login will forward to home page if logged in
            $routeProvider.otherwise({redirectTo: '/login'});
        }
    ])
    .controller('BlogNavCtrl', ['$scope', '$location', "CommonProp",
        function ($scope, $location, CommonProp) {
            $scope.isActive = function (viewLocation) {
                return viewLocation === $location.path();
            };

            $scope.logout = function () {
                CommonProp.logoutUser();
            };

            $scope.hasAuth = function () {
                var isAuthed = !(CommonProp.getUser() === '');
                if (isAuthed) {
                    // the navbar is forcibly hidden by default so that
                    //     it doesn't appear on initial load.
                    // Remove the 'hidden' class so ngShow can take over.
                    $("#primary-navbar").removeClass("hidden");
                }
                return isAuthed;
            }
        }
    ])
    .service('CommonProp', ["$firebaseAuth", "$window", "$firebaseObject",
        function ($firebaseAuth, $window, $firebaseObject) {
            var user = "";
            var isEmployee = false;
            var isAdmin = false;
            var firebase = new Firebase("https://ra-finder.firebaseio.com");
            var authObj = $firebaseAuth(firebase);

            this.checkAuth = function (auth, callback) {
                if (auth !== null) {
                    //console.log(auth.uid);
                    // Find out whether the user is an admin or other employee
                    var obj = $firebaseObject(firebase.child("Employees/Administrators"));
                    obj.$loaded().then(function () {
                        angular.forEach(obj, function (value, key) {
                            //console.log(key);
                            if (key === auth.uid) {
                                isAdmin = true;
                                isEmployee = true;
                            }
                        });

                        if (!isAdmin) {
                            obj = $firebaseObject(firebase.child("Employees"));
                            obj.$loaded().then(function () {
                                angular.forEach(obj, function (child) {
                                    angular.forEach(child, function (value, key) {
                                        //console.log(key);
                                        if (key === auth.uid) {
                                            isEmployee = true;
                                        }
                                    })
                                });
                                if (!isEmployee) {
                                    authObj.$unauth();
                                    console.log("non-employee has been logged out");
                                }
                                callback();
                            });
                        } else {
                            callback();
                        }
                    });
                }
            };

            var auth = authObj.$getAuth();
            this.checkAuth(auth, function () {
                user = auth.password.email;
            });

            return {
                getUser: function () {
                    return user;
                },
                setUser: function (value) {
                    user = value;
                },
                logoutUser: function () {
                    authObj.$unauth();
                    console.log("Logout complete");
                    // Force reload to hide the navbar
                    $window.location.reload();
                },
                isAdmin: function () {
                    return isAdmin;
                },
                isEmployee: function () {
                    return isEmployee;
                },
                checkAuth: this.checkAuth
            }
        }
    ])
    // the following is borrowed from http://weblogs.asp.net/dwahlin/building-an-angularjs-modal-service
    .service("ModalService", ['$uibModal',
        function ($uibModal) {
            var modalDefaults = {
                backdrop: true,
                keyboard: true,
                modalFade: true,
                templateUrl: '/app/partials/modal.html'
            };

            var modalOptions = {
                closeButtonText: 'Close',
                actionButtonText: 'OK',
                headerText: 'Proceed?',
                bodyText: 'Perform this action?'
            };

            this.showModal = function (customModalDefaults, customModalOptions) {
                if (!customModalDefaults) customModalDefaults = {};
                customModalDefaults.backdrop = 'static';
                return this.show(customModalDefaults, customModalOptions);
            };

            this.show = function (customModalDefaults, customModalOptions) {
                //Create temp objects to work with since we're in a singleton service
                var tempModalDefaults = {};
                var tempModalOptions = {};

                //Map angular-ui modal custom defaults to modal defaults defined in service
                angular.extend(tempModalDefaults, modalDefaults, customModalDefaults);

                //Map modal.html $scope custom properties to defaults defined in service
                angular.extend(tempModalOptions, modalOptions, customModalOptions);

                if (!tempModalDefaults.controller) {
                    tempModalDefaults.controller = function ($scope, $modalInstance) {
                        $scope.modalOptions = tempModalOptions;
                        $scope.modalOptions.ok = function (result) {
                            $modalInstance.close(result);
                        };
                        $scope.modalOptions.close = function (result) {
                            $modalInstance.dismiss('cancel');
                        };
                    }
                }

                return $uibModal.open(tempModalDefaults).result;
            };
        }
    ]);