'use strict';
angular.module('RAFinder.services.auth', [
    'rosefire'
])
/**
 * A service to handle user authentication and usage of an authenticated user's data.
 */
    .service('Auth', [
        '$firebaseAuth',
        '$window',
        '$firebaseObject',
        '$location',
        'EnvConfig',
        'Rosefire',
        function ($firebaseAuth, $window, $firebaseObject, $location, EnvConfig, Rosefire) {
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
                    if (onFailure) {
                        onFailure('not logged in');
                    }
                } else {
                    if (EnvConfig.env !== 'prod') {
                        auth.uid = auth.password.email;
                    } else if (!auth.uid.endsWith('@rose-hulman.edu')) {
                        auth.uid += '@rose-hulman.edu';
                    }
                    // console.log(auth);
                    // Find out whether the user is an admin or other employee
                    var obj = $firebaseObject(firebase.child('Employees/Administrators'));
                    obj.$loaded().then(function (data) {
                        angular.forEach(data, function (value) {
                            if (value.email.toLowerCase() === auth.uid.toLowerCase()) {
                                isAdmin = true;
                                isEmployee = true;
                            }
                        });

                        if (isAdmin) {
                            user = auth.uid;
                            if (typeof onSuccess === 'function') {
                                onSuccess();
                            }
                        } else {
                            obj = $firebaseObject(firebase.child('Employees/Graduate Assistants'));
                            obj.$loaded().then(function (data) {
                                angular.forEach(data, function (value) {
                                    if (value.email.toLowerCase() === auth.uid.toLowerCase()) {
                                        isGA = true;
                                        isEmployee = true;
                                    }
                                });

                                if (isGA) {
                                    user = auth.uid;
                                    if (typeof onSuccess === 'function') {
                                        onSuccess();
                                    }
                                } else {
                                    obj = $firebaseObject(firebase.child('Employees'));
                                    obj.$loaded().then(function (data) {
                                        angular.forEach(data, function (child) {
                                            angular.forEach(child, function (value) {
                                                if (value.email.toLowerCase() === auth.uid.toLowerCase()) {
                                                    isEmployee = true;
                                                }
                                            });
                                        });
                                        if (isEmployee) {
                                            user = auth.uid;
                                            if (onSuccess) {
                                                onSuccess();
                                            }
                                        } else {
                                            user = '';
                                            console.log('non-employee has been logged out');
                                            self.logoutUser();
                                            if (onFailure) {
                                                onFailure('non-employee attempted login');
                                            }
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
                user = auth.uid;
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
                if (EnvConfig.env !== 'prod') {
                    authObj.$authWithPassword({
                        email: username,
                        password: password
                    }).then(function (authData) {
                        callback(null, authData);
                    }).catch(function (error) {
                        callback(error, null);
                    });
                    return;
                }
                // Else, use RoseFire
                Rosefire.signIn(EnvConfig.token)
                    .then(function (token) {
                        authObj.$authWithCustomToken(token)
                            .then(function (authData) {
                                if (EnvConfig.env !== 'prod') {
                                    authData.uid = authData.password.email;
                                } else if (!authData.uid.endsWith('@rose-hulman.edu')) {
                                    authData.uid += '@rose-hulman.edu';
                                }
                                callback(null, authData);
                            })
                            .catch(function (error) {
                                callback(error, null);
                            });
                    });

            };
        }
    ]);
