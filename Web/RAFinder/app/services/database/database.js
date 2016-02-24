'use strict';
angular.module('RAFinder.services.database', [
        'firebase',
        'RAFinder.services.fileReader'
    ])
    .service('Database', [
        '$firebaseObject',
        '$firebaseArray',
        'Auth',
        'FileReader',
        function ($firebaseObject, $firebaseArray, Auth, fileReader) {
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

            this.parseEmployeeCsv = function (data, overwrite) {
                var ras = [];
                var sas = [];
                var gas = [];
                var admins = [];
                //var fraternityPresidents = [];

                fileReader.readFile(data,
                    function (lineData) {
                        var toAdd = angular.copy(lineData);

                        toAdd.email = toAdd.username + '@rose-hulman.edu';
                        delete toAdd.username;
                        if (toAdd.room.length === 1) {
                            // leading zeros were stripped, add them back
                            toAdd.room = '00' + toAdd.room;
                        } else if (toAdd.room.length === 2) {
                            // leading zero was stripped, add it back
                            toAdd.room = '0' + toAdd.room;
                        }
                        toAdd.floor = toAdd.room.substring(0, 1);
                        delete toAdd.type;

                        toAdd.hall = normalizeHall(toAdd.hall);

                        toAdd.profilePicture = '';
                        toAdd.status = 'In My Room';
                        toAdd.statusDetail = '';

                        if (lineData.type === 'Resident Assistant') {
                            ras.push(toAdd);
                        } else if (lineData.type === 'Sophomore Adviser') {
                            sas.push(toAdd);
                        } else if (lineData.type === 'Graduate Assistant') {
                            gas.push(toAdd);
                        } else if (lineData.type === 'Administrator') {
                            admins.push(toAdd);
                        }
                        //else if (lineData.type === 'Fraternity President') {
                        //    fraternityPresidents.push(toAdd);
                        //}
                    },
                    function () {
                        if (overwrite) {
                            firebase.child('Employees/Resident Assistants').remove();
                            firebase.child('Employees/Sophomore Advisors').remove();
                            firebase.child('Employees/Graduate Assistants').remove();
                            //firebase.child('Employees/Administrators').remove();
                        }
                        angular.forEach(ras, function (ra) {
                            self.employees.ras.$add(ra);
                        });

                        angular.forEach(sas, function (sa) {
                            self.employees.sas.$add(sa);
                        });

                        angular.forEach(gas, function (ga) {
                            self.employees.gas.$add(ga);
                        });

                        angular.forEach(admins, function (admin) {
                            self.employees.admins.$add(admin);
                        });

                        //angular.forEach(fraternityPresidents, function (pres) {
                        //    self.employees.fraternityPresidents.$add(pres);
                        //});
                    });
            };

            var normalizeHall = function (hall) {
                if (hall === 'APT. STYLE - EAST') {
                    return 'Apartments East';
                } else if (hall === 'APT. STYLE - WEST') {
                    return 'Apartments West';
                } else if (hall === 'BAUR-SAMES-BOGART HALL') {
                    return 'BSB';
                } else if (hall === 'BLUMBERG HALL') {
                    return 'Blumberg';
                } else if (hall === 'DEMING HALL') {
                    return 'Deming';
                } else if (hall === 'MEES HALL') {
                    return 'Mees';
                } else if (hall === 'PERCOPO HALL') {
                    return 'Percopo';
                } else if (hall === 'SCHARPENBERG HALL') {
                    return 'Scharpenberg';
                } else if (hall === 'LAKESIDE HALL') {
                    return 'Lakeside';
                } else if (hall === 'SPEED HALL') {
                    return 'Speed';
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
                        $firebaseArray(ref.child('roster'))
                            .$loaded()
                            .then(function (newRoster) {
                                angular.forEach(roster, function (item) {
                                    newRoster.$add(item);
                                });
                            });
                    });
            };
        }
    ]);
