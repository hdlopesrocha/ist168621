
function wsurl(s) {
	var l = window.location;
	return ((l.protocol === "https:") ? "wss://" : "ws://") + l.hostname + (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "") + s;
}

var configuration = {
	iceServers : [ 
	    {urls : "stun:stun.l.google.com:19302"}, 
	    {urls : "stun:23.21.150.121"}
	]
}

var local_constraints = {
		"audio" : true,
		"video" : true
};
	
var remote_constraints = { 
		'OfferToReceiveAudio':true, 
		'OfferToReceiveVideo':true 
};


function logError(err) {
	console.log(err);
}

var Kurento = new (function() {

	this.ws = null;
	
	this.start = function(groupId, ready) {		

		if ("WebSocket" in window) {
			Kurento.ws = new WebSocket(wsurl("/ws/room/" + groupId));
		
			Kurento.ws.onerror = function() {
				console.log("Error!");

			};
			
			Kurento.ws.onmessage = function(message) {
				KurentoSender.onmessage(message);
				KurentoReceiver.onmessage(message);
			};

			Kurento.ws.onopen = function() {
				Kurento.ws.send(JSON.stringify({
					id : "joinRoom",
					name : groupId
				}));
		
				
				ready();
			};

			Kurento.ws.onclose = function() {
				console.log("Connection is closed...");
			};

	
		
			window.onbeforeunload = function() {
				Kurento.ws.close();
			};
		
		
		} else {
			console.log("no websocket support!");
		}	
	};

	return this;
})();