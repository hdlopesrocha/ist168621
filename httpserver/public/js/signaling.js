var Signaling = (function(){
	
	this.login = function(email,password,success,error){
		$.ajax({
			type : "post",
			url : "/api/auth",
			data : "email="+email+"&password="+password,
			contentType : "application/x-www-form-urlencoded",
			processData : false,
			cache : false,
			async : false,
			error : function(e) {
				error();
			},
			success : function(data) {
				success();
			}
		});
	}
	
	this.register = function(email,password1,password2, photo, success, fail){
		success();
	}
	
	return this;
})();