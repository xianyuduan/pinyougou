app.controller("baseController", function ($scope, $http) {
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 5,
        perPageOptions: [3, 5, 10],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    //重新加载列表 数据
    $scope.reloadList = function () {
        //切换页码
        $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //获取复选框选中
    $scope.ids = [];
    $scope.deleteIds = function ($event, id) {
        if ($event.target.checked) {
            $scope.ids.push(id);
        } else {
            var index = $scope.ids.indexOf(id);
            $scope.ids.splice(index, 1)
        }
    };


});

app.filter("trustHtml",function ($sce) {
    return function (text) {
        return $sce.trustAsHtml(text);
    }
})