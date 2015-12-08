
function wsurl(s) {
	var l = window.location;
	return ((l.protocol === "https:") ? "wss://" : "ws://") + l.hostname + (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "") + s;
}

var local_none = {
		'offerToReceiveAudio':true, 
		'offerToReceiveVideo':true 
	};

var local_user = {
	"audio" : true,
	"video" : true
};





var screen_user = {
	video: {
	    mediaSource: 'window' || 'screen'
	}
};

var audio_constraints = { 
	'offerToReceiveAudio':true, 
	'offerToReceiveVideo':true 
};

var remote_constraints = { 
	'offerToReceiveAudio':true, 
	'offerToReceiveVideo':true 
};

function logError(err) {
	console.log(err);
}

var newParticipantsCallback = null; 
var newVideoCallback = null; 
var mixerVideoCallback = null;
var newRecordingCallback = null;
var tagArrivedCallback = null;
var contentArrivedCallback = null;

window.AudioContext = window.AudioContext || window.webkitAudioContext;
navigator.getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;


function average(array){
	var len = array.length;
	var sum = 0;
	for (var i = 0; i < len; i++) {
		sum += Math.abs(array[i]);
	}
	return sum/len;
}


var soundDetected = false;
function audioFunction(stream){
	var soundProc = 0;
	var audioContext = new AudioContext();
	var maxAudioLevel = 0;
	
	var microphone = audioContext.createMediaStreamSource(stream);
	var javascriptNode = audioContext.createScriptProcessor(256, 1, 1);
	
	javascriptNode.onaudioprocess = function(event){
		if(soundProc==0){
			var inputLevels = event.inputBuffer.getChannelData(0);
			var currentAudioLevel = average(inputLevels);
			maxAudioLevel = Math.max(maxAudioLevel, currentAudioLevel);
			var perc = currentAudioLevel/maxAudioLevel;
			if(perc>0.05){
				if(!soundDetected){
					soundDetected = true;
					Kurento.talk(true);
				}
			}else {
				if(soundDetected){
					soundDetected = false;
					Kurento.talk(false);
				}
				
			}
			
		
		}
		soundProc=soundProc+1;
		if(soundProc>4){
			soundProc = 0;
		}
	};
	
	microphone.connect(javascriptNode);
	javascriptNode.connect(audioContext.destination);
}


