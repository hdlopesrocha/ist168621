function onNewParticipants(userId, uid, video) {

}

var Kurento = (function() {

	var peerConnections = {};
	this.pc = null;
	
	function wsurl(s) {
		var l = window.location;
		return ((l.protocol === "https:") ? "wss://" : "ws://") + l.hostname + (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "") + s;
	}

	var ws = null;


	var configuration = {
		iceServers : [ 
		    {urls : "stun:stun.l.google.com:19302"}, 
		    {urls : "stun:23.21.150.121"}
		]
	}

	var local_constraints = {
		"audio" : false,
		"video" : true
	};
	
	var remote_constraints = { 
		'OfferToReceiveAudio':false, 
		'OfferToReceiveVideo':true 
	};


	function logError(err) {
		console.log(err);
	}

	this.joinRoom = function(roomName) {
		ws.send(JSON.stringify({
			id : "joinRoom",
			name : roomName
		}));
	}

	this.init = function(video, uid, groupId, ready, newParticipantsCallback, createVideoCallback) {

		if ("WebSocket" in window) {
			var path = wsurl("/ws/room/" + groupId);
			ws = new WebSocket(path);

			ws.onerror = function() {
				console.log("Error!");

			};
			
			ws.onmessage = function(message) {
				//					console.info('Received message: ' + message.data);
				var obj = JSON.parse(message.data);
				switch (obj.id) {
				case 'existingParticipants':
				case 'newParticipantArrived':
					for(var userId in obj.data){
						newParticipantsCallback(userId, obj.data[userId]);
						onNewParticipants(userId, obj.data[userId]);
					}
					break;
				case 'sessionDescription':
					
					if(!peerConnections[obj.userId]){
						var video = createVideoCallback(obj.userId);
						var options = {
						      remoteVideo: video
					    }
						peerConnections[obj.userId] = new RTCPeerConnection(configuration);
						
						peerConnections[obj.userId].createOffer(remote_constraints);

						peerConnections[obj.userId].onaddstream = function(event) {
							alert("HE HAVE REMOTE VIDEO!");
							video.src = URL.createObjectURL(event.stream);
						};	
					}

					var sdp = new RTCSessionDescription(obj);
					peerConnections[obj.userId].setRemoteDescription(sdp, function(){
						peerConnections[obj.userId].createAnswer( function( answer ) {
							console.log("createAnswer");
							console.log(answer);
							peerConnections[obj.userId].setLocalDescription(answer);
						},logError);
						
						console.log("setRemoteDescription")
						console.log(sdp)
					},logError);
					break;
				case 'description':
					var sdp = new RTCSessionDescription(obj.data);

					console.log("description");
					console.log(sdp);
					// XXX [CLIENT_OFFER_08] XXX
					Kurento.pc.setRemoteDescription(sdp, function(){
						console.log("setRemoteDescription")
						console.log(sdp)
					},logError);
		
					
					break;	
				case 'iceCandidate':
					var candidate = new RTCIceCandidate(obj.candidate);
										
					peerConnections[obj.userId].addIceCandidate(candidate, function() {
						console.log(candidate.candidate);
					}, logError);
					
					break;
				default:
					console.info('Unknown message: ' + message.data);
					break;
				}
			};

			ws.onopen = function() {
				ready();
			};

			ws.onclose = function() {
				console.log("Connection is closed...");
			};

			window.onbeforeunload = function() {
				ws.close();
			};
		} else {
			console.log("no websocket support!");
		}
		
		
		// XXX [CLIENT_ICE_01] XXX
		Kurento.pc = new RTCPeerConnection(configuration);

		// XXX [CLIENT_ICE_02] XXX		
		Kurento.pc.onicecandidate = function(event) {
			if (event.candidate) {
				console.log("onicecandidate");
				console.log(event);
				// XXX [CLIENT_ICE_03] XXX
				ws.send(JSON.stringify({
					id : "iceCandidate",
					candidate : event.candidate
				}));
			}
		}

		// XXX [CLIENT_OFFER_01] XXX
		navigator.getUserMedia(local_constraints, function(stream) {
			video.src = URL.createObjectURL(stream);
			Kurento.pc.addStream(stream);
			Kurento.pc.createOffer(function (desc) {
				console.log("createOffer");
				console.log(desc);
				// XXX [CLIENT_OFFER_02] XXX
				Kurento.pc.setLocalDescription(desc);
				// XXX [CLIENT_OFFER_03] XXX
				ws.send(JSON.stringify({
					id : "description",
					description : desc
				}));
				
				
			}, logError);
		}, logError);

		
	}

	return this;
})();