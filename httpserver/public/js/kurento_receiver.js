function onNewParticipants(userId, uid, video) {

}

var KurentoReceiver = new (function() {

	var peerConnections = {};

	var newParticipantsCallback = null; 
		
	this.start= function(npcb){

		newParticipantsCallback = npcb;
	};
		
	this.onmessage = function(message) {
			// console.info('Received message: ' + message.data);
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
			/*case 'description':
				var sdp = new RTCSessionDescription(obj.data);

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
				var candidate = new RTCIceCandidate(obj.candidate);
									
				peerConnections[obj.userId].addIceCandidate(candidate, function() {
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