window.AudioContext = window.AudioContext || window.webkitAudioContext;
navigator.getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;

var HyperWebSocket = new (function() {
    var primaryStream = null;
    var newParticipantsCallback = null;
    var newVideoCallback = null;
    var newRecordingCallback = null;
    var tagArrivedCallback = null;
    var contentArrivedCallback = null;
    var setTimeCallback = null;
    var removedUserCallback= null;
    var localVideoCallback=null;
    var serverTimeCallback=null;
    var operationTransformationCallback=null;
    var coordinationRequestCallback=null;
    var removedTagCallback = null;
	var microphoneState = true;
	var soundDetected = false;
	var pc = null;
	var ws = null;
    var local_none = {'offerToReceiveAudio':true,'offerToReceiveVideo':true};
    var screen_user = {video: {mediaSource: 'window' || 'screen'}};
    var audio_constraints = { 'offerToReceiveAudio':true,'offerToReceiveVideo':true};
    var remote_constraints = { 'offerToReceiveAudio':true,'offerToReceiveVideo':true};
    var screen_constraints = { audio: false,video: { mediaSource: "window" || "screen"}};

    function logError(err) {
        console.log(err);
    }

	this.toggleMicrophone = function() {
		microphoneState = !microphoneState;

		if(primaryStream){  
			var audioTracks = primaryStream.getAudioTracks();
		  	for (var i = 0, l = audioTracks.length; i < l; i++) {
		  		audioTracks[i].enabled = microphoneState;
		  	}
		}
		return microphoneState;
	}
	
	this.audioFunction= function(stream){
		function average(array){
        	var len = array.length;
        	var sum = 0;
        	for (var i = 0; i < len; i++) {
        		sum += Math.abs(array[i]);
        	}
        	return sum/len;
        }
		var soundProc = 0;
		var audioContext = new AudioContext();
		var maxAudioLevel = 0;
		
		var microphone = audioContext.createMediaStreamSource(stream);
		var javascriptNode = audioContext.createScriptProcessor(256, 1, 1);
		
		javascriptNode.onaudioprocess = function(event){
			if(soundProc==0){
				var perc = 0;
				if(microphoneState){
					var inputLevels = event.inputBuffer.getChannelData(0);
					var currentAudioLevel = average(inputLevels);
					maxAudioLevel = Math.max(maxAudioLevel, currentAudioLevel);
					perc = currentAudioLevel/maxAudioLevel;
				}
				if(perc>0.05){
					if(!soundDetected){
						soundDetected = true;
						HyperWebSocket.talk(true);
					}
				}else {
					if(soundDetected){
						soundDetected = false;
						HyperWebSocket.talk(false);
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

	this.talk = function(value){
		ws.send(JSON.stringify({
			id: "talk",
			value:value
		}));
	}

	this.deleteContent = function(cid){
		ws.send(JSON.stringify({
			id: "deleteContent",
			cid:cid
		}));
	}
	
	this.getContent = function(){
		ws.send(JSON.stringify({
			id: "getContent",
		}));
	}
	
	this.createContent = function(start,end,content){
		ws.send(JSON.stringify({
			id : "createContent",
			start: start,
			end: end,
			content: content
		}));
	}

	this.saveCollaborativeContent = function(content){
        ws.send(JSON.stringify({
			id : "saveCollab",
			data: content
		}));
	}

	this.removeTag = function(tid){
		ws.send(JSON.stringify({
			id : "removeTag",
			tid: tid
		}));
	}

	this.createTag = function(time,title,content){
		ws.send(JSON.stringify({
			id : "createTag",
			time: time,
			title: title,
			content: content
		}));
	}
	
	this.receiveMore = function(end,len){
		ws.send(JSON.stringify({
			id : "getMessages",
			len: len,
			end: end
		}));
	}
	
	this.setPlay=function(play){
		ws.send(JSON.stringify({
			id : "play",
			data:play
		}));
	}

    this.sendOperation = function(op,sid){
        console.log("sendOperation",op,sid);
        var obj = {
          id : "operation",
          data:op
        };
        if(sid!=null){
            obj.sid = sid;
        }
        ws.send(JSON.stringify(obj));
    }

	this.receiveRealtime = function(userId){
		console.log("receiveRealtime",userId);
		ws.send(JSON.stringify({
			id : "setRealTime",
			uid:userId
		}));
	}
	
	this.addUserGroup = function(userId){
		console.log("addUser",userId);
		ws.send(JSON.stringify({
			id : "addUser",
			uid:userId
		}));
	}
	

    this.removeUserGroup = function(userId){
        ws.send(JSON.stringify({
            id : "removeUser",
            uid:userId
        }));
    }

	this.receiveHistoric = function(userId,offset){
		console.log("receiveHistoric",userId);
		ws.send(JSON.stringify({
			id : "setHistoric",
			uid:userId,
			offset:offset
		}));
	}
	
	this.sendMessage = function(text){
		ws.send(JSON.stringify({
			id: "createMessage",
			data: text
		}));
	}

	this.start = function(groupId,mode,kscb,npcb,nvcb,nrcb,nmcb,tacb,cacb,trcb,stcb,rucb,lvcb,htcb,otcb,crcb,rtcb) {
		newParticipantsCallback = npcb;
		newVideoCallback = nvcb;
		newRecordingCallback = nrcb;
		newMessageCallback = nmcb;
		tagArrivedCallback = tacb;
		contentArrivedCallback = cacb;
		talkReceivedCallback = trcb;
		setTimeCallback = stcb;
		removedUserCallback = rucb;
		localVideoCallback = lvcb;
        serverTimeCallback=htcb;
        operationTransformationCallback = otcb;
        coordinationRequestCallback = crcb;
        removedTagCallback = rtcb;

        function wsurl(s) {
            var l = window.location;
            return ((l.protocol === "https:") ? "wss://" : "ws://") + l.hostname + (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "") + s;
        }

		if ("WebSocket" in window) {
			ws = new WebSocket(wsurl("/ws/room/" + groupId));
		
			ws.onerror = function() {
				console.log("Error!");
			};
			
			ws.onmessage = function(data) {
				var message = JSON.parse(data.data);
				var id = message.id;
				
				switch(id){
					case 'iceCandidate':
						// console.log(id,message);
						var candidate = new RTCIceCandidate(message.data);
				
						pc.addIceCandidate(candidate, function() {
							// console.log(candidate);
						}, logError);
					break;
					case 'answer':

						// console.log(id, message);
						var rsd = new RTCSessionDescription(message.data);
						// XXX [CLIENT_OFFER_08] XXX
						pc.setRemoteDescription(rsd, function(){
							// console.log("setRemoteSessionDescription",rsd);
						},logError);
					break;		
					case 'participants':
						console.log(id,message);
						for(var userId in message.data){
							newParticipantsCallback(message.data[userId]);
						}
					break;
					case 'removedUser':
                        removedUserCallback(message.uid);
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
							console.log("msg",msg);
							newMessageCallback(msg.source,msg.time,msg.text,msg.name, msg.id, msg.seq);
						}
						
						break;
					case 'tag':
						var msg = message.data;
						tagArrivedCallback(msg.id,msg.time,msg.title,msg.content);


						break;
			        case 'removeTag':
						removedTagCallback(message.tid);
						break;
					case 'talk':
						var msg = message.data;
						talkReceivedCallback(msg.uid,msg.value);
						break;
					case 'time':
						var msg = message.data;
						serverTimeCallback(new Date(msg.time));
						break;

                    case 'setTime':
                        setTimeCallback(new Date(message.time));
                        break;
                    case 'operation':
                        operationTransformationCallback(ot.TextOperation.fromJSON(message.data));
                        break;

                    case 'coordinate':
                        coordinationRequestCallback(message.sid);
                        break;
					default:
						break;					
				}
			};
			
			ws.onopen = function() {
				kscb();

                // XXX [CLIENT_ICE_01] XXX
                pc = new RTCPeerConnection({
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
                        // XXX [CLIENT_ICE_03] XXX
                        ws.send(JSON.stringify(msg));
                    }
                }


	            pc.onaddstream = function (e) {
					console.log(e);
					stq = e.stream;
					console.log(stq);
					newVideoCallback(URL.createObjectURL(stq));
				};

				// XXX [CLIENT_OFFER_01] XXX

				if(mode==0){
					navigator.mediaDevices.getUserMedia({"audio":true, "video":true }).then(function(stream) {
                        localVideoCallback(window.URL.createObjectURL(stream));
                        primaryStream = stream;
                        HyperWebSocket.audioFunction(stream);
                        pc.addStream(stream);
                        pc.createOffer(function (lsd) {
                            console.log("createOfferToSendReceive",lsd);
                            // XXX [CLIENT_OFFER_02] XXX
                            pc.setLocalDescription(lsd, function() {
                                // XXX [CLIENT_OFFER_03] XXX
                                ws.send(JSON.stringify({
                                    id : "offer",
                                    data : lsd
                                }));
                            }, logError);
                        }, logError,remote_constraints);
                    }).catch(logError);
				}
				else if(mode==1 ){
					navigator.mediaDevices.getUserMedia(screen_constraints).then(function(stream) {
						localVideoCallback(window.URL.createObjectURL(stream));

						pc.addStream(stream);
						pc.createOffer(function (lsd) {
							console.log("createOfferToSendReceive",lsd);
							// XXX [CLIENT_OFFER_02] XXX
							pc.setLocalDescription(lsd, function() {
								// XXX [CLIENT_OFFER_03] XXX
								ws.send(JSON.stringify({
									id : "offer",
									data : lsd
								}));
							}, logError);
						}, logError,remote_constraints);
					}).catch(logError);
				} else if(mode==2){
					pc.createOffer(function (lsd) {
						console.log("createOfferToReceive",lsd);
						pc.setLocalDescription(lsd, function() {
							ws.send(JSON.stringify({
								id : "offer",
								data : lsd
							}));
						}, logError);
					}, logError,local_none);
				}
			};

			ws.onclose = function() {
				console.log("Connection is closed...");
			};
		
			window.onbeforeunload = function() {
				ws.close();
			};
		
		} else {
			console.log("no websocket support!");
		}
	};

	return this;
})();
console.log(this);
console.log(HyperWebSocket);