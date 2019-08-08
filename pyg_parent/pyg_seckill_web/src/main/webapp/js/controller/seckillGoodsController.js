app.controller("seckillGoodsController",function($scope,$location,$controller,seckillGoodsService,$interval){

    //继承父控制器
    $controller("baseController",{$scope:$scope});

    //查询所有秒杀商品列表
    $scope.findAll=function () {
        seckillGoodsService.findAll().success(function (response) {
            $scope.list=response;
        })
    };

    $scope.getOne=function () {
        //获取路径中的ID
        var id = $location.search()['id'];

        if(id>0){
            seckillGoodsService.getOne(id).success(function (response) {
                $scope.entity=response;

                //计算出剩余时间
                var endTime = new Date($scope.entity.endTime).getTime();
                var nowTime = new Date().getTime();

                //剩余时间
                $scope.secondes =Math.floor( (endTime-nowTime)/1000 );

                var time =$interval(function () {
                    if($scope.secondes>0){
                        //时间递减
                        $scope.secondes=$scope.secondes-1;
                        //时间格式化
                        $scope.timeString=$scope.convertTime2String($scope.secondes);
                    }else{
                        //结束时间递减
                        $interval.cancel(time);
                    }
                },1000);
            })
        }
    }


//时间计算转换
    $scope.convertTime2String=function (allseconds) {
        //计算天数
        var days = Math.floor(allseconds/(60*60*24));

        //小时
        var hours =Math.floor( (allseconds-(days*60*60*24))/(60*60) );

        //分钟
        var minutes = Math.floor( (allseconds-(days*60*60*24)-(hours*60*60))/60 );

        //秒
        var seconds = (allseconds-(days*60*60*24)-(hours*60*60)-(minutes*60)).toFixed(0);

        if(seconds < 10){
            seconds = "0"+seconds;
        }
        //拼接时间
        var timString="";
        if(days>0){
            timString=days+"天:";
        }
        return timString+=hours+"小时:"+minutes+"分钟:"+seconds+"秒";
    };

    $scope.jump2Detail = function (id) {
        location.href='seckill-item.html#?id='+id;
    }

});
