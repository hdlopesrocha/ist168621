

var KurentoReceiver = new (function() {

	this.peerConnections = {};
	var newParticipantsCallback = null; 
	var newVideoCallback = null; 

	
	this.onNewParticipants = function(userId, uid) {
		if(KurentoReceiver.peerConnections[userId]==null){
			var pc = Kurento.createPeerConnection(userId);
			KurentoReceiver.peerConnections[userId] = pc;
	
			
			pc.onaddstream = function (e) {
				console.log(e);
				newVideoCallback(userId,URL.createObjectURL(e.stream));
			};
			
			
			// XXX [CLIENT_OFFER_01] XXX
			pc.createOffer(function (desc) {
				console.log("createOfferToReveive");
				console.log(desc);
				// XXX [CLIENT_OFFER_02] XXX

				pc.setLocalDescription(desc, function() {
					// XXX [CLIENT_OFFER_03] XXX		
					Kurento.ws.send(JSON.stringify({
						id : "offer",
						uid:userId,
						data : pc.localDescription
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
				newParticipantsCallback(userId, message.data[userId]);
				KurentoReceiver.onNewParticipants(userId, message.data[userId]);
			}
			break;
		case 'description2':
			var sdp = new RTCSessionDescription(message);
			var userId = message.uid;
			console.log("description2");
			console.log(sdp);
			// XXX [CLIENT_OFFER_08] XXX
			KurentoReceiver.peerConnections[userId].setRemoteDescription(sdp, function(){
				console.log("setRemoteDescription")
				console.log(sdp)
				/*
				peerConnections[userId].createAnswer( function( answer ) {
					console.log("createAnswer");
					console.log(answer);
					peerConnections[userId].setLocalDescription(answer);
				},logError);
				*/
			},logError);
			
			break;	
		default:
			break;
		}
	};


		
	console.log("RET RECV");


	return this;
})();