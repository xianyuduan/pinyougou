app.controller("itemCatController", function ($scope, $http, typeTemplateService,itemCatService,$controller) {
    $controller("baseController", {$scope: $scope});
    $scope.findAll=function () {
        itemCatService.findAll().success(function (res) {
            $scope.list=res;
        })
    };

    //初始化
    //商品类型模板
    $scope.template = {};
    $scope.typeTemples = {data:[]};
    $scope.initList=function () {
        itemCatService.findByParentId(0).success(function (res) {
            $scope.list=res;
        });
        typeTemplateService.findAll().success(function (res) {
            $scope.typeTemples.data=res;
        })
    };

    //按父id查
    $scope.grade=1;
    $scope.itemCat1 = {};
    $scope.itemCat2 = {};
    $scope.parentItemShow = "顶级分类";
    $scope.parentId=0;
    $scope.item = {id:0};
    $scope.findByParentId=function (item) {
        $scope.item=item;
        itemCatService.findByParentId(item.id).success(function (res) {
            $scope.parentId=item.id;
            $scope.list=res;
            if($scope.grade==1) {
                $scope.parentItemShow ="顶级分类";
                $scope.itemCat1 = {};
                $scope.itemCat2 = {};
            }else if($scope.grade==2){
                $scope.parentItemShow =item.name;
                $scope.itemCat1 = item;
                $scope.itemCat2 = {};
            }else if($scope.grade==3){
                $scope.itemCat2 = item;
                $scope.parentItemShow =$scope.itemCat1.name+">>"+item.name;
            }
        })
    };

    $scope.setGrade=function (i) {
        $scope.grade=i;
    };

    //刷新
    $scope.reload=function () {
        $scope.findByParentId($scope.item);
    };
    //新建
    $scope.entity = {};
    $scope.save=function () {
        $scope.entity.typeId=$scope.template.id;
        $scope.entity.parentId=$scope.parentId;
        itemCatService.save($scope.entity).success(function (res) {
            alert(res.message);
            if (res.success) {
                $scope.reload();
            }
        });
    };




    //修改
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(function (res) {
            $scope.entity = res;
            $scope.template = null;
            typeTemplateService.findOne($scope.entity.typeId).success(function (response) {
                $scope.template =response;
            })
        })
    };

    //删除
    $scope.delete = function () {
        if ($scope.ids.length == 0) {
            alert("至少选中一个")
        } else {
            itemCatService.delete($scope.ids).success(function (res) {
                alert(res.message);
                if (res.success) {
                    $scope.reload();
                    $scope.ids = [];
                }
            })
        }
    }
});