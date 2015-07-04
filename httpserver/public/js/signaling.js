var Signaling = (function(){
	
	this.login = function(email,password,success,error){
		$.ajax({
			type : "post",
			url : "/api/user/login",
			data : "email="+email+"&password="+password,
			contentType : "application/x-www-form-urlencoded",
			processData : false,
			cache : false,
			async : true,
			error : function(e) {
				error();
			},
			success : function(data) {
				success();
			}
		});
	}
	
	this.register = function(email,password1,password2, formData, success, error){
		formData.append("email",email);
		formData.append("password",password1);
		
		
		$.ajax({   
		    type: "POST",
		    url: "/api/user/register",
		    data: formData,
	        encType: "multipart/form-data",
		    contentType: false,
	        processData: false,
	        cache: false,
	        async : true,
	        error:function(e){
		    	error();
		    },
		    success:function(){
				success();
		    }
		});
		
	}
	
	this.logout = function(success){
		$.get( "/api/user/logout", function( data ) {
			success();
		});
	}
	
	this.search = function(query,result){
		$.get( "/api/user/search?s="+query, function( data ) {
			result(JSON.parse(data));
		});
	}

	this.listRelations = function(result){
		$.get( "/api/relation/list", function( data ) {
			result(JSON.parse(data));
		});
	}
	
	this.listRequests = function(result){
		$.get( "/api/relation/requests", function( data ) {
			result(JSON.parse(data));
		});
	}

	this.rejectRelation = function(userId, success){
		$.get( "/api/relation/del/"+userId, function( data ) {
			success();
		});
	}
	
	this.addRelation = function(userId, success){
		$.get( "/api/relation/add/"+userId, function( data ) {
			success();
		});
	}
	
	this.createGroup = function(name,success){
		$.get( "/api/group/create?n="+name, function( data ) {
			success();			
		});
	}
	
	this.listGroups = function(result){
		$.get( "/api/group/list", function( data ) {
			result(JSON.parse(data));
		});
	}
	
	this.listGroupMembers = function(groupId,result){
		$.get( "/api/group/members/"+groupId, function( data ) {
			result(JSON.parse(data));
		});
	}
	
	
	return this;
})();