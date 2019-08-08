app.controller("indexController", function ($scope, $http, indexService,$controller) {
    $controller("baseController", {$scope: $scope});

    $scope.username = "";
    $scope.findLoginUser=function () {
        indexService.findLoginUser().success(function (res) {
            $scope.username=res.username;
        })
    }

});