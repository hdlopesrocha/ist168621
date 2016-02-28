var HyperHttp = (function(){

	this.searchInsideGroup = function(gid,query,success){
		$.get( "/api/group/"+gid+"/content?query="+query,success);
	}
		
	this.userProfile = function(uid,success){
		$.get( "/api/user/"+uid, success);
	}

	this.login = function(email,password,success,error){
		$.ajax({
			type : "POST",
			url : "/api/user/auth",
			data : "email="+email+"&password="+password,
			contentType : "application/x-www-form-urlencoded",
			processData : false,
			cache : false,
			async : true,
			error : function(e) {
				error();
			},
			success : success
		});
	}

	this.deleteInvite = function(gid,success){
       $.ajax({
            url: "/api/invite/"+gid,
            type: 'DELETE',
            success: success
        });
	}
	
	this.createInvite = function(gid,success){
        $.ajax({
            url: "/api/invite/"+gid,
            type: 'PUT',
            success: success
        });
	}
	
	this.getInvite = function(gid,success){
		$.get( "/api/invite/"+gid,success);
	}
	
	this.register = function(email,password1,password2, formData, success, error){
		formData.append("email",email);
		formData.append("password",password1);
		
		$.ajax({   
		    type: "POST",
		    url: "/api/user",
		    data: formData,
	        encType: "multipart/form-data",
		    contentType: false,
	        processData: false,
	        cache: false,
	        async : true,
	        error:function(e){
		    	error();
		    },
            success: success
		});
	}

	this.updateUser = function(userId,email, formData, success, error){
        formData.append("email",email);
        $.ajax({
            type: "PUT",
            url: "/api/user/"+userId,
            data: formData,
            encType: "multipart/form-data",
            contentType: false,
            processData: false,
            cache: false,
            async : true,
            error:function(e){
                error();
            },
            success:success
        });
    }
	
	this.logout = function(success){
	    $.ajax({
            url: "/api/user/auth",
            type: 'DELETE',
            success: success
        });
	}
	
	this.searchGroupCandidates = function(groupId,query,result){
		$.get( "/api/group/"+groupId+"/candidates?s="+query, function( data ) {
			console.log("searchGroupCandidates",query,data);
			result(data);
		});
	}

	this.listRelations = function(result){
		$.get( "/api/relation", function( data ) {
			result(data);
		});
	}
	
	this.listRequests = function(result){
		$.get( "/api/requests", function( data ) {
			result(data);
		});
	}

	this.rejectRelation = function(userId, success){
		$.ajax({
            url: "/api/relation/"+userId,
            type: 'DELETE',
            success: success
        });
	}
	
	this.addRelation = function(userId, success){
		$.ajax({
            url: "/api/relation/"+userId,
            type: 'PUT',
            success: success
        });
	}

	this.createGroup = function(name,visibility,success){
        $.ajax({
            url: "/api/group?n="+name+"&v="+visibility,
            type: 'PUT',
            success: success

        });
	}
	
	this.listGroups = function(result){
		$.get( "/api/group", result);
	}

	return this;
})();