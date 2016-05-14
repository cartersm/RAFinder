'use strict';
angular.module('RAFinder.services.auth', [])
    .service('Auth', [
        '$firebaseAuth',
        '$window',
        '$firebaseObject',
        '$location',
        'EnvConfig',
        function ($firebaseAuth, $window, $firebaseObject, $location, EnvConfig) {
            var user = '';
            var isEmployee = false;
            var isAdmin = false;
            var isGA = false;
            var firebase = new Firebase(EnvConfig.url);
            var authObj = $firebaseAuth(firebase);

            var self = this;

            this.checkAuth = function (onSuccess, onFailure) {
                var auth = authObj.$getAuth();
                // append '@rose-hulman.edu' if it does not exist
                if (!auth.uid.endsWith('@rose-hulman.edu')) {
                    auth.uid += '@rose-hulman.edu';
                }
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
                var data = {
                    registryToken: EnvConfig.token,
                    email: username + '@rose-hulman.edu',
                    password: password
                };

                $window.Rosefire.getToken(data, function (err, token) {
                    if (err) {
                        callback(err, null);
                        return;
                    }
                    authObj.$authWithCustomToken(token)
                        .then(function (authData) {
                            if (!auth.uid.endsWith('@rose-hulman.edu')) {
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
