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


function webrtc_init(sdpoffer) {
	peerConnection = new RTCPeerConnection(configuration,  {optional: [{RtpDataChannels: true}]});

	

	function webrtc_create_offer(){
		var sdpConstraints = {
			    'OfferToReceiveAudio': false,
			    'OfferToReceiveVideo': false
			};
		
		peerConnection.createOffer(function (sdp) {
		    peerConnection.setLocalDescription(sdp);
		    sdpoffer(sdp);
		    console.log("------ SEND OFFER ------");
		}, function(){}, sdpConstraints);	
	}

	
	peerConnection.onicecandidate = function (e) {
		console.log("onicecandidate");
	    if (e.candidate) {
	    	peerConnection.addIceCandidate(e.candidate);
	    	webrtc_create_offer();
	    }
	};
	
	peerConnection.onnegotiationneeded = function(){
		console.log("onnegotiationneeded");
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

	webrtc_create_offer();

}

function webrtc_remote_description(answer){
	console.log("setRemoteDescription");
	console.log(answer)
	peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
};