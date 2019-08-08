//服务层
app.service('sellerService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seller/findAll');
	};

    this.findPage = function (page, size) {
        return $http.get('../seller/findPage/' + page + '/' + size)
    };

	//查询实体
	this.findOne=function(id){
		return $http.get('../seller/findOne/'+id);
	};
	//增加 
	this.add=function(entity){
		return  $http.post('../seller/add',entity );
	};
	//修改 
	this.update=function(entity){
		return  $http.post('../seller/update',entity );
	};
	//删除
	this.dele=function(ids){
		return $http.get('../seller/delete/ids='+ids);
	};
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../seller/search/page='+page+"/rows="+rows, searchEntity);
	};

	//更新状态
	this.updateStatus=function(id, status){
        return $http.post('../seller/updateStatus/'+id+"/"+status);
	}
});
