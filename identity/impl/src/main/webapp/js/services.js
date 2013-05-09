'use strict';

var identityBrokerServices = angular.module("identityBrokerServices", [ "ngResource" ]);

identityBrokerServices.factory("Config", function($resource) {
    return $resource("/ejs-identity/api/login/:appKey");
});

identityBrokerServices.factory('ConfigLoader', function(Config, $route, $q) {
    return function() {
        var delay = $q.defer();
        Config.get({
            appKey : $route.current.params.key
        }, function(config) {
            delay.resolve(config);
        }, function() {
            delay.reject('Unable to fetch config ' + $route.current.params.userId);
        });
        return delay.promise;
    };
});

identityBrokerServices.factory("User", function($resource) {
    return $resource("/ejs-identity/api/im/users/:userId", {
        userId : "@userId"
    }, {
        save : {
            method : "PUT"
        }
    });
});