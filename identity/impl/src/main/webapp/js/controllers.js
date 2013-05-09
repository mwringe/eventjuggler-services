'use strict';

function LoginCtrl($scope, config, $routeParams) {
    $scope.config = config;
    $scope.appKey = $routeParams.key;
    $scope.warning = $routeParams.warning;
    $scope.info = $routeParams.info;
}

function RegisterCtrl($scope, config, User, $location, $routeParams) {
    $scope.config = config;
    $scope.appKey = $routeParams.key;

    $scope.user = {};

    $scope.save = function() {
        User.save($scope.user, function() {
            $location.url("/login/" + $scope.appKey + "?info=created");
        }, function() {
            $scope.warning = "invalid";
        });
    };
}