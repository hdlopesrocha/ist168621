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
var dataChannel;
var peerConnection = null;

function webrtc_init(icecand) {
	peerConnection = new RTCPeerConnection(configuration,  {optional: [{RtpDataChannels: true}]});

	peerConnection.onicecandidate = function (e) {
//	    alert("onicecandidate");
		// candidate exists in e.candidate
	    if (e.candidate) 
		    icecand(e.candidate);
	   		//send("icecandidate", JSON.stringify(e.candidate));
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
//	    sendNegotiation("offer", sdp);
	    console.log("------ SEND OFFER ------");
	}, null, sdpConstraints);

	
	function processIce(iceCandidate){
		  peerConnection.addIceCandidate(new RTCIceCandidate(iceCandidate));
		}

	function processAnswer(answer){
		  peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
		  console.log("------ PROCESSED ANSWER ------");
		};

	

	
}