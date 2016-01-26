'use strict';
angular.module('RAFinder.services', [])
    .service('AuthService', [
        '$firebaseAuth',
        '$window',
        '$firebaseObject',
        '$location',
        function ($firebaseAuth, $window, $firebaseObject, $location) {
            var user = '';
            var isEmployee = false;
            var isAdmin = false;
            var firebase = new Firebase('https://ra-finder.firebaseio.com');
            var authObj = $firebaseAuth(firebase);

            this.checkAuth = function (onSuccess, onFailure) {
                var auth = authObj.$getAuth();
                if (auth !== null) {
                    // Find out whether the user is an admin or other employee
                    var obj = $firebaseObject(firebase.child('Employees/Administrators'));
                    obj.$loaded().then(function () {
                        angular.forEach(obj, function (value, key) {
                            if (key === auth.uid) {
                                isAdmin = true;
                                isEmployee = true;
                            }
                        });

                        if (isAdmin) {
                            user = auth.password.email;
                            if (typeof onSuccess === 'function') onSuccess();
                        } else {
                            obj = $firebaseObject(firebase.child('Employees'));
                            obj.$loaded().then(function () {
                                angular.forEach(obj, function (child) {
                                    angular.forEach(child, function (value, key) {
                                        //console.log(key);
                                        if (key === auth.uid) {
                                            isEmployee = true;
                                        }
                                    });
                                });
                                if (isEmployee) {
                                    user = auth.username.email;
                                    if (typeof onSuccess === 'function') onSuccess();
                                } else {
                                    user = '';
                                    console.log('non-employee has been logged out');
                                    this.logoutUser();
                                    if (typeof onFailure === 'function') onFailure('non-employee attempted login');
                                }
                            }.bind(this));
                        }
                    }.bind(this));
                } else {
                    // We're not logged in
                    $location.path('/login');
                    if (typeof onFailure === 'function') onFailure('not logged in');
                }
            }.bind(this);

            this.getUser = function () {
                return user;
            }.bind(this);

            this.setUser = function (value) {
                user = value;
            }.bind(this);

            this.logoutUser = function () {
                authObj.$unauth();
                isEmployee = false;
                isAdmin = false;
                console.log('Logout complete');
                // Force reload to hide the navbar
                $location.path('/login');
            }.bind(this);

            this.isAdmin = function () {
                return isAdmin;
            }.bind(this);

            this.isEmployee = function () {
                return isEmployee;
            }.bind(this);

            var auth = authObj.$getAuth();
            this.checkAuth(function () {
                user = auth.password.email;
            });

            this.getAuthObject = function () {
                return authObj;
            };
        }
    ])
    // the following is borrowed from http://weblogs.asp.net/dwahlin/building-an-angularjs-modal-service
    .service('ModalService', [
        '$uibModal',
        '$rootScope',
        function ($uibModal, $rootScope) {
            var modalDefaults = {
                backdrop: true,
                keyboard: true,
                modalFade: true,
                templateUrl: '/app/partials/modal.html',
                scope: $rootScope.$new()
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
                    tempModalDefaults.controller = function ($scope, $uibModalInstance) {
                        $scope.modalOptions = tempModalOptions;
                        $scope.modalOptions.ok = function (result) {
                            $uibModalInstance.close(result);
                        };
                        $scope.modalOptions.close = function (result) {
                            $uibModalInstance.dismiss('cancel');
                        };
                    };
                }

                return $uibModal.open(tempModalDefaults).result;
            };
        }
    ]);
