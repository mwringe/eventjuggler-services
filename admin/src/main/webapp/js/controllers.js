'use strict';

function WelcomeCtrl($scope) {
    $scope.message = "Hello world";
}

function ApplicationListCtrl($scope, Application, $routeParams) {
    $scope.applications = Application.query();
}

function ApplicationDetailCtrl($scope, Application, $routeParams, $location) {
    if ($routeParams.key == "create") {
        $scope.application = {};
    } else {
        $scope.application = Application.get({
            "key" : $routeParams.key
        });
    }

    $scope.addProvider = function() {
        if (!$scope.application.providers) {
            $scope.application.providers = [];
        }
        $scope.application.providers.push({});
    }

    $scope.removeProvider = function(provider) {
        var i = $scope.application.providers.indexOf(provider);
        $scope.application.providers.splice(i, 1);
    }
    
    $scope.save = function() {
        if (!$scope.application.key) {
            Application.save($scope.application);
        } else {
            Application.update({
                "key" : $scope.application.key
            }, $scope.application);
        }
        
        $location.url("/applications");
    };
    
    $scope.remove = function() {
        $scope.application.$remove();
        $location.url("/applications");
    }
}