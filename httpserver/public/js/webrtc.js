var RTCPeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
var IceCandidate = window.mozRTCIceCandidate || window.RTCIceCandidate;
var SessionDescription = window.mozRTCSessionDescription || window.RTCSessionDescription;
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

var pc = null;

function webrtc_init(icecand) {
	pc = new RTCPeerConnection(configuration);

	pc.onicecandidate = function (e) {
//	    alert("onicecandidate");
		// candidate exists in e.candidate
	    if (e.candidate) 
		    icecand(e.candidate);
	   		//send("icecandidate", JSON.stringify(e.candidate));
	};
	
	pc.onnegotiationneeded = function(){
//	    alert("onnegotiationneeded");
	}
		
	 // once remote stream arrives, show it in the remote video element
	pc.onaddstream = function (evt) {
		 alert("onaddstream");		
		 // remoteView.src = URL.createObjectURL(evt.stream);
	};
	
	var channel = pc.createDataChannel("sendDataChannel", {reliable: false});
	
	pc.createOffer(
		function (offer) {
//			alert("createOffer");		
	        pc.setLocalDescription(offer);
	    }, function (e) { }
	);
	
	console.log(pc);
}