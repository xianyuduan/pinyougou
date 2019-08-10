//服务层
app.service('seckillOrderService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seckillOrder/findAll');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../seckillOrder/findPage/'+page+'/'+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../seckillOrder/findOne/'+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../seckillOrder/add',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../seckillOrder/update',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../seckillOrder/delete/'+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../seckillOrder/search/'+page+"/"+rows, searchEntity);
	}    	
});
