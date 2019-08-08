//控制层 
app.controller('goodsController',
    function ($scope, $controller, goodsService, itemCatService, typeTemplateService, uploadService, specificationService,$location) {

        $controller('baseController', {
            $scope: $scope
        });// 继承

        // 读取列表数据绑定到表单中
        $scope.findAll = function () {
            goodsService.findAll().success(function (response) {
                $scope.list = response;
            });
        };

        $scope.findPage = function (page, size) {
            goodsService.findPage(page, size).success(
                function (response) {
                    $scope.list = response.rows;
                    $scope.paginationConf.totalItems = response.total;//更新总记录数
                }
            );
        };

        // 查询实体

        $scope.findOne = function () {
            goodsService.findOne($location.search().id).success(function (response) {
                $scope.entity = response;
                $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                $scope.specName = '';
                for(var i=0;i<$scope.entity.itemList.length;i++) {
                    $scope.specName+=$scope.entity.itemList[i].spec
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
                }
                editor.html($scope.entity.goodsDesc.introduction);
                itemCatService.findByParentId($scope.entity.tbGoods.category1Id).success(function (res) {
                    $scope.category2 = res;
                });
                itemCatService.findByParentId($scope.entity.tbGoods.category2Id).success(function (res) {
                    $scope.category3 = res;
                });
            });
        };

        // 保存
        $scope.save = function () {
            $scope.entity.goodsDesc.introduction = editor.html();
            goodsService.add($scope.entity).success(function (response) {
                alert(response.message);
                if (response.success) {
                    // 跳转到商品登录页面
                    location.href = "/admin/goods_edit.html";
                }
            });
        };

        // 批量删除
        $scope.dele = function () {
            // 获取选中的复选框
            goodsService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();// 刷新列表
                        $scope.selectIds = [];
                    }
                });
        };

        $scope.searchEntity = {};// 定义搜索对象

        // 搜索
        $scope.search = function (page, rows) {
            goodsService.search(page, rows, $scope.searchEntity).success(
                function (response) {
                    $scope.list = response.rows;
                    $scope.paginationConf.totalItems = response.total;// 更新总记录数
                });
        };
        $scope.entity = {
            tbGoods: {},
            goodsDesc: {itemImages: [], customAttributeItems: [], specificationItems: []},
            itemList: [{spec: {}, price: 100, num: 9999, status: 0, isDefault: 0}]
        };

        //一级分类列表
        $scope.findCategory1 = function (parentId) {
            itemCatService.findByParentId(parentId).success(function (res) {
                $scope.category1 = res;
                if($location.search().id!=undefined) {
                    $scope.findOne();
                }
                //$scope.category2 = [];
                //$scope.category3 = [];
                //$scope.entity.tbGoods.typeTemplateId = "";
                //$scope.brandList = [];
            })
        };

        //二级分类列表
        $scope.$watch('entity.tbGoods.category1Id', function (newValue, oldValue) {
            if(oldValue!=undefined) {
                if (newValue == -1) {
                    $scope.category2 = [];
                    $scope.entity.tbGoods.category2Id = -1;
                } else {
                    if (newValue != undefined) {
                        itemCatService.findByParentId(newValue).success(function (res) {
                            $scope.category2 = res;
                            //$scope.category3 = [];
                            //$scope.entity.tbGoods.typeTemplateId = "";
                            //$scope.brandList = [];
                            $scope.entity.tbGoods.category2Id = -1;
                        })
                    }
                }
            }
        });

        //三级分类列表
        $scope.$watch("entity.tbGoods.category2Id", function (newValue, oldValue) {
            if(oldValue!=undefined) {
                if (newValue != undefined) {
                    itemCatService.findByParentId(newValue).success(function (res) {
                        $scope.category3 = res;
                        //$scope.entity.tbGoods.typeTemplateId = "";
                        //$scope.brandList = [];
                        $scope.entity.tbGoods.category3Id = -1;
                    })
                }
            }
        });

        //模板
        $scope.$watch("entity.tbGoods.category3Id", function (newValue, oldValue) {
            if(oldValue!=undefined) {
                if (newValue != undefined) {
                    itemCatService.findOne(newValue).success(function (res) {
                        $scope.entity.tbGoods.typeTemplateId = res.typeId;
                        $scope.entity.tbGoods.brandId = -1;
                    })
                }
            }

        });

        //品牌
        $scope.$watch("entity.tbGoods.typeTemplateId", function (newValue, oldValue) {
                if (newValue != undefined && newValue != "") {
                    typeTemplateService.findOne(newValue).success(function (res) {
                        //品牌列表
                        $scope.brandList = JSON.parse(res.brandIds);
                        //扩展属性
                        if(oldValue!=undefined) {
                            $scope.entity.goodsDesc.customAttributeItems = JSON.parse(res.customAttributeItems);

                        }
                    });
                    //按模板id查规格
                    specificationService.findByTypeTemplateId(newValue).success(function (res) {
                        $scope.specList = res;
                    });
                    if(oldValue!=undefined) {
                        $scope.entity.tbGoods.isEnableSpec = 0;
                    }else {
                        $scope.entity.tbGoods.isEnableSpec = 1;
                    }

                }
        });

        //上传文件
        $scope.img = {url: '', color: ''};
        $scope.uploadFile = function () {
            uploadService.uploadFile().success(function (res) {
                if (res.success) {
                    $scope.img.url = res.message;
                } else {
                    alert(res.message);
                }
            })
        };

        //增加照片
        $scope.addImg = function () {
            if ($scope.img.url == null) {
                alert("请上传图片")
            } else {
                $scope.entity.goodsDesc.itemImages.push($scope.img)
            }
        };

        //删除照片
        $scope.delImg = function (index) {
            $scope.entity.goodsDesc.itemImages.splice(index, 1);
        };

        //获取选中的规格,绑定到entity中
        $scope.selectSpecs = function ($event, attributeName, attributeValue) {
            var object = $scope.selectObjectsByKey($scope.entity.goodsDesc.specificationItems, "attributeName", attributeName);
            if (object != null) {//规格数组中有该规格
                if ($event.target.checked) {//选中
                    object.attributeValue.push(attributeValue);
                } else {
                    var index = object.attributeValue.indexOf(attributeValue);
                    object.attributeValue.splice(index, 1);
                    if (object.attributeValue.length == 0) {//规格中属性删完了.删掉该规格
                        var index = $scope.entity.goodsDesc.specificationItems.indexOf(object);
                        $scope.entity.goodsDesc.specificationItems.splice(index, 1);
                    }
                }
            } else {//规格数组中没有该规格
                $scope.entity.goodsDesc.specificationItems.push({
                    "attributeName": attributeName,
                    "attributeValue": [attributeValue]
                });
            }

        };


        //创建存储sku列表的数组
        $scope.createItemList = function () {
            $scope.entity.itemList = [{spec: {}, price: 100, num: 9999, status: 0, isDefault: 0}];
            //循环规格数组,分别和itemList合并
            for (var i = 0; i < $scope.entity.goodsDesc.specificationItems.length; i++) {
                $scope.entity.itemList = $scope.addColumn($scope.entity.goodsDesc.specificationItems[i], $scope.entity.itemList);
            }
            //alert(JSON.stringify($scope.entity.itemList))
        };


        //合并函数
        $scope.addColumn = function (specItems, itemList) {
            //建一个新数组,存储合并后的数组
            var newItemList = [];
            //循环itemList
            for (var i = 0; i < itemList.length; i++) {
                //循环specItems.attributeValue
                for (var j = 0; j < specItems.attributeValue.length; j++) {
                    //构造合并后数组的元素
                    //深克隆原来数组,相当于直接将itemList赋值给新数组元素,再将specItems合并到该数组
                    var newItem = JSON.parse(JSON.stringify(itemList[i]));
                    newItem.spec[specItems.attributeName] = specItems.attributeValue[j];
                    newItemList.push(newItem);
                }
            }
            return newItemList;
        };

        //点击启用规格是清空上次的SpecAndItem
        $scope.initSpecAndItem = function () {
            $scope.entity.itemList = [{spec: {}, price: 100, num: 9999, status: 0, isDefault: 0}];
            $scope.entity.goodsDesc.specificationItems = [];
        };

        //状态列表
        $scope.status = ["未申请", "申请中", "审核通过", "已驳回"];
        //上架状态列表
        $scope.isMarketable = ["未上架", "已上架"];

        //分类列表
        $scope.category = [];
        $scope.findCategory = function () {
            itemCatService.findAll().success(function (res) {
                for (var i = 0; i < res.length; i++) {
                    $scope.category[res[i].id] = res[i].name;
                }
            })
        };

        //上下架
        $scope.updateMarketable = function (status) {
            if ($scope.selectIds.length == 0) {
                alert("至少选择一个")
            } else {
                goodsService.updateMarketable($scope.selectIds, status).success(function (res) {
                    alert(res.message);
                    if (res.success) {
                        $scope.reloadList();
                        $scope.selectIds = [];
                    }
                })
            }
        }
        $scope.toEdit=function (id) {
            location.href="goods_edit.html#?id="+id;
        };
        $scope.goBack=function (num) {
            location.href="goods.html";
        }
        $scope.specContain=function (name) {
            if($scope.specName.indexOf(name)>=0) {
                return true;
            }else {
                return false;
            }
        }
    });
