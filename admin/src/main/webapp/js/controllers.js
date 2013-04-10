'use strict';

function WelcomeCtrl($scope) {
    $scope.message = "Hello world";
}

function ApplicationListCtrl($scope, Application, $routeParams) {
    $scope.applications = Application.query();
}

function ApplicationDetailCtrl($scope, Application, Provider, $routeParams, $location) {
    var navigationToApplications = function() {
        $location.url("/applications");
    };

    if ($routeParams.key == "new") {
        $scope.application = {};
    } else {
        $scope.application = Application.get({
            "key" : $routeParams.key
        });
    }
    
    $scope.providers = Provider.query();

    $scope.addProvider = function() {
        if (!$scope.application.providers) {
            $scope.application.providers = [];
        }
        $scope.application.providers.push({
            "provider" : $scope.newProvider
        });
    }

    $scope.removeProvider = function(provider) {
        var i = $scope.application.providers.indexOf(provider);
        $scope.application.providers.splice(i, 1);
    }

    $scope.save = function() {
        Application.save($scope.application, navigationToApplications);
    };

    $scope.update = function() {
        Application.update({
            "key" : $scope.application.key
        }, $scope.application, navigationToApplications);
    }
    
    $scope.cancel = function() {
        navigationToApplications();
    }

    $scope.remove = function() {
        $scope.application.$remove(navigationToApplications);
    }
}