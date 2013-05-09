'use strict';

var identityBrokerModule = angular.module('identityBroker', [ 'identityBrokerServices' ]);

identityBrokerModule.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/login/:key', {
        templateUrl : 'partials/login.html',
        resolve : {
            config : function(ConfigLoader) {
                return ConfigLoader();
            }
        },
        controller : LoginCtrl
    }).when('/register/:key', {
        templateUrl : 'partials/register.html',
        resolve : {
            config : function(ConfigLoader) {
                return ConfigLoader();
            }
        },
        controller : RegisterCtrl
    }).otherwise({
        templateUrl : 'partials/failure.html'
    });
} ]);