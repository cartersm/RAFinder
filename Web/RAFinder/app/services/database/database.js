'use strict';
angular.module('RAFinder.services.database', [
    'firebase'
])
    .service('Database', [
        '$firebaseObject',
        '$firebaseArray',
        'Auth',
        function ($firebaseObject, $firebaseArray, Auth) {
            var firebase = new Firebase('https://ra-finder.firebaseio.com');

            this.employees = {
                ras: [],
                sas: [],
                gas: [],
                admins: []
            };

            this.resHalls = [];
            this.dutyRosters = [];

            var self = this;

            // Employees
            var loadRAs = function (callback) {
                $firebaseArray(firebase.child('Employees/Resident Assistants'))
                    .$loaded()
                    .then(function (data) {
                        self.employees.ras = data;
                        callback(self.employees.ras);
                    });
            };

            var loadSAs = function (callback) {
                $firebaseArray(firebase.child('Employees/Sophomore Advisors'))
                    .$loaded()
                    .then(function (data) {
                        self.employees.sas = data;
                        callback(self.employees.sas);
                    });
            };

            var loadGAs = function (callback) {
                $firebaseArray(firebase.child('Employees/Graduate Assistants'))
                    .$loaded()
                    .then(function (data) {
                        self.employees.gas = data;
                        callback(self.employees.gas);
                    });
            };

            var loadAdmins = function (callback) {
                $firebaseArray(firebase.child('Employees/Administrators'))
                    .$loaded()
                    .then(function (data) {
                        self.employees.admins = data;
                        callback(self.employees.admins);
                    });
            };

            this.getRAs = function (callback) {
                if (self.employees.ras.length === 0) {
                    loadRAs(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.employees.ras);
                }
            };

            this.getSAs = function (callback) {
                if (self.employees.sas.length === 0) {
                    loadSAs(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.employees.sas);
                }
            };

            this.getGAs = function (callback) {
                if (self.employees.gas.length === 0) {
                    loadGAs(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.employees.gas);
                }
            };

            this.getAdmins = function (callback) {
                if (self.employees.admins.length === 0) {
                    loadAdmins(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.employees.admins);
                }
            };

            // FIXME: this should be removed as soon as we have KERBEROS auth in place
            this.addEmployee = function (employeeType, user) {
                var authObj = Auth.getAuthObject();
                authObj.$createUser({email: user.email, password: 'test1234'})
                    .then(function (authData) {
                        console.log('successfully created user: ' + authData.uid);
                        firebase.child('Employees/' + employeeType + '/' + authData.uid)
                            .set(user, function (error) {
                                if (error != null) {
                                    // TODO: look into Rockwood's validation
                                    console.error(error);
                                }
                            });
                    }, function (error) {
                        console.error('Error creating user: ' + error);
                    });
            };

            this.removeEmployee = function (type, person) {
                var data;
                switch (type) {
                    case 'ra':
                        data = self.employees.ras;
                        break;
                    case 'sa':
                        data = self.employees.sas;
                        break;
                    case 'ga':
                        data = self.employees.gas;
                        break;
                    case 'admin':
                        data = self.employees.admins;
                        break;
                }
                data.$remove(person);

                // FIXME: remove once KERBEROS auth is in place
                if (person.email.endsWith('@test.com')) {
                    // This is a test entity created by a demo of the app; remove it from the Firebase's authorized users
                    var authObj = Auth.getAuthObject();
                    authObj.$removeUser({email: person.email, password: 'test1234'});
                }
            };

            // Hall Rosters
            var loadResHalls = function (callback) {
                $firebaseArray(firebase.child('ResHalls'))
                    .$loaded()
                    .then(function (data) {
                        self.resHalls = data;
                        callback(self.resHalls);
                    });
            };

            this.getResHalls = function (callback) {
                if (self.resHalls.length === 0) {
                    loadResHalls(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.resHalls);
                }
            };

            // Duty Rosters
            var loadDutyRosters = function (callback) {
                $firebaseArray(firebase.child('DutyRosters'))
                    .$loaded()
                    .then(function (data) {
                        self.dutyRosters = data;
                        callback(self.dutyRosters);
                        console.log(self.dutyRosters);
                    });
            };

            this.getDutyRosters = function (callback) {
                if (self.dutyRosters.length === 0) {
                    loadDutyRosters(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.dutyRosters);
                }
            };

            this.addDutyRosterItem = function (date, roster) {
                self.dutyRosters.$add({date: date})
                    .then(function (ref) {
                        var newRoster = self.dutyRosters.$getRecord(ref.key());
                        // CONSIDER pushing these individually so they have UIDs
                        newRoster.roster = roster;
                        self.dutyRosters.$save(newRoster);
                    });
            };
        }
    ]);