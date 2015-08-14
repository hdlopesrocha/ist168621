
function wsurl(s) {
	var l = window.location;
	return ((l.protocol === "https:") ? "wss://" : "ws://") + l.hostname + (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "") + s;
}


var local_constraints = {
		"audio" : true,
		"video" : true
};
	
var remote_constraints = { 
	'offerToReceiveAudio':true, 
	'offerToReceiveVideo':true 
};


function logError(err) {
	console.log(err);
}

var Kurento = new (function() {
	var configuration = 

	
	this.ws = null;

	this.createPeerConnection = function() {
		return new RTCPeerConnection({
			iceServers : [ {
				urls : "stun:stun.l.google.com:19302"
			}, {
				urls : "stun:23.21.150.121"
			} ]
		});
	}
	
	
	this.start = function(groupId, ready) {		

		if ("WebSocket" in window) {
			Kurento.ws = new WebSocket(wsurl("/ws/room/" + groupId));
		
			Kurento.ws.onerror = function() {
				console.log("Error!");

			};
			
			Kurento.ws.onmessage = function(message) {
				var obj = JSON.parse(message.data);
				KurentoSender.onmessage(obj.id,obj);
				KurentoReceiver.onmessage(obj.id,obj);
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