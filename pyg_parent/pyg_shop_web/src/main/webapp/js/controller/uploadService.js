//服务层
app.service('uploadService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.uploadFile=function(){
        var formData = new FormData();
        formData.append("file", file.files[0]);
        return $http({
            method: "post",
			url:"../file/upload",
			data:formData,
            headers:{'Content-type':undefined},
            transformRequest: angular.identity
        });
	};
});
