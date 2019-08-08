app.controller("contentController", function ($scope, $http, contentService, $controller) {
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

    //查找所有有效的广告
    $scope.contentList = [];
    $scope.findContentList=function (categoryId) {
        contentService.findContentList(categoryId).success(function (res) {
            $scope.contentList[categoryId]=res;
        })
    }

    $scope.keywords='';
    $scope.search=function () {
        if($scope.keywords==''){
            $scope.keywords='华为'
        }
        location.href='http://search.pinyougou.com/search.html#?keywords='+$scope.keywords
    }
});