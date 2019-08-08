app.service("seckillGoodsService",function($http){

    //查询列表
    this.findAll=function(){
        return $http.get("/seckillGoods/list");
    }
    this.getOne=function(id){
        return $http.get("/seckillGoods/findOne/"+id);
    }

});
