'use strict';
/**
 * A service to handle user authentication and usage of an authenticated user's data.
 */
angular.module('RAFinder.services.auth', [])
    .service('Auth', [
        '$firebaseAuth',
        '$window',
        '$firebaseObject',
        '$location',
        'EnvConfig',
        function ($firebaseAuth, $window, $firebaseObject, $location, EnvConfig) {
            // registry token for RoseFire TODO: move me
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

            /**
             * Checks the authentication status of the user, then what class that employee is.
             * non-Employees are logged out immediately, and Employees are further queried for their employee type.
             *
             * @param {function} [onSuccess] - a callback method for a successful auth check.
             * @param {function} [onFailure] - a callback method for a failed auth check.
             */
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

            /**
             * Returns the authenticated user's email address.
             *
             * @returns {String}
             */
            this.getUser = function () {
                return user;
            };

            /**
             * Sets the authenticated user's email address to the given value.
             *
             * @param {String} value
             */
            this.setUser = function (value) {
                user = value;
            };

            /**
             * Logs the user out.
             */
            this.logoutUser = function () {
                authObj.$unauth();
                isEmployee = false;
                isAdmin = false;
                console.log('Logout complete');
                // Force reload to hide the navbar
                $location.path('/login');
            };

            /**
             * Returns true is the authenticated user is an Admin.
             *
             * @returns {boolean}
             */
            this.isAdmin = function () {
                return isAdmin;
            };

            /**
             * Returns true is the authenticated user is a GA.
             *
             * @returns {boolean}
             */
            this.isGA = function () {
                return isGA;
            };

            /**
             * Returns true is the authenticated user is any Employee.
             *
             * @returns {boolean}
             */
            this.isEmployee = function () {
                return isEmployee;
            };

            /**
             * Returns true if the given record is the user's own record.
             *
             * @param {Object} record - The record to compare against.
             * @param {String} record.email - The email address being compared.
             * @returns {boolean}
             */
            this.isOwnRecord = function (record) {
                // TODO: test me
                return record.email === user;
            };

            var auth = authObj.$getAuth();
            this.checkAuth(function () {
                user = auth.password.email;
            });

            /**
             * Returns the auth object associated with this instance.
             *
             * @returns {Object}
             */
            this.getAuthObject = function () {
                return authObj;
            };

            /**
             * Attempts to authenticate the given username and password.
             *
             * @param {String} username - the username or email address to authenticate.
             * @param {String} password - the password to authenticate.
             * @param {function} [callback] = A callback function that is passed authData on success.
             */
            this.auth = function (username, password, callback) {
                // TODO: Rosefire auth - this will require either adding '@rose-hulman.edu' or checking against username only
                // if (EnvConfig.env === 'prod') {
                //     var data = {
                //         registryToken: REGISTRY_TOKEN,
                //         email: username,
                //         password: password
                //     };
                //
                //     $window.Rosefire.getToken(data, function (err, token) {
                //         if (err) {
                //             callback(error, null);
                //             return;
                //         }
                //         authObj.$authWithCustomToken({
                //             email: username,
                //             password: password
                //         }).then(function (authData) {
                //             callback(null, authData);
                //         }).catch(function (error) {
                //             callback(error, null);
                //         });
                //     });
                // } else {
                authObj.$authWithPassword({
                    email: username,
                    password: password
                }).then(function (authData) {
                    callback(null, authData);
                }).catch(function (error) {
                    callback(error, null);
                });
                // }
            };
        }
    ]);
