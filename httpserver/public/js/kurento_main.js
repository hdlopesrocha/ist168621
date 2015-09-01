
function wsurl(s) {
	var l = window.location;
	return ((l.protocol === "https:") ? "wss://" : "ws://") + l.hostname + (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "") + s;
}


var local_constraints = {
		"audio" : true,
		"video" : true,
		'offerToReceiveAudio':true, 
		'offerToReceiveVideo':true 
};
	
var remote_constraints = { 
	'offerToReceiveAudio':true, 
	'offerToReceiveVideo':true 
};


function logError(err) {
	console.log(err);
}


var newParticipantsCallback = null; 
var newVideoCallback = null; 


var Kurento = new (function() {
	
	this.pc = null;

	this.ws = null;

	
	this.receiveRealtime = function(userId){
		Kurento.ws.send(JSON.stringify({
			id : "realtime",
			uid:userId
		}));
	}
	
	this.receiveHistoric = function(userId,offset){
		Kurento.ws.send(JSON.stringify({
			id : "historic",
			uid:userId,
			offset:offset
		}));
	}
	
	
	this.createPeerConnection = function() {
		// XXX [CLIENT_ICE_01] XXX
		var pc = new RTCPeerConnection({
			iceServers : [ {
				urls : "stun:stun.l.google.com:19302"
			}, {
				urls : "stun:23.21.150.121"
			} ]
		});
		
		// XXX [CLIENT_ICE_02] XXX		
		pc.onicecandidate = function(event) {
			if (event.candidate) {
				var msg = {
					id : "iceCandidate",
					candidate : event.candidate
				}
				
				// XXX [CLIENT_ICE_03] XXX
				Kurento.ws.send(JSON.stringify(msg));
			}
		}
		return pc;
	}
	
	
	this.start = function(groupId,npcb,nvcb) {		
		newParticipantsCallback = npcb;
		newVideoCallback = nvcb;
		
		
		if ("WebSocket" in window) {
			Kurento.ws = new WebSocket(wsurl("/ws/room/" + groupId));
		
			Kurento.ws.onerror = function() {
				console.log("Error!");

			};
			
			Kurento.ws.onmessage = function(data) {
				var message = JSON.parse(data.data);
				var id = message.id;
				
				switch(id){
					case 'iceCandidate':
						console.log(id,message);
						var candidate = new RTCIceCandidate(message.candidate);
						if(message.uid){
							KurentoReceiver.peerConnections[message.uid].addIceCandidate(candidate, function() {
								console.log(candidate);
							}, logError);
						}else {
							Kurento.pc.addIceCandidate(candidate, function() {
								console.log(candidate);
							}, logError);
						}
						break;
					case 'description':
						console.log(id, message);
						var sdp = new RTCSessionDescription(message);

						console.log("description");
						console.log(sdp);
						// XXX [CLIENT_OFFER_08] XXX
						Kurento.pc.setRemoteDescription(sdp, function(){
							console.log("setRemoteDescription")
							console.log(sdp)
						},logError);
						break;
					case 'participants':
						console.log(id,message);
						for(var uid in message.data){
							newParticipantsCallback(uid, message.data[uid]);
						}
						break;
					case 'description2':
						var sdp = new RTCSessionDescription(message);
						console.log("description2");
						console.log(sdp);
						// XXX [CLIENT_OFFER_08] XXX
						KurentoReceiver.peerConnection.setRemoteDescription(sdp, function(){
							console.log("setRemoteDescription")
							console.log(sdp)
						},logError);
					default:
						break;					
				}
			};

			Kurento.ws.onopen = function() {
				Kurento.pc = Kurento.createPeerConnection(null);

				// XXX [CLIENT_OFFER_01] XXX
				navigator.getUserMedia(local_constraints, function(stream) {
					Kurento.pc.addStream(stream);
					Kurento.pc.createOffer(function (desc) {		
						console.log("createOffer");
						console.log(desc);
						// XXX [CLIENT_OFFER_02] XXX
						Kurento.pc.setLocalDescription(desc, function() {
							// XXX [CLIENT_OFFER_03] XXX		
							Kurento.ws.send(JSON.stringify({
								id : "offer",
								data : Kurento.pc.localDescription
							}));
						}, logError);
					}, logError,remote_constraints);
				}, logError);

				/*
				// XXX [CLIENT_OFFER_01] XXX
				Kurento.pc.createOffer(function (desc) {
					console.log("createOfferToReveive");
					console.log(desc);
					// XXX [CLIENT_OFFER_02] XXX

					Kurento.pc.setLocalDescription(desc, function() {
						// XXX [CLIENT_OFFER_03] XXX		
						Kurento.ws.send(JSON.stringify({
							id : "offer",
							uid:userId,
							data : Kurento.pc.localDescription
						}));
					}, logError);
					
				
				}, logError,remote_constraints);
				*/

				
				Kurento.pc.onaddstream = function (e) {
					console.log(e);
					newVideoCallback(URL.createObjectURL(e.stream));
				};
				
	
				
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