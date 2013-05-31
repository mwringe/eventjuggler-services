'use strict';

var eventjugglerServices = angular.module('eventjugglerAdminServices', [ 'ngResource' ]);

eventjugglerServices.factory('Application', function($resource) {
    return $resource('/ejs-identity/api/admin/applications/:key', {
        key : '@key'
    }, {
        update : {
            method : 'PUT'
        }
    });
});

eventjugglerServices.factory('ApplicationListLoader', function(Application, $q) {
    return function() {
        var delay = $q.defer();
        Application.query(function(applications) {
            delay.resolve(applications);
        }, function() {
            delay.reject('Unable to fetch applications');
        });
        return delay.promise;
    };
});

eventjugglerServices.factory('ApplicationLoader', function(Application, $route, $q) {
    return function() {
        var delay = $q.defer();
        Application.get({
            key : $route.current.params.key
        }, function(application) {
            delay.resolve(application);
        }, function() {
            delay.reject('Unable to fetch application ' + $route.current.params.recipeId);
        });
        return delay.promise;
    };
});

eventjugglerServices.factory('Provider', function($resource) {
    return $resource('/ejs-identity/api/admin/providers');
});

eventjugglerServices.factory('ProviderListLoader', function(Provider, $q) {
    return function() {
        var delay = $q.defer();
        Provider.query(function(providers) {
            delay.resolve(providers);
        }, function() {
            delay.reject('Unable to fetch providers');
        });
        return delay.promise;
    };
});

eventjugglerServices.factory('Realms', function($resource) {
    var realms = {};
    realms.query = function() {
        return [ "applications", "applications2", "default", "dummy-social", "system" ];
    };
    return realms;
});

eventjugglerServices.factory('RealmsLoader', function(Realms) {
    return function() {
        return Realms.query();
    };
});

eventjugglerServices.factory('RealmLoader', function($route) {
    return function() {
        return $route.current.params.realmId;
    };
});

eventjugglerServices.factory('User', function($resource) {
    return $resource('/ejs-identity/api/im/:realmId/users/:userId', {
        realmId : '@realmId',
        userId : '@userId'
    }, {
        save : {
            method : 'PUT'
        }
    });
});

eventjugglerServices.factory('UserListLoader', function(User, $route, $q) {
    return function() {
        var delay = $q.defer();
        User.query({
            realmId : $route.current.params.realmId
        }, function(users) {
            delay.resolve(users);
        }, function() {
            delay.reject('Unable to fetch users');
        });
        return delay.promise;
    };
});

eventjugglerServices.factory('UserLoader', function(User, $route, $q) {
    return function() {
        var delay = $q.defer();
        User.get({
            realmId : $route.current.params.realmId,
            userId : $route.current.params.userId
        }, function(user) {
            delay.resolve(user);
        }, function() {
            delay.reject('Unable to fetch user ' + $route.current.params.userId);
        });
        return delay.promise;
    };
});

eventjugglerServices.factory('Activities', function($resource) {
    var activities = {};
    activities.events = $resource('/ejs-activities/api/events');
    activities.statistics = $resource('/ejs-activities/api/statistics');
    return activities;
});

eventjugglerServices.factory('ActivitiesStatisticsLoader', function(Activities, $q) {
    return function() {
        var delay = $q.defer();
        Activities.statistics.get(function(statistics) {
            delay.resolve(statistics);
        }, function() {
            delay.reject('Unable to fetch statistics');
        });
        return delay.promise;
    };
});

eventjugglerServices.factory('ActivitiesEventsLoader', function(Activities, $q) {
    return function() {
        var delay = $q.defer();
        Activities.events.query({
            "max" : 10
        }, function(events) {
            delay.resolve(events);
        }, function() {
            delay.reject('Unable to fetch events');
        });
        return delay.promise;
    };
});

eventjugglerServices.service('Auth', function($resource, $http, $location) {
    var auth = {};
    auth.user = {};

    auth.token = $location.search().token;
    if (auth.token) {
        $location.search("token", null);
        localStorage.setItem("token", auth.token);
    } else {
        auth.token = localStorage.getItem("token");
    }

    if (auth.token) {
        $http.defaults.headers.common['token'] = auth.token;

        auth.user = $resource('/ejs-identity/api/auth/userinfo').get({
            appKey : "system",
            token : auth.token
        }, function() {
            if (auth.user.userId) {
                auth.loggedIn = true;
                auth.root = auth.user.userId == "root";

                var displayName;
                if (auth.user.firstName || auth.user.lastName) {
                    displayName = auth.user.firstName;
                    if (auth.user.lastName) {
                        displayName = displayName ? displayName + " " + auth.user.lastName : auth.user.lastName;
                    }
                } else {
                    displayName = auth.user.userId;
                }

                auth.user.displayName = displayName;
            } else {
                auth.logout();
            }
        }, function() {
            auth.logout();
        });
    }

    auth.logout = function() {
        $resource('/ejs-identity/api/auth/logout').get({
            appKey : "system"
        });

        localStorage.removeItem("token");
        $http.defaults.headers.common['token'] = null;

        auth.loggedIn = false;
        auth.root = false;

        $location.url("/");
    };

    return auth;
});