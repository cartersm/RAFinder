'use strict';
angular.module('RAFinder.services.auth', [])
    .service('Auth', [
        '$firebaseAuth',
        '$window',
        '$firebaseObject',
        '$location',
        'EnvConfig',
        function ($firebaseAuth, $window, $firebaseObject, $location, EnvConfig) {
            // registry token for RoseFire
            const REGISTRY_TOKEN = 'f5bed5423c49f86cb1999207180b6520a0091e516e4135eb34e035fcf2da85748f8d8176c4c0da3' +
                '3055f57c6d042821fJXiBojbwBpJ9pabFlFE7RYn/yukoVvJLJ9RveyCVfmBWAFinaQi1a7toTpqn3rsN0U1Eyf3kphf1faL9k' +
                'BDkBgTAcay8Jwx+01DFViPYYoCiRAK8R6J09RJlzo10lZ8Z';
            var user = '';
            var isEmployee = false;
            var isAdmin = false;
            var isGA = false;
            var firebase = new Firebase(EnvConfig.url);
            var authObj = $firebaseAuth(firebase);

            var self = this;

            this.checkAuth = function (onSuccess, onFailure) {
                var auth = authObj.$getAuth();
                if (auth === null || auth.expires <= Date.now() / 1000) {
                    if (auth) {
                        // The auth has expired
                        self.logoutUser();
                    }
                    // We're not logged in
                    $location.path('/login');
                    if (onFailure) onFailure('not logged in');
                } else {
                    // Find out whether the user is an admin or other employee
                    var obj = $firebaseObject(firebase.child('Employees/Administrators'));
                    obj.$loaded().then(function (data) {
                        angular.forEach(data, function (value, key) {
                            // TODO: this'll break RoseFire auth
                            if (key === auth.uid) {
                                isAdmin = true;
                                isEmployee = true;
                            }
                        });

                        if (isAdmin) {
                            user = auth.password.email;
                            if (typeof onSuccess === 'function') onSuccess();
                        } else {
                            var obj = $firebaseObject(firebase.child('Employees/Graduate Assistants'));
                            obj.$loaded().then(function (data) {
                                angular.forEach(data, function (value, key) {
                                    // TODO: this'll break RoseFire auth
                                    if (key === auth.uid) {
                                        isGA = true;
                                        isEmployee = true;
                                    }
                                });

                                if (isGA) {
                                    user = auth.password.email;
                                    if (typeof onSuccess === 'function') onSuccess();
                                } else {
                                    obj = $firebaseObject(firebase.child('Employees'));
                                    obj.$loaded().then(function (data) {
                                        angular.forEach(data, function (child) {
                                            angular.forEach(child, function (value, key) {
                                                // TODO: this'll break RoseFire auth
                                                if (key === auth.uid) {
                                                    isEmployee = true;
                                                }
                                            });
                                        });
                                        if (isEmployee) {
                                            user = auth.password.email;
                                            if (onSuccess) onSuccess();
                                        } else {
                                            user = '';
                                            console.log('non-employee has been logged out');
                                            self.logoutUser();
                                            if (onFailure) onFailure('non-employee attempted login');
                                        }
                                    });
                                }
                            });
                        }

                    });
                }
            };

            this.getUser = function () {
                return user;
            };

            this.setUser = function (value) {
                user = value;
            };

            this.logoutUser = function () {
                authObj.$unauth();
                isEmployee = false;
                isAdmin = false;
                console.log('Logout complete');
                // Force reload to hide the navbar
                $location.path('/login');
            };

            this.isAdmin = function () {
                return isAdmin;
            };

            this.isGA = function () {
                return isGA;
            };

            this.isEmployee = function () {
                return isEmployee;
            };

            this.isOwnRecord = function (record) {
                // TODO: test me
                return record.email === user;
            };

            var auth = authObj.$getAuth();
            this.checkAuth(function () {
                user = auth.password.email;
            });

            this.getAuthObject = function () {
                return authObj;
            };

            this.auth = function (username, password, callback) {
                authObj.$authWithPassword({
                    email: username,
                    password: password
                }).then(function (authData) {
                    callback(null, authData);
                }).catch(function (error) {
                    callback(error, null);
                });

                // TODO: Rosefire auth - this will require either adding '@rose-hulman.edu' or checking against username only
                //var data = {
                //    registryToken: REGISTRY_TOKEN,
                //    email: username,
                //    password: password
                //};
                //
                //$window.Rosefire.getToken(data, function (err, token) {
                //    if (err) {
                //        callback(error, null);
                //        return;
                //    }
                //    authObj.$authWithCustomToken({
                //        email: username,
                //        password: password
                //    }).then(function (authData) {
                //        callback(null, authData);
                //    }).catch(function (error) {
                //        callback(error, null);
                //    });
                //});
            };
        }
    ]);
