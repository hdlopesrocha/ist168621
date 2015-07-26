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
var dataChannel;
var peerConnection = null;

function webrtc_init(icecand,sdpoffer) {
	peerConnection = new RTCPeerConnection(configuration,  {optional: [{RtpDataChannels: true}]});

	peerConnection.onicecandidate = function (e) {
//	    alert("onicecandidate");
		// candidate exists in e.candidate
	    if (e.candidate) 
		    icecand(e.candidate);
	};
	
	peerConnection.onnegotiationneeded = function(){
//	    alert("onnegotiationneeded");
	}
		
	 // once remote stream arrives, show it in the remote video element
	peerConnection.onaddstream = function (evt) {
		 alert("onaddstream");		
		 // remoteView.src = URL.createObjectURL(evt.stream);
	};
	
	dataChannel = peerConnection.createDataChannel("sendDataChannel", {reliable: false});
	dataChannel.onmessage = function(e){console.log("DC message:" +e.data);};
	dataChannel.onopen = function(){console.log("------ DATACHANNEL OPENED ------");};
	dataChannel.onclose = function(){console.log("------- DC closed! -------")};
	dataChannel.onerror = function(){console.log("DC ERROR!!!")};

	var sdpConstraints = {'mandatory':
	  {
	    'OfferToReceiveAudio': false,
	    'OfferToReceiveVideo': false
	  }
	};

	peerConnection.createOffer(function (sdp) {
	    peerConnection.setLocalDescription(sdp);
	   
	    sdpoffer(sdp);
	    //	    sendNegotiation("offer", sdp);
	    console.log("------ SEND OFFER ------");
	}, function(){}, sdpConstraints);
	
}

function webrtc_ice_candidate(iceCandidate){
	console.log("webrtc_ice_candidate");
	console.log(iceCandidate);
	peerConnection.addIceCandidate(new RTCIceCandidate(iceCandidate));
}

function webrtc_remote_description(answer){
	console.log("webrtc_remote_description");
	console.log(answer)
	peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
};
