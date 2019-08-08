app.controller("searchController", function ($scope, $http, searchService, $controller,$location) {
    $controller("baseController", {$scope: $scope});

    //传入后台参数
    $scope.searchEntity = {keywords: "三星", brand: '', category: '', spec: {}, price: '',sort:'ASC',sortField:'item_price',page:1,size:10};
    //从后台接收参数
    $scope.searchResult = {itemList: []};
    //初始化
    $scope.searchInit = function () {
        $scope.searchEntity.keywords=$location.search().keywords;
        $scope.search();
    };

    //在searchEntity中添加过滤条件
    $scope.addSearchEntity = function (key, value) {
        $scope.searchEntity[key] = value;
        $scope.searchEntity.page=1;
        $scope.search();
    };

    //在searchEntity中添加规格过滤条件
    $scope.addSearchEntitySpec = function (key, value) {
        $scope.searchEntity.spec[key] = value;
        $scope.searchEntity.page=1;
        $scope.search();
    };

    //清空过滤条件
    $scope.deleteSearchEntity = function (searchName) {
        $scope.searchEntity[searchName] = '';
        $scope.searchEntity.page=1;
        $scope.search();
    };
    //清空规格过滤条件
    $scope.deleteSearchEntitySpec = function (key) {
        delete $scope.searchEntity.spec[key];
        $scope.searchEntity.page=1;
        $scope.search();
    };

    //查询函数
    $scope.search = function () {
        searchService.search($scope.searchEntity).success(function (res) {
            $scope.searchResult = res;
            $scope.buildPageNum();
        })
    };

    //排序函数
    $scope.sort=function (sortField) {
        $scope.searchEntity.sortField=sortField;
        $scope.searchEntity.page=1;
        if($scope.searchEntity.sort=='ASC'){
            $scope.searchEntity.sort = 'DESC';
        }else {
            $scope.searchEntity.sort = 'ASC';
        }
        $scope.search();
    };

    //分页
    $scope.pageNum = [];
    $scope.buildPageNum=function () {
        var start=$scope.searchEntity.page-2;
        var end=$scope.searchEntity.page+2;
        if(end>$scope.searchResult.totalPages) {
            end=$scope.searchResult.totalPages;
            start=end-4;
        }
        if(start<1) {
            start=1;
            end=5>$scope.searchResult.totalPages?$scope.searchResult.totalPages:5;
        }
        var index=0;
        $scope.pageNum = [];
        for(var i=start;i<=end;i++) {
            $scope.pageNum[index++]=i;
        }
    };
    //改变当前页
    $scope.changeCurrentPage=function (num) {
        if(num>=1 && num<=$scope.searchResult.totalPages) {
            $scope.searchEntity.page=num*1;
            $scope.search();
        }
        $scope.toPage=$scope.searchEntity.page;
    }
});