function onExistingParticipants(msg, uid) {
	var constraints = {
		audio : true,
		video : {
			mandatory : {
				maxWidth : 320,
				maxFrameRate : 15,
				minFrameRate : 15
			}
		}
	};

}


function onNewParticipant(msg) {



}






var Kurento = (function(){
	
	function wsurl(s) {
	    var l = window.location;
	    return ((l.protocol === "https:") ? "wss://" : "ws://") + l.hostname + (((l.port != 80) && (l.port != 443)) ? ":" + l.port : "") + s;
	}	
	
	var ws = null;
		
	
	
	this.init = function(uid,groupId,ready,participants, newparticipant){

		 if ("WebSocket" in window) {
			 var path = wsurl("/ws/room/"+groupId);
			 ws = new WebSocket(path);

			 ws.onerror = function(){
           	 console.log("Error!");

			 };
			 
			 ws.onmessage = function (message) {
					console.info('Received message: ' + message.data);
					var obj = JSON.parse(message.data);
					switch (obj.id) {
						case 'existingParticipants':
							participants(obj.data);
							onExistingParticipants(obj,uid);
							break;
						case 'newParticipantArrived':
							newparticipant(obj.data);
							onNewParticipant(obj);
							break;
						default:
							break;
					}
			 
			 };
            
            ws.onopen = function(){
           	 console.log("Connected!");
           	 ready();
            };
            
            ws.onclose = function() { 
           	 console.log("Connection is closed...");
            };	 
            

            window.onbeforeunload = function() {
            	ws.close();
            };
        }else {
        	console.log("no websocket support!");
        }
				
	}
	
	this.joinRoom = function(roomName){
		var data = {
				id:"joinRoom",
				name:roomName
		};
		ws.send(JSON.stringify(data));
	}
	
	
	this.pc = null;

	var configuration = {
		iceServers: [{urls: "stun:stun.l.google.com:19302"},{urls: "stun:23.21.150.121"}]
	}

	var local_constraints = { "audio": true, "video": true };
	
	function logError(err){
		console.log(err);
	}
	
	this.createPeerConnection = function(video){
		Kurento.pc = new RTCPeerConnection(configuration);
		
		Kurento.pc.onicecandidate = function(event) {
		    if (event.candidate) {
		    	console.log(event);
		    }
		}		
		
		function gotDescription(desc){	
			console.log(desc);
			Kurento.pc.setLocalDescription(desc);			
		}
		    
		navigator.getUserMedia(local_constraints, function (stream) {
			video.src = URL.createObjectURL(stream);
			Kurento.pc.addStream(stream);			
			Kurento.pc.createOffer(gotDescription, logError);
		}, logError);


	};
	
	
	return this;
})();