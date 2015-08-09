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
		var data = {
			id : "joinRoom",
			name : roomName
		};
		ws.send(JSON.stringify(data));
	}

	this.init = function(video, uid, groupId, ready, newParticipantsCallback, createVideoCallback) {

		Kurento.pc = new RTCPeerConnection(configuration);

		
		Kurento.pc.onnegotiationneeded=function(){
			console.log("Negotiation Needed!");
		};
		
		
		Kurento.pc.onicecandidate = function(event) {
			if (event.candidate) {
			//	console.log(event);
			}
		}

		Kurento.pc.onaddstream = function(stream){
			console.log("Local Stream Added!");
		}
		
		

		navigator.getUserMedia(local_constraints, function(stream) {
			video.src = URL.createObjectURL(stream);
			Kurento.pc.addStream(stream);
			Kurento.pc.createOffer(function (desc) {
			//	console.log(desc);
				Kurento.pc.setLocalDescription(desc);
			}, logError);
		}, logError);

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
	}

	return this;
})();