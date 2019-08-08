app.controller("specificationController", function ($scope, $http, specificationService,$controller) {
    $controller("baseController", {$scope: $scope});

    $scope.findAll=function () {
        specificationService.findAll().success(function (res) {
            $scope.list=res;
        })
    };

    $scope.findPage=function (page, size) {
        specificationService.findPage(page,size).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };


    //新建
    $scope.entity = {
        spec:{},
        optionList:[]
    };
    $scope.save=function () {
        specificationService.save($scope.entity).success(function (res) {
            alert(res.message);
            if (res.success) {
                $scope.reloadList();
            }
        });
    };

    //新增规格
    $scope.addRow=function () {
        $scope.entity.optionList.push({})
    };
    //删除规格
    $scope.deleteRow=function (index) {
        $scope.entity.optionList.splice(index, 1);
    };


    //修改
    $scope.findOne = function (id) {
        specificationService.findOne(id).success(function (res) {
            $scope.entity = res;
        })
    };

    //删除
    $scope.delete = function () {
        if ($scope.ids.length == 0) {
            alert("至少选中一个")
        } else {
            specificationService.delete($scope.ids).success(function (res) {
                alert(res.message);
                if (res.success) {
                    $scope.reloadList();
                    $scope.ids = [];
                }
            })
        }
    }
});