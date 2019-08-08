app.controller("contentController", function ($scope, $http, uploadService, contentService, contentCategoryService, $controller) {
    $controller("baseController", {$scope: $scope});

    $scope.findAll = function () {
        contentService.findAll().success(function (res) {
            $scope.list = res;
        })
    };

    $scope.findPage = function (page, size) {
        contentService.findPage(page, size).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //新建
    $scope.entity = {};
    $scope.save = function () {
        contentService.save($scope.entity).success(function (res) {
            alert(res.message);
            if (res.success) {
                $scope.reloadList();
            }
        });
    };


    //修改
    $scope.findOne = function (id) {
        contentService.findOne(id).success(function (res) {
            $scope.entity = res;
        })
    };

    //删除
    $scope.delete = function () {
        if ($scope.ids.length == 0) {
            alert("至少选中一个")
        } else {
            contentService.delete($scope.ids).success(function (res) {
                alert(res.message);
                if (res.success) {
                    $scope.reloadList();
                    $scope.ids = [];
                }
            })
        }
    };

    //初始化函数,查出所有广告分类
    $scope.findContent = function () {
        contentCategoryService.findAll().success(function (res) {
            $scope.contentList = res;
        })
    };

    //上传图片
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (res) {
            if(res.success) {
                $scope.entity.pic=res.message;
            }else {
                alert(res.message);
            }
        })
    };
    //状态数组
    $scope.status = ["无效", "有效"];
});