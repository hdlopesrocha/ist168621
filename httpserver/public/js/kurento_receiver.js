

var KurentoReceiver = new (function() {

	var peerConnections = {};
	var newParticipantsCallback = null; 
	var newVideoCallback = null; 

	this.onNewParticipants = function(userId, uid, video) {
		if(peerConnections[userId]==null){
			var pc = new RTCPeerConnection(configuration);
			peerConnections[userId] = pc;
			
			// XXX [CLIENT_ICE_02] XXX		
			pc.onicecandidate = function(event) {
				if (event.candidate) {
					// XXX [CLIENT_ICE_03] XXX
					Kurento.ws.send(JSON.stringify({
						id : "iceCandidate",
						uid:userId,
						candidate : event.candidate
					}));
				}
			}
			
			
			// XXX [CLIENT_OFFER_01] XXX
			pc.createOffer(function (desc) {
				console.log("createOfferToReveive");
				console.log(desc);
				// XXX [CLIENT_OFFER_02] XXX

				pc.setLocalDescription(desc, function() {
					// XXX [CLIENT_OFFER_03] XXX		
					Kurento.ws.send(JSON.stringify({
						id : "offerToRecv",
						uid:userId,
						data : JSON.stringify(pc.localDescription)
					}));
				}, logError);
				
			
			}, logError,remote_constraints);
		

			
		}
	}
	
	
	this.start= function(npcb,nvcb){
		newParticipantsCallback = npcb;
		newVideoCallback = nvcb;
	};
		
	this.onmessage = function(id,message) {
		// console.info('Received message: ' + message.data);
		switch (id) {
		case 'participants':
			console.log(id,message);
			for(var userId in message.data){
				var video  = newVideoCallback(userId);
				newParticipantsCallback(userId, message.data[userId]);
				KurentoReceiver.onNewParticipants(userId, message.data[userId],video);
			}
			break;
		case 'sessionDescription':
			
			if(!peerConnections[message.userId]){
				var video = createVideoCallback(message.userId);
				var options = {
				      remoteVideo: video
			    }
				peerConnections[message.userId] = new RTCPeerConnection(configuration);
				
				peerConnections[message.userId].createOffer(remote_constraints);

				peerConnections[message.userId].onaddstream = function(event) {
					alert("HE HAVE REMOTE VIDEO!");
					video.src = URL.createObjectURL(event.stream);
				};	
			}

			var sdp = new RTCSessionDescription(message);
			peerConnections[message.userId].setRemoteDescription(sdp, function(){
				peerConnections[message.userId].createAnswer( function( answer ) {
					console.log("createAnswer");
					console.log(answer);
					peerConnections[message.userId].setLocalDescription(answer);
				},logError);
				
				console.log("setRemoteDescription")
				console.log(sdp)
			},logError);
			break;
		/*case 'description':
			var sdp = new RTCSessionDescription(message.data);

			console.log("description");
			console.log(sdp);
			// XXX [CLIENT_OFFER_08] XXX
			/*Kurento.pc.setRemoteDescription(sdp, function(){
				console.log("setRemoteDescription")
				console.log(sdp)
			},logError);
			
			break;	
		 */
		case 'iceCandidate':
			var candidate = new RTCIceCandidate(message.candidate);
								
			peerConnections[message.userId].addIceCandidate(candidate, function() {
				console.log(candidate.candidate);
			}, logError);
			
			break;
		default:
			break;
		}
	};


		
	console.log("RET RECV");


	return this;
})();