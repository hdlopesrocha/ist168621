window.AudioContext = window.AudioContext || window.webkitAudioContext;
navigator.getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;

var HyperWebSocket = new (function() {
    var primaryStream = null;
    var participantPresenceHandler = null;
    var remoteVideoHandler = null;
    var timeTagArrivedHandler = null;
    var hyperContentArrivedHandler = null;
    var lookAtTimeHandler = null;
    var removedUserHandler= null;
    var voiceDetectedHandler = null;
    var qrCodeHandler = null;
    var messageArrivedHandler = null;
    var videoRecordingHandler = null;
    var localVideoHandler=null;
    var serverTimeHandler=null;
    var operationTransformationHandler=null;
    var coordinationRequestHandler=null;
    var removedTagHandler = null;
	var microphoneState = true;
	var soundDetected = false;
	var pc = null;
	var ws = null;
    var local_none = {'offerToReceiveAudio':true,'offerToReceiveVideo':true};
    var screen_user = {video: {mediaSource: 'window' || 'screen'}};
    var audio_constraints = { 'offerToReceiveAudio':true,'offerToReceiveVideo':true};
    var remote_constraints = { 'offerToReceiveAudio':true,'offerToReceiveVideo':true};
    var screen_constraints = { audio: false,video: { mediaSource: "window" || "screen"}};

    // ===============================================
    // ================== UTILITIES ==================
    // ===============================================

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

    // ===============================================
    // ================ SEND MESSAGES ================
    // ===============================================

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

	this.createTag = function(time,title){
		ws.send(JSON.stringify({
			id : "createTag",
			time: time,
			title: title
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

	this.receiveRealTime = function(userId){
		console.log("receiveRealTime",userId);
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

    // =================================================
    // ============== SET HANDLER METHODS ==============
    // =================================================

	this.setOnRemovedTagHandler= function(handler){
	    removedTagHandler = handler;
	}

    this.setOnCoordinationRequestHandler = function(handler){
        coordinationRequestHandler = handler;
    }

    this.setOnOperationTransformationReceivedHandler = function(handler){
        operationTransformationHandler = handler;
    }

    this.setServerTimeSynchronizationHandler = function(handler){
        serverTimeHandler = handler;
    }

    this.setOnLocalVideoAvailableHandler = function(handler){
		localVideoHandler = handler;
    }

    this.setOnUserRemovedHandler = function(handler){
        removedUserHandler = handler;
    }

    this.setOnLookAtTimeRequestHandler = function(handler){
        lookAtTimeHandler = handler;
    }

    this.setVoiceDetectedHandler = function(handler){
        voiceDetectedHandler = handler;
    }

    this.setOnHyperContentArrivedHandler = function(handler){
        hyperContentArrivedHandler = handler;
    }

    this.setOnTimeTagArrivedHandler = function(handler){
        timeTagArrivedHandler = handler;
    }

    this.setOnMessageArrivedHandler = function(handler){
        messageArrivedHandler = handler;
    }

    this.setOnNewVideoRecordingArrivedHandler = function(handler){
        videoRecordingHandler = handler;
    }

    this.setOnRemoteVideoAvailableHandler = function(handler){
		remoteVideoHandler = handler;
    }

    this.setParticipantPresenceHandler = function(handler){
        participantPresenceHandler = handler;
    }

    this.setOnQrCodeEventHandler = function(handler){
        qrCodeHandler = handler;
    }

    // =================================================
    // ========== WEBRTC and RECEIVE MESSAGES ==========
    // =================================================

	this.start = function(groupId,mode,kscb) {

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
						if(participantPresenceHandler){
    						for(var userId in message.data){
							    participantPresenceHandler(message.data[userId]);
							}
						}
					break;
					case 'removedUser':
					    if(removedUserHandler){
                            removedUserHandler(message.uid);
                        }
					break;
					case 'content':
					    if(hyperContentArrivedHandler){
						    hyperContentArrivedHandler(message.data,message.more);
						}
					break;
					case 'rec':
						if(videoRecordingHandler){
						    delete message.id;
       					    videoRecordingHandler(message);
						}
						break;
					case 'msg':
						for(var i in message.data){
							var msg = message.data[i];
							console.log("msg",msg);
							if(messageArrivedHandler){
							    messageArrivedHandler(msg.source,msg.time,msg.text,msg.name, msg.id, msg.seq);
						    }
						}
						
						break;
					case 'tag':
					    if(timeTagArrivedHandler){
                            var msg = message.data;
                            timeTagArrivedHandler(msg.id,msg.time,msg.title,msg.content);
                        }
						break;
			        case 'removeTag':
			            if (removedTagHandler){
						    removedTagHandler(message.tid);
						}
						break;
					case 'talk':
						if(voiceDetectedHandler){
                            var msg = message.data;
                            voiceDetectedHandler(msg.uid,msg.value);
						}
						break;
					case 'time':
						if(serverTimeHandler){
						    var msg = message.data;
						    serverTimeHandler(new Date(msg.time));
						}
						break;

                    case 'setTime':
                        if(lookAtTimeHandler){
                            lookAtTimeHandler(new Date(message.time));
                        }
                        break;
                    case 'operation':
                        if(operationTransformationHandler){
                            operationTransformationHandler(ot.TextOperation.fromJSON(message.data));
                        }
                        break;

                    case 'coordinate':
                        if(coordinationRequestHandler){
                            coordinationRequestHandler(message.sid);
                        }
                        break;
                    case 'qrCode':
                        if(qrCodeHandler){
                            qrCodeHandler(message.hash,message.data);
                        }
                        break

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
					if(remoteVideoHandler){
					    remoteVideoHandler(URL.createObjectURL(stq));
					}
				};

				// XXX [CLIENT_OFFER_01] XXX

				if(mode==0){
					navigator.mediaDevices.getUserMedia({"audio":true, "video":true }).then(function(stream) {
                        if(localVideoHandler){
                            localVideoHandler(window.URL.createObjectURL(stream));
                        }
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
						if(localVideoHandler){
						    localVideoHandler(window.URL.createObjectURL(stream));
						}

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
