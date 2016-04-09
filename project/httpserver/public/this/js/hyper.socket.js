window.AudioContext = window.AudioContext || window.webkitAudioContext;
navigator.getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;

var HyperWebSocket = new (function() {
    var primaryStream = null;
    var participantPresenceHandler = null;
    var remoteVideoHandler = null;
    var timeTagArrivedHandler = null;
    var channelsArrivedHandler = null;
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
    var local_none = {offerToReceiveAudio:true,offerToReceiveVideo:true};
    var screen_user = {video: {mediaSource: 'window' || 'screen'}, audio: false};
    var camera_user = {audio:true, video:true };

    var audio_constraints = { 'offerToReceiveAudio':true,'offerToReceiveVideo':true};
    var remote_constraints = { 'offerToReceiveAudio':true,'offerToReceiveVideo':true};
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
                var inputLevels = event.inputBuffer.getChannelData(0);
                var currentAudioLevel = average(inputLevels);

                maxAudioLevel = Math.max(maxAudioLevel, currentAudioLevel);
                if(maxAudioLevel){
                    perc = currentAudioLevel/maxAudioLevel;
                }

				if(perc>0.10){
					if(!soundDetected){
						soundDetected = true;
						this.talk(true);
					}
				}else {
					if(soundDetected){
						soundDetected = false;
						this.talk(false);
					}	
				}
			}
			soundProc=soundProc+1;
			if(soundProc>10){
				soundProc = 0;
			}
		}.bind(this);
		
		microphone.connect(javascriptNode);
		javascriptNode.connect(audioContext.destination);
	}

    // ===============================================
    // ================ SEND MESSAGES ================
    // ===============================================

	this.talk = function(value){
		ws.send(JSON.stringify({
			cmd: "talk",
			value:value
		}));
	}

	this.deleteContent = function(cid){
		ws.send(JSON.stringify({
			cmd: "deleteContent",
			cid:cid
		}));
	}
	
	this.getContent = function(){
		ws.send(JSON.stringify({
			cmd: "getContent",
		}));
	}
	
	this.createContent = function(start,end,content){
		ws.send(JSON.stringify({
			cmd : "createContent",
			start: start,
			end: end,
			content: content
		}));
	}

	this.saveCollaborativeContent = function(content){
        ws.send(JSON.stringify({
			cmd : "saveCollab",
			data: content
		}));
	}

	this.removeTag = function(tid){
		ws.send(JSON.stringify({
			cmd : "removeTag",
			tid: tid
		}));
	}

	this.createTag = function(id,time,title){
		ws.send(JSON.stringify({
			cmd : "createTag",
			id:id,
			time: time,
			title: title
		}));
	}
	
	this.receiveMore = function(end,len){
		ws.send(JSON.stringify({
			cmd : "getMessages",
			len: len,
			end: end
		}));
	}
	
	this.setPlay=function(play){
		ws.send(JSON.stringify({
			cmd : "play",
			data:play
		}));
	}

    this.sendOperation = function(op,sid){
        console.log("sendOperation",op,sid);
        var obj = {
          cmd : "operation",
          data:op
        };
        if(sid!=null){
            obj.sid = sid;
        }
        ws.send(JSON.stringify(obj));
    }

	this.addUserGroup = function(userId){
		console.log("addUser",userId);
		ws.send(JSON.stringify({
			cmd : "addUser",
			uid:userId
		}));
	}
	

    this.removeUserGroup = function(userId){
        ws.send(JSON.stringify({
            cmd : "removeUser",
            uid:userId
        }));
    }



	this.receiveRealTime = function(userId,sessionId){
		console.log("receiveRealTime",userId);
		var msg = {
            cmd : "setRealTime",
            uid:userId
        };
		if(sessionId){
		    msg.sid = sessionId;
		}
		ws.send(JSON.stringify(msg));
	}


	this.receiveHistoric = function(userId,offset,sessionId){
		console.log("receiveHistoric",userId);
		var msg = {
            cmd : "setHistoric",
            uid:userId,
            offset:offset
        };
    	if(sessionId){
		    msg.sid = sessionId;
		}
		ws.send(JSON.stringify(msg));
	}
	
	this.sendMessage = function(text){
		ws.send(JSON.stringify({
			cmd: "createMessage",
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


    this.setChannelsArrivedHandler = function (handler) {
        channelsArrivedHandler = handler;
    };

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

            ws.callbacks = [];
		    ws.onCmd = function(cmd,callback){
                ws.callbacks.push({cmd:cmd,callback:callback});
                return ws;
		    };

			ws.onerror = function() {
				console.log("Error!");
			};

			ws.onCmd('iceCandidate',function(message){
	            console.log(message);
				var candidate = new RTCIceCandidate(message.data);
				pc.addIceCandidate(candidate, function() {

				}, logError);
			});

		    ws.onCmd('answer',function(message){
                console.log(message);
                var rsd = new RTCSessionDescription(message.data);
                // XXX [CLIENT_OFFER_08] XXX
                pc.setRemoteDescription(rsd, function(){
                    // console.log("setRemoteSessionDescription",rsd);
                },logError);
			});

		    ws.onCmd('participants',function(message){
	            if(participantPresenceHandler){
    				for(var userId in message.data){
					    participantPresenceHandler(message.data[userId]);
					}
				}
			});

		    ws.onCmd('removedUser',function(message){
	            if(removedUserHandler){
                    removedUserHandler(message.uid);
                }
			});

	        ws.onCmd('content',function(message){
                if(hyperContentArrivedHandler){
                    hyperContentArrivedHandler(message.data,message.more);
                }
			});

	        ws.onCmd('rec',function(message){
                if(videoRecordingHandler){
                    delete message.cmd;
                    videoRecordingHandler(message);
                }
			});

            ws.onCmd('channels', function(message){
                channelsArrivedHandler(message.data,message.play);

            });

	        ws.onCmd('msg',function(message){
                for(var i in message.data){
                    var item = message.data[i];
                    console.log("msg",item);
                    if(messageArrivedHandler){
                        messageArrivedHandler(item.source,item.time,item.text,item.name, item.id, item.seq);
                    }
                }
			});

	        ws.onCmd('tag',function(message){
                if(timeTagArrivedHandler){
                     var item = message.data;
                     timeTagArrivedHandler(item.id,item.time,item.title);
                }
			});

	        ws.onCmd('removeTag',function(message){
                if (removedTagHandler){
                    removedTagHandler(message.tid);
                }
			});

	        ws.onCmd('talk',function(message){
                if(voiceDetectedHandler){
                    var msg = message.data;
                    voiceDetectedHandler(msg.uid,msg.value);
                }
			});

            ws.onCmd('time',function(message){
                if(serverTimeHandler){
                    var item = message.data;
                    serverTimeHandler(new Date(item.time));
                }
            });

            ws.onCmd('setTime',function(message){
                if(lookAtTimeHandler){
                   lookAtTimeHandler(new Date(message.time));
                }
            });

            ws.onCmd('operation',function(message){
                if(operationTransformationHandler){
                    operationTransformationHandler(ot.TextOperation.fromJSON(message.data));
                }
            });

            ws.onCmd('coordinate',function(message){
                if(coordinationRequestHandler){
                    coordinationRequestHandler(message.sid);
                }
            });

            ws.onCmd('qrCode',function(message){
                if(qrCodeHandler){
                    qrCodeHandler(message.hash,message.data);
                }
            });

			ws.onmessage = function(data) {
				var message = JSON.parse(data.data);
				var cmd = message.cmd;

				for(var i in  ws.callbacks){
                    var item = ws.callbacks[i];
                    if(item.cmd == cmd){
                        item.callback(message);
                    }
				}
			};
			
			ws.onopen = function() {
				kscb();

                // XXX [CLIENT_ICE_01] XXX
                pc = new RTCPeerConnection({
                    iceServers : [
                        {
                            urls : "stun:stun.iptel.org"
                        },
                        {
                            urls : "stun:stun.ekiga.net"
                        },
                        {
                            urls : "stun:stun.fwdnet.net"
                        },
                        {
                            urls : "stun:stun.ideasip.com"
                        }
                    ]
                });

                // stun -i wlan0 -p 3478 -v citysdk.tagus.ist.utl.pt
                // stun -i wlan0 -p 19302 -v 64.233.184.127


                // XXX [CLIENT_ICE_02] XXX
                pc.onicecandidate = function(event) {
                    if (event.candidate) {
                        var msg = {
                            cmd : "iceCandidate",
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

				if(mode==0 || mode==1){
					navigator.mediaDevices.getUserMedia(mode==0 ? camera_user: screen_user).then(function(stream) {
                        if(localVideoHandler){
                            localVideoHandler(window.URL.createObjectURL(stream));
                        }
                        primaryStream = stream;
                        console.log(stream);
                        if(mode==0){
                            HyperWebSocket.audioFunction(stream);
                        }
                        pc.addStream(stream);
                        pc.createOffer(function (lsd) {
                            console.log("createOfferToSendReceive",lsd);
                            // XXX [CLIENT_OFFER_02] XXX
                            pc.setLocalDescription(lsd, function() {
                                // XXX [CLIENT_OFFER_03] XXX
                                ws.send(JSON.stringify({
                                    cmd : "offer",
                                    data : lsd
                                }));
                            }, logError);
                        }, logError,remote_constraints);
                    }.bind(this)).catch(logError);
				}
				else if(mode==2){
					pc.createOffer(function (lsd) {
						console.log("createOfferToReceive",lsd);
						pc.setLocalDescription(lsd, function() {
							ws.send(JSON.stringify({
								cmd : "offer",
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
