var RTCPeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
var RTCIceCandidate = window.mozRTCIceCandidate || window.RTCIceCandidate;
var RTCSessionDescription = window.mozRTCSessionDescription || window.RTCSessionDescription;
navigator.getUserMedia = navigator.getUserMedia || navigator.mozGetUserMedia || navigator.webkitGetUserMedia;

var configuration = {
	iceServers: [
		{
			urls: "stun:stun.l.google.com:19302"
		},
		{	
			urls: "stun:23.21.150.121"
		}
	]
}

var dc = null;
var pc = null;

function webrtc_init(sdpoffer) {
	pc = new RTCPeerConnection(configuration,  {optional: [{RtpDataChannels: true}]});

	pc.oniceconnectionstatechange = function() {
		console.log("oniceconnectionstatechange");
		console.log(pc.iceConnectionState);
	};
	
	pc.onicecandidate = function (e) {
		console.log("onicecandidate");
	    if (e.candidate) {
	    	pc.addIceCandidate(e.candidate);
	    }
	};
	
	pc.onnegotiationneeded = function(){
		console.log("onnegotiationneeded");
	}
		
	// once remote stream arrives, show it in the remote video element
	pc.onaddstream = function (evt) {
       alert("onaddstream");		
       // remoteView.src = URL.createObjectURL(evt.stream);
	};
	
	dc = pc.createDataChannel("sendDataChannel", {reliable: false});
	dc.onmessage = function(e){console.log("DC message:" +e.data);};
	dc.onopen = function(){console.log("------ DATACHANNEL OPENED ------");};
	dc.onclose = function(){console.log("------- DC closed! -------")};
	dc.onerror = function(){console.log("DC ERROR!!!")};

	var sdpConstraints = {
	    'OfferToReceiveAudio': false,
	    'OfferToReceiveVideo': false
	};
	/*
	pc.createOffer(function (sdp) {
	    pc.setLocalDescription(sdp);
	    sdpoffer(sdp);
	    console.log("------ SEND OFFER ------");
	},  function(){}, sdpConstraints);	
	*/
}

function webrtc_remote_description(answer){
	/*
	console.log("setRemoteDescription");
	console.log(answer)
	pc.setRemoteDescription(new RTCSessionDescription(answer));
	*/
};