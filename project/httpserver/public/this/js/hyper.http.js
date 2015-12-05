var Signaling = (function(){

	this.searchInsideGroup = function(gid,query,success){
		$.get( "/api/group/search/"+gid+"?query="+query, function( data ) {
			success(JSON.parse(data));
		});	
	}
	
	
	this.userProfile = function(uid,success){
		$.get( "/api/user/get/"+uid, function( data ) {
			success(JSON.parse(data));
		});	
	}
	
	this.postIceCandidate = function(gid,token,sdp,success,error){
		$.ajax({
			type : "post",
			url : "/api/group/ice/"+gid+"/"+token,
			data : sdp,
			contentType : "text/plain",
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
	
	
	
	this.postSdp = function(gid,sdp,success,error){
		$.ajax({
			type : "post",
			url : "/api/group/sdp/"+gid,
			data : sdp,
			contentType : "text/plain",
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
	
	this.listRecordings = function(){
		
	}
	
	this.deleteInvite = function(gid,success){
		$.get( "/api/invite/delete/"+gid, function( data ) {
			success();
		});
	}
	
	this.createInvite = function(gid,success){
		$.get( "/api/invite/create/"+gid, function( data ) {
			success(data);
		});
	}
	
	this.getInvite = function(gid,success){
		$.get( "/api/invite/get/"+gid, function( data ) {
			success(data);
		});
	}
	
	this.listRecordings = function(gid,seq,success){
		$.get( "/api/rec/"+gid+"/"+seq, function( data ) {
			success(JSON.parse(data));
		});
	}
	
	this.saveRecording = function(gid,uid,inter,start,end,name,type, formData, success, error){
		formData.append("uid",uid);
		formData.append("start",start);
		formData.append("end",end);
		formData.append("name",name);
		formData.append("type",type);
		if(inter!=null){
			formData.append("inter",inter);
		}
		
		$.ajax({   
		    type: "POST",
		    url: "/api/rec/"+gid,
		    data: formData,
	        encType: "multipart/form-data",
		    contentType: false,
	        processData: false,
	        cache: false,
	        async : true,
	        error:function(e){
		    	error();
		    },
		    success:function(data){
				success(data);
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
	
	this.searchGroupCandidates = function(groupId,query,result){
		$.get( "/api/group/candidates/"+groupId+"?s="+query, function( data ) {
			result(JSON.parse(data));
		});
	}

	this.addUserGroup = function(gid,uid,success){
		$.get( "/api/group/add/"+gid+"/"+uid, function( data ) {
			success();
		});
	}
	
	this.removeUserGroup = function(gid,uid,success){
		$.get( "/api/group/del/"+gid+"/"+uid, function( data ) {
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
	
	
	this.getSdp = function(membershipId,result){
		$.get( "/api/group/sdp/"+membershipId, function( data ) {
			result(JSON.parse(data));
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


	this.listGroupMembersProperties = function(groupId,result){
		$.get( "/api/group/properties/"+groupId, function( data ) {
			result(JSON.parse(data));
		});
	}
	
	
	this.publish = function(key,success,error){

		$.ajax({
			type : "post",
			url : "/api/pubsub/"+key,
			data : "",
			contentType : "text/plain",
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
	
	function subscribeCycle(key,ts,result){
		$.get( "/api/pubsub/"+key+"/"+ts, function( data ) {
			if(data.length!=0){			
				result();
				var obj = JSON.parse(data);
				ts = obj.ts;
			}else {
				// server timed-out
			}
			subscribeCycle(key,ts,result);
		}).fail(function(){
			console.log("subscribe "+ key+" error!");
			// try again later
			setTimeout(function(){
				subscribeCycle(key,ts,result);
			},10000);
		});		
	}
	
	this.subscribe = function(key,result){
		subscribeCycle(key,-1,result);
	}

	
	
	return this;
})();