app.controller("typeTemplateController", function ($scope, $http,brandService,specificationService, typeTemplateService, $controller) {
    $controller("baseController", {$scope: $scope});

    $scope.findAll=function () {
        typeTemplateService.findAll().success(function (res) {
            $scope.list=res;
        })
    };

    $scope.findPage = function (page, size) {
        typeTemplateService.findPage(page, size).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };


    //新建
    $scope.entity = {customAttributeItems:[]};
    $scope.save = function () {
        typeTemplateService.save($scope.entity).success(function (res) {
            alert(res.message);
            if (res.success) {
                $scope.reloadList();
            }
        });
    };

    //增加扩展属性
    $scope.addRow=function () {
        $scope.entity.customAttributeItems.push({})
    };

    //删除扩展属性
    $scope.deleteRow=function (item) {
        var index=$scope.entity.customAttributeItems.indexOf(item);
        $scope.entity.customAttributeItems.splice(index,1);
    };


    //修改
    $scope.findOne = function (id) {
        typeTemplateService.findOne(id).success(function (res) {
            $scope.entity=res;
            $scope.entity.brandIds =JSON.parse(res.brandIds);
            $scope.entity.specIds =JSON.parse(res.specIds);
            $scope.entity.customAttributeItems =JSON.parse(res.customAttributeItems);
        })
    };

    //删除
    $scope.delete = function () {
        if ($scope.ids.length == 0) {
            alert("至少选中一个")
        } else {
            typeTemplateService.delete($scope.ids).success(function (res) {
                alert(res.message);
                if (res.success) {
                    $scope.reloadList();
                    $scope.ids = [];
                }
            })
        }
    };

    //json字符串转数组,拿到对应key值后再转字符串
    $scope.jsonToString = function (str, key) {
        var arr = JSON.parse(str);
        var result = [];
        for (var i = 0; i < arr.length; i++) {
            result.push(arr[i][key]);
        }
        return result.join();
    };

    // // 初始化方法,查出所有品牌和规格
    $scope.brandList = {data:[]};
    $scope.specList = {data:[]};
    $scope.initList = function () {
        brandService.findAll().success(function (res) {
            $scope.brandList.data =res;
        });
        specificationService.findAll().success(function (res) {
            $scope.specList.data=res;
        })
    }
});