var Signaling = (function(){

	this.searchInsideGroup = function(gid,query,success){
		$.get( "/api/group/"+gid+"/content?query="+query, function( data ) {
			success(data);
		});	
	}
	
	
	this.userProfile = function(uid,success){
		$.get( "/api/user/"+uid, function( data ) {
			success(data);
		});	
	}

	this.login = function(email,password,success,error){
		$.ajax({
			type : "POST",
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

	this.deleteInvite = function(gid,success){
       $.ajax({
            url: "/api/invite/"+gid,
            type: 'DELETE',
            success: function(data) {
                success(data);
            }
        });
	}
	
	this.createInvite = function(gid,success){
        $.ajax({
            url: "/api/invite/"+gid,
            type: 'PUT',
            success: function(data) {
                success(data);
            }
        });
	}
	
	this.getInvite = function(gid,success){
		$.get( "/api/invite/"+gid, function( data ) {
			success(data);
		});
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
		    success:function(){
				success();
		    }
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
	
	this.searchGroupCandidates = function(groupId,query,result){
		$.get( "/api/group/"+groupId+"/candidates?s="+query, function( data ) {
			console.log("searchGroupCandidates",query,data);
			result(JSON.parse(data));
		});
	}



	this.listRelations = function(result){
		$.get( "/api/relation", function( data ) {
			result(JSON.parse(data));
		});
	}
	
	this.listRequests = function(result){
		$.get( "/api/requests", function( data ) {
			result(JSON.parse(data));
		});
	}

	this.rejectRelation = function(userId, success){
		$.ajax({
            url: "/api/relation/"+userId,
            type: 'DELETE',
            success: function(result) {
			    success();
            }
        });
	}
	
	this.addRelation = function(userId, success){
		$.ajax({
            url: "/api/relation/"+userId,
            type: 'PUT',
            success: function(result) {
			    success();
            }
        });
	}
	
	
	this.getSdp = function(membershipId,result){
		$.get( "/api/group/sdp/"+membershipId, function( data ) {
			result(JSON.parse(data));
		});
	}
	
	
	this.createGroup = function(name,visibility,success){
        $.ajax({
            url: "/api/group?n="+name+"&v="+visibility,
            type: 'PUT',
            success: function(result) {
                success();
            }
        });
	}
	
	this.listGroups = function(result){
		$.get( "/api/group", function( data ) {
			result(JSON.parse(data));
		});
	}


	
	return this;
})();