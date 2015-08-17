


var KurentoSender = new (function() {

	this.pc = null;
	
	this.start = function(userId,video){
		KurentoSender.pc = Kurento.createPeerConnection(null);

		// XXX [CLIENT_OFFER_01] XXX
		navigator.getUserMedia(local_constraints, function(stream) {
			video.src = URL.createObjectURL(stream);
			KurentoSender.pc.addStream(stream);
			KurentoSender.pc.createOffer(function (desc) {
				console.log("createOffer");
				console.log(desc);
				// XXX [CLIENT_OFFER_02] XXX
				KurentoSender.pc.setLocalDescription(desc);
				// XXX [CLIENT_OFFER_03] XXX
				Kurento.ws.send(JSON.stringify({
					id : "offer",
					// uid:userId, // null userId for sender
					data : desc
				}));
				
				
			}, logError);
		}, logError);

		
	};

	

	this.onmessage = function(id,message) {
		switch (id) {
			case 'description':
				console.log(id, message);
				var sdp = new RTCSessionDescription(message);

				console.log("description");
				console.log(sdp);
				// XXX [CLIENT_OFFER_08] XXX
				KurentoSender.pc.setRemoteDescription(sdp, function(){
					console.log("setRemoteDescription")
					console.log(sdp)
				},logError);
	
				
				break;
				
			default:
				break;
		}
	};


	return this;
})();