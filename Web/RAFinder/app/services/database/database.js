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
                $firebaseArray(firebase.child('Employees/Resident Assistants').orderByChild('hall'))
                    .$loaded()
                    .then(function (data) {

                        self.employees.ras = data;
                        callback(self.employees.ras);
                    });
            };

            var loadSAs = function (callback) {
                $firebaseArray(firebase.child('Employees/Sophomore Advisors').orderByChild('hall'))
                    .$loaded()
                    .then(function (data) {
                        self.employees.sas = data;
                        callback(self.employees.sas);
                    });
            };

            var loadGAs = function (callback) {
                $firebaseArray(firebase.child('Employees/Graduate Assistants').orderByChild('hall'))
                    .$loaded()
                    .then(function (data) {
                        self.employees.gas = data;
                        callback(self.employees.gas);
                    });
            };

            var loadAdmins = function (callback) {
                $firebaseArray(firebase.child('Employees/Administrators').orderByChild('hall'))
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

                fileReader.readCsv(data,
                    function (lineData) {
                        var toAdd = angular.copy(lineData);

                        if (toAdd.username.indexOf('@') < 0) {
                            toAdd.email = toAdd.username + '@rose-hulman.edu';
                        } else {
                            toAdd.email = toAdd.username;
                        }
                        delete toAdd.username;
                        if (toAdd.room.length === 1) {
                            // leading zeros were stripped, add them back
                            toAdd.room = '00' + toAdd.room;
                        } else if (toAdd.room.length === 2) {
                            // leading zero was stripped, add it back
                            toAdd.room = '0' + toAdd.room;
                        }
                        toAdd.floor = parseInt(toAdd.room.substring(0, 1));
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

            this.editDutyRosterItem = function (roster) {
                self.dutyRosters.$save(roster);
            };

            this.parseDutyRosterFile = function (data, overwrite) {
                var rosters = [];
                var roster = roster = {
                    date: '',
                    roster: []
                };
                var lines = data.split('\n');
                var rollover = lines.length / 11;
                var count = 0;
                var nTriplets = 0;

                if (overwrite) {
                    firebase.child('DutyRosters').remove();
                }

                var headers = lines.slice(rollover * 10);
                if (self.employees.admins.length === 0) {
                    self.getAdmins(function (ignored) {
                        if (self.employees.ras.length === 0) {
                            self.getRAs(function (ignored) {
                                if (self.employees.gas.length === 0) {
                                    self.getGAs(function (ignored) {
                                        fileReader.readLines(data, dataCallback, endCallback);
                                    });
                                }
                            });
                        }
                    });
                }
                var dataCallback = function (line) {
                    if (count == rollover) {
                        count = 0;
                        rosters.push(angular.copy(roster));
                        roster = {
                            date: '',
                            roster: []
                        };
                        nTriplets = 0;
                    }
                    if (line in headers) return;
                    if (!isNaN(line)) {
                        roster.date =
                            line.slice(4, 8) + '-' + // YYYY
                            line.slice(0, 2) + '-' + // MM
                            line.slice(2, 4);        // DD
                    } else {
                        var employee = getEmployeeRecordForDutyRoster(line);
                        if (employee) {
                            if (employee.hall === 'Mees' ||
                                employee.hall === 'Scharpenberg' ||
                                employee.hall === 'Blumberg') {
                                nTriplets++;
                                employee.hall = 'Triplets ' + nTriplets;
                            }
                            roster.roster.push({
                                email: employee.email,
                                hall: employee.hall,
                                name: employee.name,
                                phoneNumber: employee.phoneNumber,
                                uid: employee.uid
                            });
                        }
                    }
                    count++;
                };
                var endCallback = function () {
                    angular.forEach(rosters, function (roster) {
                        self.addDutyRosterItem(roster.date, roster.roster);
                    });

                };
            };

            var getEmployeeRecordForDutyRoster = function (name) {
                var found = null;
                angular.forEach(self.employees.admins, function (admin) {
                    if (found) return;
                    if (admin.name === name) {
                        found = angular.copy(admin);
                        found.hall = 'OCP';
                        found.uid = admin.$id;
                    }
                });

                if (found) return found;

                angular.forEach(self.employees.gas, function (ga) {
                    if (found) return;
                    if (ga.name === name) {
                        found = angular.copy(ga);
                        found.hall = 'GA';
                        found.uid = ga.$id;
                    }
                });

                if (found) return found;

                angular.forEach(self.employees.ras, function (ra) {
                    if (found) return;
                    if (ra.name === name) {
                        found = angular.copy(ra);
                        found.uid = ra.$id;
                    }
                });

                return found;
            };

            this.formatDate = function (dateStr) {
                var dateObj = new Date(Date.parse(dateStr));
                var month = (dateObj.getMonth() + 1);
                var monthStr;
                if (month < 10) {
                    monthStr = '0' + month;
                } else {
                    monthStr = '' + month;
                }
                var dateOfMonth = dateObj.getDate();
                var dateOfMonthStr;
                if (dateOfMonth < 10) {
                    dateOfMonthStr = '0' + dateOfMonth;
                } else {
                    dateOfMonthStr = '' + dateOfMonth;
                }
                return dateObj.getFullYear() + '-' + monthStr + '-' + dateOfMonthStr;
            };
        }
    ]);
