'use strict';

var eventjugglerModule = angular.module('eventjugglerAdmin', [ 'eventjugglerAdminServices' ]);

eventjugglerModule.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/welcome', {
        templateUrl : 'partials/welcome.html',
        controller : WelcomeCtrl
    }).otherwise({
        templateUrl : 'partials/welcome.html',
        controller : WelcomeCtrl
    });
} ]);