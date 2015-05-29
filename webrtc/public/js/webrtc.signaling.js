function SignalingChannel() {
	this.send = function(message) {
		$.ajax({
			type : "POST",
			url : "/rest/add",
			data : message,
			success : function(response){},
			contentType: "text/plain",
			dataType : "text"
		});

	}

}

var signalingChannel = new SignalingChannel();

var configuration = {
	'iceServers' : [ {
		'url' : 'stun:stun.l.google.com:19302'
	} ]
};
var pc;

// call start() to initiate

function start() {
	console.log('start');

    //compatibility for firefox and chrome
    var RTCPeerConnection = window.RTCPeerConnection
        || window.mozRTCPeerConnection
        || window.webkitRTCPeerConnection;
    
	pc = new RTCPeerConnection(configuration);

	// send any ice candidates to the other peer
	pc.onicecandidate = function(evt) {
		console.log('onicecandidate');
		if (evt.candidate)
			signalingChannel.send(JSON.stringify({
				'candidate' : evt.candidate
			}));
	};

	// let the 'negotiationneeded' event trigger offer generation
	/*pc.onnegotiationneeded = function() {
		console.log('onnegotiationneeded');

		pc.createOffer(localDescCreated, logError);
	}*/
	
	pc.createOffer(localDescCreated, logError);


	// once remote stream arrives, show it in the remote video element
	pc.onaddstream = function(evt) {
		console.log('onaddstream');

		remoteView.src = URL.createObjectURL(evt.stream);
	};

	// get a local stream, show it in a self-view and add it to be sent
	navigator.getUserMedia({
		'audio' : true,
		'video' : true
	}, function(stream) {
		selfView.src = URL.createObjectURL(stream);
		pc.addStream(stream);
	}, logError);
}

function localDescCreated(desc) {
	console.log('localDescCreated');

	pc.setLocalDescription(desc, function() {
		signalingChannel.send(JSON.stringify(pc.localDescription));
		
	}, logError);
}

signalingChannel.onmessage = function(evt) {
	console.log('onmessage');

	
	if (!pc)
		start();

	var message = JSON.parse(evt.data);
	if (message.sdp)
		pc.setRemoteDescription(new RTCSessionDescription(message.sdp),
				function() {
					// if we received an offer, we need to answer
					if (pc.remoteDescription.type == 'offer')
						pc.createAnswer(localDescCreated, logError);
				}, logError);
	else
		pc.addIceCandidate(new RTCIceCandidate(message.candidate));
};

function logError(error) {
	log(error.name + ': ' + error.message);
}
