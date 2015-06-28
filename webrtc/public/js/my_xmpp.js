function xmpp_register(connection,username,password,registered,conflict,notacceptable){
	if(!username || !password){
		notacceptable();
	}
	else {
		var callback = function (status) {
	        if (status === Strophe.Status.REGISTER) {
	            connection.register.fields.username = username;
	            connection.register.fields.password = password;
	            connection.register.submit();
	        } else if (status === Strophe.Status.REGISTERED) {
	            registered();
	        	connection.reset();
	        } else  if(status === Strophe.Status.CONFLICT ){
	        	conflict();
	        	connection.reset();
	        } else  if(status === Strophe.Status.NOTACCEPTABLE ){
	        	notacceptable();
	        	connection.reset();
	        } 
		};
		connection.register.connect("ejabberd", callback, function(){}, function(){});
	}
}


function xmpp_login(connection,username,password,connected,fail){
	if(!username || !password){
		fail();
	}
	else {
		var callback = function(status) {
		    if (status === Strophe.Status.CONNFAIL || status === Strophe.Status.AUTHFAIL) {
		    	fail();
		    } else if (status === Strophe.Status.CONNECTED) {
		    	connected();
		    	console.log(connection);
		    }
		}
		connection.connect(username+"@ejabberd",password, callback);
	}
}


