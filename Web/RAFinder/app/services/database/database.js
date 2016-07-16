'use strict';
/**
 * A facade service to interact with the database.
 * ALL database read/write operations should be delegated through methods of this service.
 */
angular.module('RAFinder.services.database', [
    'firebase',
    'RAFinder.services.fileReader'
])
    .service('Database', [
        '$firebaseObject',
        '$firebaseArray',
        'Auth',
        'FileReader',
        'EnvConfig',
        function ($firebaseObject, $firebaseArray, Auth, FileReader, EnvConfig) {
            var firebase = new Firebase(EnvConfig.url);

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

            /**
             * Returns the synchronized list of RAs via a callback.
             *
             * @param {function} callback - the callback to utilize the data.
             */
            this.getRAs = function (callback) {
                if (self.employees.ras.length === 0) {
                    loadRAs(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.employees.ras);
                }
            };

            /**
             * Returns the synchronized list of SAs via a callback.
             *
             * @param {function} callback - the callback to utilize the data.
             */
            this.getSAs = function (callback) {
                if (self.employees.sas.length === 0) {
                    loadSAs(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.employees.sas);
                }
            };

            /**
             * Returns the synchronized list of GAs via a callback.
             *
             * @param {function} callback - the callback to utilize the data.
             */
            this.getGAs = function (callback) {
                if (self.employees.gas.length === 0) {
                    loadGAs(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.employees.gas);
                }
            };

            /**
             * Returns the synchronized list of Admins via a callback.
             *
             * @param {function} callback - the callback to utilize the data.
             */
            this.getAdmins = function (callback) {
                if (self.employees.admins.length === 0) {
                    loadAdmins(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.employees.admins);
                }
            };

            var normalizeHall = function (hall) {
                if (hall === 'APT. STYLE - EAST') {
                    return 'Apartments East';
                }
                if (hall === 'APT. STYLE - WEST') {
                    return 'Apartments West';
                }
                if (hall === 'BAUR-SAMES-BOGART HALL') {
                    return 'BSB';
                }
                if (hall === 'BLUMBERG HALL') {
                    return 'Blumberg';
                }
                if (hall === 'DEMING HALL') {
                    return 'Deming';
                }
                if (hall === 'MEES HALL') {
                    return 'Mees';
                }
                if (hall === 'PERCOPO HALL') {
                    return 'Percopo';
                }
                if (hall === 'SCHARPENBERG HALL') {
                    return 'Scharpenberg';
                }
                if (hall === 'LAKESIDE HALL') {
                    return 'Lakeside';
                }
                if (hall === 'SPEED HALL') {
                    return 'Speed';
                }
                return hall;
            };

            /**
             * Parses the given CSV of employee data.
             *
             * @param {*} data - the data to parse.
             * @param {boolean} overwrite - whether to overwrite the existing data.
             */
            this.parseEmployeeCsv = function (data, overwrite) {
                var ras = [];
                var sas = [];
                var gas = [];
                var admins = [];
                //var fraternityPresidents = [];

                FileReader.readCsv(data,
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
                        toAdd.floor = parseInt(toAdd.room.substring(0, 1), 10);
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
                            // don't remove admins
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

            /**
             * Adds the given employee to the Database.
             *
             * @param {String} employeeType - the type of employee being added.
             * @param {Object} user - the user to add
             * @param {String} user.email
             * @param {Number} user.floor
             * @param {String} user.hall
             * @param {String} user.name
             * @param {String} user.phoneNumber
             * @param {String} user.profilePicture
             * @param {String} user.room
             * @param {String} user.status
             * @param {String} user.statusDetail
             */
            // FIXME: this should be removed as soon as we have KERBEROS auth in place
            this.addEmployee = function (employeeType, user) {
                if (EnvConfig.env === 'prod') {
                    firebase.child('Employees/' + employeeType + '/')
                        .push(user, function (error) {
                            if (error !== null) {
                                console.error(error);
                                // TODO: show error to user?
                            }
                        });
                    return;
                }
                // Else, we're in dev; create a fake user
                var authObj = Auth.getAuthObject();
                authObj.$createUser({email: user.email, password: 'test1234'})
                    .then(function (authData) {
                        console.log('successfully created user: ' + authData.uid);
                        firebase.child('Employees/' + employeeType + '/' + authData.uid)
                            .set(user, function (error) {
                                if (error !== null) {
                                    console.error(error);
                                }
                            });
                    }, function (error) {
                        console.error('Error creating user: ' + error);
                    });
            };

            /**
             * Removes the given employee from the database.
             *
             * @param {String} type - the type of user being deleted.
             * @param {Object} person - the synchronized record to be deleted.
             */
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

                if (EnvConfig.env === 'prod') {
                    return;
                }
                if (person.email.endsWith('@test.com')) {
                    // This is a test entity created by a demo of the app; remove it
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

            /**
             * Returns the synchronized list of Residence Halls via a callback.
             *
             * @param {function} callback - the callback to utilize the data.
             */
            this.getResHalls = function (callback) {
                if (self.resHalls.length === 0) {
                    loadResHalls(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.resHalls);
                }
            };

            var numberToOrdinal = function (number) {
                switch (number) {
                case '1':
                    return '1st';
                case '2':
                    return '2nd';
                default:
                    return number + 'th';
                }
            };

            this.parseHallRosterCsv = function (data) {
                var residents = [];

                var dataCallback = function (lineData) {
                    var toAdd = angular.copy(lineData);
                    console.log(toAdd);

                    residents.push({
                        resident: {
                            name: toAdd.NAME,
                            type: toAdd.TYPE,
                            image: toAdd.IMAGE
                        },
                        hall: normalizeHall(toAdd.HALL),
                        floor: numberToOrdinal(toAdd.FLOOR),
                        room: toAdd.ROOM
                    });

                    console.log(residents);
                };

                var endCallback = function () {
                    var editedRooms = {};
                    console.log(self.resHalls);
                    angular.forEach(residents, function (value) {
                        console.log(value);
                        angular.forEach(self.resHalls, function (hall) {
                            if (hall.hall !== value.hall) {
                                return;
                            }
                            angular.forEach(hall.floors, function (floor) {
                                if (floor.floor !== value.floor) {
                                    return;
                                }
                                angular.forEach(floor.rooms, function (room) {
                                    if (room.number !== value.room) {
                                        return;
                                    }
                                    var roomData = hall.hall + floor.floor + room.number;
                                    console.log(editedRooms[roomData]);
                                    if (!editedRooms[roomData]) {
                                        editedRooms[roomData] = true;
                                        room.residents = [value.resident];
                                    } else {
                                        room.residents.push(value.resident);
                                    }
                                });
                            });
                        });
                    });
                    self.resHalls.$save(self.resHalls);
                };

                FileReader.readCsv(data, dataCallback, endCallback);
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

            /**
             * Returns the synchronized list of Duty Rosters via a callback.
             *
             * @param {function} callback - the callback to utilize the data.
             */
            this.getDutyRosters = function (callback) {
                if (self.dutyRosters.length === 0) {
                    loadDutyRosters(function (data) {
                        callback(data);
                    });
                } else {
                    callback(self.dutyRosters);
                }
            };

            /**
             * Adds the given Duty Roster Item to the database.
             *
             * @param {Date|String} date - the date at which to add the roster.
             * @param {Object[]} roster - The roster to add.
             */
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

            /**
             * Edits the given Duty Roster item.
             *
             * @param {Object} roster - the roster to add.
             */
            this.editDutyRosterItem = function (roster) {
                self.dutyRosters.$save(roster);
            };

            /**
             * Parses the given Duty Roster file.
             *
             * @param {*} data - the data to parse.
             * @param {boolean} overwrite - whether to overwrite the existing data.
             */
            this.parseDutyRosterFile = function (data, overwrite) {
                var rosters = [];
                var roster = {
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
                    self.getAdmins(function () {
                        if (self.employees.ras.length === 0) {
                            self.getRAs(function () {
                                if (self.employees.gas.length === 0) {
                                    self.getGAs(function () {
                                        FileReader.readLines(data, dataCallback, endCallback);
                                    });
                                }
                            });
                        }
                    });
                }
                var dataCallback = function (line) {
                    if (count === rollover) {
                        count = 0;
                        rosters.push(angular.copy(roster));
                        roster = {
                            date: '',
                            roster: []
                        };
                        nTriplets = 0;
                    }
                    if (headers.hasOwnProperty(line)) {
                        return;
                    }
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
                                nTriplets += 1;
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
                    count += 1;
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
                    if (found) {
                        return;
                    }
                    if (admin.name === name) {
                        found = angular.copy(admin);
                        found.hall = 'OCP';
                        found.uid = admin.$id;
                    }
                });

                if (found) {
                    return found;
                }

                angular.forEach(self.employees.gas, function (ga) {
                    if (found) {
                        return;
                    }
                    if (ga.name === name) {
                        found = angular.copy(ga);
                        found.hall = 'GA';
                        found.uid = ga.$id;
                    }
                });

                if (found) {
                    return found;
                }

                angular.forEach(self.employees.ras, function (ra) {
                    if (found) {
                        return;
                    }
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
        }]);
