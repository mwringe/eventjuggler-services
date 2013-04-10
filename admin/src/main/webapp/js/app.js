'use strict';

var eventjugglerModule = angular.module('eventjugglerAdmin', [ 'eventjugglerAdminServices' ]);

eventjugglerModule.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/applications/:key', {
        templateUrl : 'partials/application-detail.html',
        controller : ApplicationDetailCtrl
    }).when('/applications', {
        templateUrl : 'partials/application-list.html',
        controller : ApplicationListCtrl
    }).otherwise({
        templateUrl : 'partials/home.html',
        controller : WelcomeCtrl
    });
} ]);