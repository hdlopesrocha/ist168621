
var constraints = {
		audio : true,
		video : {
			mandatory : {
				maxWidth : 320,
				maxFrameRate : 15,
				minFrameRate : 15
			}
		}
	};



function onNewParticipants(userId, uid, video) {

	
	
}


var Kurento = (function() {

	var peerConnections = {};

	
	function wsurl(s) {
		var l = window.location;
		return ((l.protocol === "https:") ? "wss://" : "ws://") + l.hostname
				+ (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "") + s;
	}

	var ws = null;

	this.pc = null;

	var configuration = {
		iceServers : [ {
			urls : "stun:stun.l.google.com:19302"
		}, {
			urls : "stun:23.21.150.121"
		} ]
	}

	var local_constraints = {
		"audio" : false,
		"video" : true
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

		Kurento.pc.onicecandidate = function(event) {
			if (event.candidate) {
				console.log(event);
			}
		}

		function gotDescription(desc) {
			console.log(desc);
			Kurento.pc.setLocalDescription(desc);
		}

		navigator.getUserMedia(local_constraints, function(stream) {
			video.src = URL.createObjectURL(stream);
			Kurento.pc.addStream(stream);
			Kurento.pc.createOffer(function gotDescription(desc) {
				console.log(desc);
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
				case 'iceCandidate':
					var candidate = new RTCIceCandidate(obj.candidate);
					console.log(candidate);
					
			
					
					if(!peerConnections[obj.userId]){
						var video = createVideoCallback(obj.userId);
						var options = {
						      remoteVideo: video
					    }
						peerConnections[obj.userId] = new RTCPeerConnection(options);
						
						peerConnections[obj.userId].onaddstream = function(event) {
							alert("REMOTE VIDEO!");
							video.src = URL.createObjectURL(event.stream);
						};	
					}
					
					
					
					peerConnections[obj.userId].addIceCandidate(candidate, function() {
						console.log("success!")
					}, logError);
					break;
				default:
					console.info('Unknown message: ' + message.data);
					break;
				}
			};

			ws.onopen = function() {
				console.log("Connected!");
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