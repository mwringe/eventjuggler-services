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

eventjugglerServices.factory('Provider', function($resource) {
    return $resource('/ejs-identity/api/admin/providers');
});

eventjugglerServices.factory('User', function($resource) {
    return $resource('/ejs-identity/api/im/users/:userId', {
        userId : '@userId'
    }, {
        save : {
            method : 'PUT'
        }
    });
});

eventjugglerServices.service('Auth', function($resource, $http, $location) {
    var token = $location.search().token;
    if (!token) {
        token = localStorage.getItem("token");
    }

    var auth = {};
    auth.user;

    auth.logout = function() {
        $resource('/ejs-identity/api/auth/logout').get();
        
        localStorage.removeItem("token");
        $http.defaults.headers.common['token'] = null;
        console.debug("logged out");

        auth.loggedIn = false;
        auth.root = false;
    };

    if (!auth.user && token) {
        auth.user = $resource('/ejs-identity/api/auth/userinfo').get({
            token : token
        }, function() {
            if (auth.user.userId) {
                localStorage.setItem("token", token);
                $http.defaults.headers.common['token'] = token;

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

                console.debug("logged in " + (auth.root ? "root" : "user"));
            } else {
                auth.logout();
            }
        }, function() {
            auth.logout();
        });
    }

    return auth;
});