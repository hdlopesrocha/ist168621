HyperWebSocket.setOnQrCodeEventHandler(function(hash,data){
    console.log(hash,data);

    $("#qrContent").html(data? data : "");
});

HyperWebSocket.setParticipantPresenceHandler(function (info) {
    var userId = info.id;
    var name = info.name;
    var photo = info.photo;
    var online = info.online;
    console.log("*",info);
    if (!participants[userId]) {
        participants[userId] = {id:userId,name:name,photo:photo};
        var del_button = '<button onclick="removeMemberConfirmation(\''+ userId+ '\',\''+name+'\')" type="button" class="btn btn-danger btn-xs"><i class="fa fa-times"></i></button>';


        var style='style="background-image:url('+photo+');height:40px;width:40px" class="pull-left media-object circular"';
        var audio ='<i style="top:-12px;left:-24px;position:relative" id="mic'+userId+'" class="fa fa-microphone text-off"></i>';
        var channel = '<div style="display:inline" id="channel'+userId+'" class="channel"></div>';

        $("#videoMembers").append(
            '<div id="vidMember'+userId+'" '+ onclick+'>'+
            '<a '+style+'>'+audio+'</a>'+
            '<div class="media-body">'+name+'<br>'+del_button+channel+'</div></div>'
        );
    }

    if(online){
        $("#vidMember" + userId).attr('class', "media circular-online");
    }else {
        $("#vidMember" + userId).attr('class', "media circular-offline");
        $("#mic"+userId).attr("class","fa fa-microphone text-off");
    }
});

HyperWebSocket.setOnRemoteVideoAvailableHandler(function (streamUrl) {
    var video = document.getElementById("myvideo");
    video.src = streamUrl;
    return video;
});

HyperWebSocket.setOnNewVideoRecordingArrivedHandler(function (array) {
    console.log("REC", array);
    for ( var id in array) {
        var start = array[id][0];
        var end = array[id][1];
        timeline.setRecord(id, start, end);
    }
    if (firstRecording) {
        firstRecording = false;
        /*timeline.fit({
            animation : false
        });*/
    }
});

HyperWebSocket.setChannelsArrivedHandler(function (array,play) {
    console.log("channels", array);
    var users = {};
    $(".channel").html("");

    for(var i in array){
        var channel = array[i];
        $("#channel"+channel.uid).append('<button onclick=\'selectUser("'+channel.uid+'","'+channel.sid+'")\' type="button" class="btn btn-default btn-xs"><i class="fa fa-video-camera"></i></button>');
    }

});

HyperWebSocket.setOnMessageArrivedHandler(function(userId, time, text, name, mid, seq) {
    console.log("messageArrived",seq,oldestMsg);

    var html = '<li class="media"><div class="media-body"><div class="media">'
            + '<a class="pull-left" href="#"><img class="media-object img-circle"  style="height: 40px;" src="/photo/'+userId+'" /></a>'
            + '<div class="media-body">' + text + '<br />'
            +' <small class="text-muted">' + name + ' | ' + time + '</small>'
            + '<hr /></div></div></div></li>';

    if (oldestMsg==null || seq < oldestMsg) {
        oldestMsg = seq;
        var oldHeight = $("#messagesList").height();
        $("#messagesList").prepend(html);
        var newHeight = $("#messagesList").height();
        $("div").scrollTop(newHeight - oldHeight);

    } else {
        $("#messagesList").append(html);
        $("div").scrollTop($("#messagesList").height());
    }

    if (!$('#messagesPanel').hasScrollBar()) {
        receiveMore();
    }
});

HyperWebSocket.setOnTimeTagArrivedHandler(function(id, time, title) {
    console.log(id, time, title);
    timeline.loadTag(id, time, title);
});

HyperWebSocket.setVoiceDetectedHandler(function(userId,value){
    if(value){
        if(autoSelect){
            selectUser(userId);
        }
        $("#mic"+userId).attr("class","fa fa-microphone text-success");
    }else {
        $("#mic"+userId).attr("class","fa fa-microphone text-off");
    }
});

HyperWebSocket.setOnHyperContentArrivedHandler(function(content,more) {
    moreContent = more;
    $("#dynamicContent div").remove();
    console.log(content);
    localContent = content.sort(function(a, b) {
        return new Date(a.time).getTime() - new Date(b.time).getTime();
    });
    renderContent();
});

HyperWebSocket.setOnLookAtTimeRequestHandler(function(time) {
    console.log("setTime",time);
    timeline.setTime(time);
    HyperWebSocket.getContent();
});

HyperWebSocket.setOnLocalVideoAvailableHandler(function(streamUrl){
    var video = document.getElementById("ownvideo");
    video.src = streamUrl;
});

HyperWebSocket.setServerTimeSynchronizationHandler(function(time){
    timeline.sync(time);
});

HyperWebSocket.setOnOperationTransformationReceivedHandler(function(operation){
    console.log(operation);
    adapter.applyOperation(operation);
});

HyperWebSocket.setOnCoordinationRequestHandler(function(sid){
    var op = ot.TextOperation().insert(adapter.cm.getValue());
    console.log("CoOp",op);
    HyperWebSocket.sendOperation(op.toJSON(),sid);
});

HyperWebSocket.setOnRemovedTagHandler(function(tagId){
    timeline.removeTag(tagId);
});