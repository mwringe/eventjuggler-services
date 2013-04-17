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

    $scope.addProvider = function(providerId) {
        if (!$scope.application.providers) {
            $scope.application.providers = [];
        }

        $scope.application.providers.push({
            "providerId" : providerId
        });
    }

    $scope.getProviderDescription = function(providerId) {
        for ( var i = 0; i < $scope.providers.length; i++) {
            if ($scope.providers[i].id == providerId) {
                return $scope.providers[i];
            }
        }
    }

    $scope.removeProvider = function(i) {
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