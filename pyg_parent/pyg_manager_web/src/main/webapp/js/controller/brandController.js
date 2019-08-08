app.controller("brandController", function ($scope, $http, brandService,$controller) {
    $controller("baseController", {$scope: $scope});

    $scope.findAll=function () {
        brandService.findAll().success(function (res) {
            $scope.list=res;
        })
    };

    $scope.findPage=function (page, size) {
        brandService.findPage(page,size).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //新建
    $scope.entity = {};
    $scope.save=function () {
        brandService.save($scope.entity).success(function (res) {
            alert(res.message);
            if (res.success) {
                $scope.reloadList();
            }
        });
    };


    //修改
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (res) {
            $scope.entity = res;
        })
    };

    //删除
    $scope.delete = function () {
        if ($scope.ids.length == 0) {
            alert("至少选中一个")
        } else {
            brandService.delete($scope.ids).success(function (res) {
                alert(res.message);
                if (res.success) {
                    $scope.reloadList();
                    $scope.ids = [];
                }
            })
        }
    }
});