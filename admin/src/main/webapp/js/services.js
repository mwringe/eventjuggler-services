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

    console.debug("token = " + token);

    var user;

    if (!user && token) {
        console.debug("loading user");

        var userInfoRes = $resource('/ejs-identity/api/auth/userinfo');
        user = userInfoRes.get({
            token : token
        }, function() {
            $http.defaults.headers.common['token'] = token;
            console.debug("logged in");
        }, function() {
            console.debug("logged out");
            localStorage.removeItem("token");
            $http.defaults.headers.common['token'] = null;
        });
    }

    return user;
});