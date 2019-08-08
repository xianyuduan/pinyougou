<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7; IE=EDGE">
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>
    <title>产品详情页</title>
    <link rel="icon" href="assets/img/favicon.ico">

    <link rel="stylesheet" type="text/css" href="css/webbase.css"/>
    <link rel="stylesheet" type="text/css" href="css/pages-item.css"/>
    <link rel="stylesheet" type="text/css" href="css/pages-zoom.css"/>
    <link rel="stylesheet" type="text/css" href="css/widget-cartPanelView.css"/>
    <script>
        var spec=${item.spec}
        var itemList=[
            <#list items as item >
                {id:${item.id?c},spec:${item.spec}},
            </#list>
        ]
    </script>
    <script src="./plugins/angularjs/angular.min.js"></script>
    <script src="./js/base.js"></script>
    <script src="./js/controller/baseController.js"></script>
    <script src="./js/controller/pageController.js"></script>
</head>

<body ng-app="pyg" ng-controller="pageController">
<!-- 头部栏位 -->
<!--页面顶部-->
<#include "head.ftl"/>

<div class="py-container">
    <div id="item">
        <div class="crumb-wrap">
            <ul class="sui-breadcrumb">
                <li>
                    <a href="#">${itemCat1}</a>
                </li>
                <li>
                    <a href="#">${itemCat2}</a>
                </li>
                <li>
                    <a href="#">${itemCat3}</a>
                </li>
                <li class="active">${goods.goodsName}</li>
            </ul>
        </div>
        <!--product-info-->
        <div class="product-info">
            <div class="fl preview-wrap">
                <!--放大镜效果-->
                <div class="zoom">
                    <!--默认第一个预览-->
                    <div id="preview" class="spec-preview">
                        <#assign imgList=goodsDesc.itemImages?eval >
                        <#if imgList?size gt 0></#if>
                        <span class="jqzoom"><img jqimg="${imgList[0].url}" src="${imgList[0].url}"/></span>
                    </div>
                    <!--下方的缩略图-->
                    <div class="spec-scroll">
                        <a class="prev">&lt;</a>
                        <!--左右按钮-->
                        <div class="items">
                            <ul>
                            <#list imgList as img>
                                <li><img src="${img.url}" bimg="${img.url}" onmousemove="preview(this)"/></li>
                            </#list>
                            </ul>
                        </div>
                        <a class="next">&gt;</a>
                    </div>
                </div>
            </div>
            <div class="fr itemInfo-wrap">
                <div class="sku-name">
                    <h4>${item.title}</h4>
                </div>
                <div class="news"><span>${goods.caption!""}</span></div>
                <div class="summary">
                    <div class="summary-wrap">
                        <div class="fl title">
                            <i>价　　格</i>
                        </div>
                        <div class="fl price">
                            <i>¥</i>
                            <em>${item.price}</em>
                            <span>降价通知</span>
                        </div>
                        <div class="fr remark">
                            <i>累计评价</i><em>612188</em>
                        </div>
                    </div>
                    <div class="summary-wrap">
                        <div class="fl title">
                            <i>促　　销</i>
                        </div>
                        <div class="fl fix-width">
                            <i class="red-bg">加价购</i>
                            <em class="t-gray">满999.00另加20.00元，或满1999.00另加30.00元，或满2999.00另加40.00元，即可在购物车换
                                购热销商品</em>
                        </div>
                    </div>
                </div>
                <div class="support">
                    <#list goodsDesc.customAttributeItems?eval as custom>
                    <div class="summary-wrap">
                        <div class="fl title">
                            <i>${custom.text}</i>
                        </div>
                        <div class="fl fix-width">
                            <em class="t-gray">${custom.value}</em>
                        </div>
                    </div>
                    </#list>
                </div>
                <div class="clearfix choose">
                    <div id="specification" class="summary-wrap clearfix">
                        <#list goodsDesc.specificationItems?eval as option>
                        <dl>
                            <dt>
                                <div class="fl title">
                                    <i>${option.attributeName}</i>
                                </div>
                            </dt>
                            <#list option.attributeValue as specValue>
                            <dd><a href="javascript:;" class="{{selectSpec('${option.attributeName}','${specValue}')?'selected':''}}" ng-click="changeSpec('${option.attributeName}','${specValue}')">${specValue}<span title="点击取消选择">&nbsp;</span>
                            </a></dd>
                            </#list>
                        </dl>
                        </#list>
                    </div>


                    <div class="summary-wrap">
                        <div class="fl title">
                            <div class="control-group">
                                <div class="controls">
                                    <input autocomplete="off" type="text" value="1" minnum="1" class="itxt" ng-model="num"/>
                                    <a href="javascript:void(0)" class="increment plus" ng-click="num=num+1">+</a>
                                    <a href="javascript:void(0)" class="increment mins" ng-click="num=num==1?1:num-1">-</a>
                                </div>
                            </div>
                        </div>
                        <div class="fl">
                            <ul class="btn-choose unstyled">
                                <li>
                                    <a href="cart.html" target="_blank" class="sui-btn  btn-danger addshopcar">加入购物车</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!--product-detail-->
        <div class="clearfix product-detail">
        <#include "aside.ftl" />
            <div class="fr detail">
                <div class="clearfix fitting">
                    <h4 class="kt">选择搭配</h4>
                    <div class="good-suits">
                        <div class="fl master">
                            <div class="list-wrap">
                                <div class="p-img">
                                    <img src="img/_/l-m01.png"/>
                                </div>
                                <em>￥5299</em>
                                <i>+</i>
                            </div>
                        </div>
                        <div class="fl suits">
                            <ul class="suit-list">
                                <li class="">
                                    <div id="">
                                        <img src="img/_/dp01.png"/>
                                    </div>
                                    <i>Feless费勒斯VR</i>
                                    <label data-toggle="checkbox" class="checkbox-pretty">
                                        <input type="checkbox"><span>39</span>
                                    </label>
                                </li>
                                <li class="">
                                    <div id=""><img src="img/_/dp02.png"/></div>
                                    <i>Feless费勒斯VR</i>
                                    <label data-toggle="checkbox" class="checkbox-pretty">
                                        <input type="checkbox"><span>50</span>
                                    </label>
                                </li>
                                <li class="">
                                    <div id=""><img src="img/_/dp03.png"/></div>
                                    <i>Feless费勒斯VR</i>
                                    <label data-toggle="checkbox" class="checkbox-pretty">
                                        <input type="checkbox"><span>59</span>
                                    </label>
                                </li>
                                <li class="">
                                    <div id=""><img src="img/_/dp04.png"/></div>
                                    <i>Feless费勒斯VR</i>
                                    <label data-toggle="checkbox" class="checkbox-pretty">
                                        <input type="checkbox"><span>99</span>
                                    </label>
                                </li>
                            </ul>
                        </div>
                        <div class="fr result">
                            <div class="num">已选购0件商品</div>
                            <div class="price-tit"><strong>套餐价</strong></div>
                            <div class="price">￥5299</div>
                            <button class="sui-btn  btn-danger addshopcar">加入购物车</button>
                        </div>
                    </div>
                </div>
                <div class="tab-main intro">
                    <ul class="sui-nav nav-tabs tab-wraped">
                        <li class="active">
                            <a href="#one" data-toggle="tab">
                                <span>商品介绍</span>
                            </a>
                        </li>
                        <li>
                            <a href="#two" data-toggle="tab">
                                <span>规格与包装</span>
                            </a>
                        </li>
                        <li>
                            <a href="#three" data-toggle="tab">
                                <span>售后保障</span>
                            </a>
                        </li>
                        <li>
                            <a href="#four" data-toggle="tab">
                                <span>商品评价</span>
                            </a>
                        </li>
                        <li>
                            <a href="#five" data-toggle="tab">
                                <span>手机社区</span>
                            </a>
                        </li>
                    </ul>
                    <div class="clearfix"></div>
                    <div class="tab-content tab-wraped">
                        <div id="one" class="tab-pane active">
                            ${goodsDesc.introduction!""}
                        </div>
                        <div id="two" class="tab-pane">
                            <p>${goodsDesc.packageList}</p>
                        </div>
                        <div id="three" class="tab-pane">
                            <p>${goodsDesc.saleService!'无'}</p>
                        </div>
                        <div id="four" class="tab-pane">
                            <p>商品评价</p>
                        </div>
                        <div id="five" class="tab-pane">
                            <p>手机社区</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!--like-->
        <#include "like.ftl" />
    </div>
</div>
<!-- 底部栏位 -->
<!--页面底部-->
<#include "foot.ftl" />
<!--页面底部END-->


<script type="text/javascript" src="js/plugins/jquery/jquery.min.js"></script>
<script type="text/javascript">
    $(function () {
        $("#service").hover(function () {
            $(".service").show();
        }, function () {
            $(".service").hide();
        });
        $("#shopcar").hover(function () {
            $("#shopcarlist").show();
        }, function () {
            $("#shopcarlist").hide();
        });

    })
</script>
<script type="text/javascript" src="js/model/cartModel.js"></script>
<script type="text/javascript" src="js/plugins/jquery.easing/jquery.easing.min.js"></script>
<script type="text/javascript" src="js/plugins/sui/sui.min.js"></script>
<script type="text/javascript" src="js/plugins/jquery.jqzoom/jquery.jqzoom.js"></script>
<script type="text/javascript" src="js/plugins/jquery.jqzoom/zoom.js"></script>
<script type="text/javascript" src="index/index.js"></script>
</body>

</html>