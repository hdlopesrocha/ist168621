var BOSH_SERVICE = 'http://192.168.56.101:5280/http-bind';
var connection = null;

function log(msg) {
    $('#log').append('<div class="msg"></div>').append(document.createTextNode(msg));
}

function rawInput(data){
    log('RECV: ' + data);
}

function rawOutput(data){
    log('SENT: ' + data);
}

function xmpp_register(username,password,registered,conflict,notacceptable){
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


function xmpp_login(username,password,connected,fail){
	if(!username || !password){
		fail();
	}
	else {
		var callback = function(status) {
		    if (status === Strophe.Status.CONNFAIL || status === Strophe.Status.AUTHFAIL) {
		    	fail();
		    } else if (status === Strophe.Status.CONNECTED) {
		    	connected();
			    localStorage.connection = connection;
		    	console.log(connection);
		    }
		}
		connection.connect(username+"@ejabberd",password, callback);
	}
}


function xmpp_connect(){
	//var cookieJid = $.cookie("jid");
	//var cookieSid = $.cookie("sid");
	//var cookieRid = $.cookie("rid");
    connection = new Strophe.Connection(BOSH_SERVICE);
    connection.rawInput = rawInput;
    connection.rawOutput = rawOutput;
	
}