var Kurento = new (function() {
	 
	
	this.peerConnection = {};

	this.webSocket = null;

	this.talk = function(value){
		Kurento.webSocket.send(JSON.stringify({
			id: "talk",
			value:value
		}));
	}
	
	this.getContent = function(){
		Kurento.webSocket.send(JSON.stringify({
			id: "getContent",
		}));
	}
	
	this.createContent = function(start,end,content){
		Kurento.webSocket.send(JSON.stringify({
			id : "createContent",
			start: start,
			end: end,
			content: content
		}));
	}
	
	this.createTag = function(time,title,content){
		Kurento.webSocket.send(JSON.stringify({
			id : "createTag",
			time: time,
			title: title,
			content: content
		}));
	}
	
	this.receiveMore = function(end,len){
		Kurento.webSocket.send(JSON.stringify({
			id : "getMessages",
			len: len,
			end: end
		}));
	}
	
	this.setPlay=function(play){
		Kurento.webSocket.send(JSON.stringify({
			id : "play",
			data:play
		}));
	}
	
	this.receiveRealtime = function(userId){
		console.log("receiveRealtime",userId);
		Kurento.webSocket.send(JSON.stringify({
			id : "setRealtime",
			uid:userId
		}));
	}
	
	this.addUser = function(userId){
		console.log("addUser",userId);
		Kurento.webSocket.send(JSON.stringify({
			id : "addUser",
			uid:userId
		}));
	}
	
	
	this.receiveHistoric = function(userId,offset){
		console.log("receiveHistoric",userId);
		Kurento.webSocket.send(JSON.stringify({
			id : "setHistoric",
			uid:userId,
			offset:offset
		}));
	}
	
	this.sendMessage = function(text){
		Kurento.webSocket.send(JSON.stringify({
			id: "createMessage",
			data: text
		}));
	}

	this.createPeerConnection = function(name) {
		// XXX [CLIENT_ICE_01] XXX
		var pc = new RTCPeerConnection({
			iceServers : [ {
				urls : "stun:stun.l.google.com:19302"
			}, {
				urls : "stun:23.21.150.121"
			} ]
		});
		
		// XXX [CLIENT_ICE_02] XXX		
		pc.onicecandidate = function(event) {
			if (event.candidate) {
				var msg = {
					id : "iceCandidate",
					candidate : event.candidate
				}
				if(name){
					msg.name=name;
				}
				
				// XXX [CLIENT_ICE_03] XXX
				Kurento.webSocket.send(JSON.stringify(msg));
			}
		}
		
		Kurento.peerConnection[name] = pc;
	}
	
	this.start = function(groupId,mode,kscb,npcb,nvcb,mvcb,nrcb,nmcb,tacb,cacb,trcb) {		
		newParticipantsCallback = npcb;
		newVideoCallback = nvcb;
		mixerVideoCallback = mvcb;
		newRecordingCallback = nrcb;
		newMessageCallback = nmcb;
		tagArrivedCallback = tacb;
		contentArrivedCallback = cacb;
		talkReceivedCallback = trcb;
		
		if ("WebSocket" in window) {
			Kurento.webSocket = new WebSocket(wsurl("/ws/room/" + groupId));
		
			Kurento.webSocket.onerror = function() {
				console.log("Error!");
			};
			
			Kurento.webSocket.onmessage = function(data) {
				var message = JSON.parse(data.data);
				var id = message.id;
				
				switch(id){
					case 'iceCandidate':
						console.log(id,message);
						var candidate = new RTCIceCandidate(message.data);
				
						Kurento.peerConnection[message.name].addIceCandidate(candidate, function() {
							console.log(candidate);
						}, logError);
					break;
					case 'answer':
						var name = message.name;
						
						console.log(id, message);
						var rsd = new RTCSessionDescription(message.data);
						// XXX [CLIENT_OFFER_08] XXX
						Kurento.peerConnection[message.name].setRemoteDescription(rsd, function(){
							console.log("setRemoteSessionDescription",rsd);
						},logError);
					break;		
					case 'participants':
						console.log(id,message);
						for(var userId in message.data){
							newParticipantsCallback(message.data[userId]);
						}
					break;
					case 'content':
						contentArrivedCallback(message.data,message.more);
					break;
					case 'rec':
						delete message.id;
						newRecordingCallback(message);						
						break;
					case 'msg':
						for(var i in message.data){
							var msg = message.data[i];
							newMessageCallback(msg.source,msg.time,msg.text,msg.name, msg.id, msg.seq);
						}
						
						break;
					case 'tag':
						var msg = message.data;
						tagArrivedCallback(msg.id,msg.time,msg.title,msg.content);

						
						break;
					case 'talk':
						var msg = message.data;

						
						talkReceivedCallback(msg.uid,msg.value);
						break;
						
					default:
						break;					
				}
			};
			
			Kurento.webSocket.onopen = function() {
				kscb();
				Kurento.createPeerConnection("main");
				Kurento.createPeerConnection("mixer");

				
				if(mode==1){
					navigator.mediaDevices.getUserMedia({ audio: true, video: true }).then(function(stream) {					
						Kurento.peerConnection["main"].addStream(stream);
						Kurento.peerConnection["main"].createOffer(function (lsd) {		
							console.log("createOfferToSendReceive",lsd);
							// XXX [CLIENT_OFFER_02] XXX
							Kurento.peerConnection["main"].setLocalDescription(lsd, function() {
								// XXX [CLIENT_OFFER_03] XXX		
								Kurento.webSocket.send(JSON.stringify({
									id : "offer",
									name : "main",
									data : lsd
								}));
							}, logError);
						}, logError,remote_constraints);
					}).catch(logError);
						
				}
				
				
				// XXX [CLIENT_OFFER_01] XXX
				else if(mode==0 ){
					navigator.getUserMedia(local_user, function(stream) {
						audioFunction(stream);
						Kurento.peerConnection["main"].addStream(stream);
						Kurento.peerConnection["main"].createOffer(function (lsd) {		
							console.log("createOfferToSendReceive",lsd);
							// XXX [CLIENT_OFFER_02] XXX
							Kurento.peerConnection["main"].setLocalDescription(lsd, function() {
								// XXX [CLIENT_OFFER_03] XXX		
								Kurento.webSocket.send(JSON.stringify({
									id : "offer",
									name : "main",
									data : lsd
								}));
							}, logError);
						}, logError,remote_constraints);
					}, logError);
				} else if(mode==2){
					Kurento.peerConnection["main"].createOffer(function (lsd) {
						console.log("createOfferToReceive",lsd);
						Kurento.peerConnection["main"].setLocalDescription(lsd, function() {
							Kurento.webSocket.send(JSON.stringify({
								id : "offer",
								name : "main",
								data : lsd
							}));
						}, logError);
					}, logError,local_none);
				}
				
				Kurento.peerConnection["mixer"].createOffer(function (lsd) {
					console.log("createOfferToReceive",lsd);
					Kurento.peerConnection["mixer"].setLocalDescription(lsd, function() {
						Kurento.webSocket.send(JSON.stringify({
							id : "offer",
							name : "mixer",
							data : lsd
						}));
					}, logError);
				}, logError,audio_constraints);
				
				Kurento.peerConnection["main"].onaddstream = function (e) {
					console.log("main",e);
					stq = e.stream;
					console.log(stq);
					newVideoCallback(URL.createObjectURL(stq));
				};
				
				Kurento.peerConnection["mixer"].onaddstream = function (e) {
					console.log("mixer",e);
					mixerVideoCallback(URL.createObjectURL(e.stream));
				};
			};

			Kurento.webSocket.onclose = function() {
				console.log("Connection is closed...");
			};
		
			window.onbeforeunload = function() {
				Kurento.webSocket.close();
			};
		
		} else {
			console.log("no websocket support!");
		}
	};

	return this;
})();