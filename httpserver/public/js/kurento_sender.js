


var KurentoSender = new (function() {

	this.pc = null;
	

	this.start = function(video){
		
		// XXX [CLIENT_ICE_01] XXX
		console.log(configuration);
		KurentoSender.pc = new RTCPeerConnection(configuration);

		// XXX [CLIENT_ICE_02] XXX		
		KurentoSender.pc.onicecandidate = function(event) {
			if (event.candidate) {
				console.log("onicecandidate");
				console.log(event);
				// XXX [CLIENT_ICE_03] XXX
				Kurento.ws.send(JSON.stringify({
					id : "iceCandidate",
					candidate : event.candidate
				}));
			}
		}

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
					id : "description",
					data : desc
				}));
				
				
			}, logError);
		}, logError);

		
	};

	

	this.onmessage = function(message) {
		var obj = JSON.parse(message.data);
		switch (obj.id) {
			case 'description':
				var sdp = new RTCSessionDescription(obj);

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