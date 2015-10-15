
function wsurl(s) {
	var l = window.location;
	return ((l.protocol === "https:") ? "wss://" : "ws://") + l.hostname + (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "") + s;
}


var local_constraints = {
	"audio" : true,
	"video" : true
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

var Kurento = new (function() {
	
	this.pc = {};

	this.ws = null;

	this.createTag = function(time,title,content){
		Kurento.ws.send(JSON.stringify({
			id : "addTag",
			time: time,
			title: title,
			content: content
		}));
	}
	

	this.receiveMore = function(end,len){
		Kurento.ws.send(JSON.stringify({
			id : "getmsg",
			len: len,
			end: end
		}));
	}
	
	
	this.setPlay=function(play){
		Kurento.ws.send(JSON.stringify({
			id : "play",
			data:play
		}));
	}
	
	this.receiveRealtime = function(userId){
		Kurento.ws.send(JSON.stringify({
			id : "realtime",
			uid:userId
		}));
	}
	
	this.sendMessage = function(text){
		Kurento.ws.send(JSON.stringify({
			id: "msg",
			data: text
		}));
	}
	
	this.receiveHistoric = function(userId,offset){
		Kurento.ws.send(JSON.stringify({
			id : "historic",
			uid:userId,
			offset:offset
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
				Kurento.ws.send(JSON.stringify(msg));
			}
		}
		
		Kurento.pc[name] = pc;
	}
	
	
	this.start = function(groupId,npcb,nvcb,mvcb,nrcb,nmcb,tacb) {		
		newParticipantsCallback = npcb;
		newVideoCallback = nvcb;
		mixerVideoCallback = mvcb;
		newRecordingCallback = nrcb;
		newMessageCallback = nmcb;
		tagArrivedCallback = tacb;
		
		if ("WebSocket" in window) {
			Kurento.ws = new WebSocket(wsurl("/ws/room/" + groupId));
		
			Kurento.ws.onerror = function() {
				console.log("Error!");

			};
			
			Kurento.ws.onmessage = function(data) {
				var message = JSON.parse(data.data);
				var id = message.id;
				
				switch(id){
					case 'iceCandidate':
						console.log(id,message);
						var candidate = new RTCIceCandidate(message.data);
				
						Kurento.pc[message.name].addIceCandidate(candidate, function() {
							console.log(candidate);
						}, logError);
					break;
					case 'answer':
						var name = message.name;
						
						console.log(id, message);
						var rsd = new RTCSessionDescription(message.data);
						// XXX [CLIENT_OFFER_08] XXX
						Kurento.pc[message.name].setRemoteDescription(rsd, function(){
							console.log("setRemoteSessionDescription",rsd);
						},logError);
					break;		
					case 'participants':
						console.log(id,message);
						for(var uid in message.data){
							newParticipantsCallback(uid, message.data[uid]);
						}
					break;
					case 'rec':
						delete message.id;
						newRecordingCallback(message);						
						break;
					case 'msg':
						for(var i in message.data){
							var msg = message.data[i];
							newMessageCallback(msg.uid,msg.time,msg.text,msg.name, msg.id, msg.seq);
						}
						
						break;
					case 'tag':
						var msg = message.data;
						tagArrivedCallback(msg.id,msg.time,msg.title,msg.content);

						
						break;
					default:
						break;					
				}
			};

		
			
			Kurento.ws.onopen = function() {
				Kurento.createPeerConnection("main");
				Kurento.createPeerConnection("mixer");

				// XXX [CLIENT_OFFER_01] XXX
				navigator.getUserMedia(local_constraints, function(stream) {
					Kurento.pc["main"].addStream(stream);
					Kurento.pc["main"].createOffer(function (lsd) {		
						console.log("createOfferToSendReceive",lsd);
						// XXX [CLIENT_OFFER_02] XXX
						Kurento.pc["main"].setLocalDescription(lsd, function() {
							// XXX [CLIENT_OFFER_03] XXX		
							Kurento.ws.send(JSON.stringify({
								id : "offer",
								name : "main",
								data : lsd
							}));
						}, logError);
					}, logError,remote_constraints);
				}, logError);
				
				Kurento.pc["mixer"].createOffer(function (lsd) {
					console.log("createOfferToReceive",lsd);

					Kurento.pc["mixer"].setLocalDescription(lsd, function() {
						Kurento.ws.send(JSON.stringify({
							id : "offer",
							name : "mixer",
							data : lsd
						}));
					}, logError);
				}, logError,remote_constraints);
				
				Kurento.pc["main"].onaddstream = function (e) {
					console.log("main",e);
					stq = e.stream;
					console.log(stq);
					newVideoCallback(URL.createObjectURL(stq));
				};
				
				Kurento.pc["mixer"].onaddstream = function (e) {
					console.log("mixer",e);
					mixerVideoCallback(URL.createObjectURL(e.stream));
				};
			};

			Kurento.ws.onclose = function() {
				console.log("Connection is closed...");
			};
		
			window.onbeforeunload = function() {
				Kurento.ws.close();
			};
		
		
		} else {
			console.log("no websocket support!");
		}	
	};

	return this;
})();