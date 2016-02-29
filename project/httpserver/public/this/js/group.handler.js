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
        participants[userId] = true;
        var del_button = "<i style='color:red' onclick='removeMemberConfirmation(\""+ userId+ "\",\""+name+"\")' class='fa fa-times'></i>";
        var onclick = 'onclick=\'selectUser("'+userId+'")\'';
        var style='style="background-image:url('+photo+');height:40px;width:40px" class="pull-left media-object circular"';
        var audio ='<i style="top:-12px;left:-24px;position:relative" id="mic'+userId+'" class="fa fa-microphone text-off"></i>';

        $("#videoMembers").append(
            '<div id="vidMember'+userId+'" '+ onclick+'>'+
            '<a '+style+'>'+audio+'</a>'+
            '<div class="media-body"><h5>'+name+' '+del_button+'</h5><small class="text-muted">Active From 3 hours</small></div></div>'
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
        timeline.fit({
            animation : false
        });
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

HyperWebSocket.setOnTimeTagArrivedHandler(function(id, time, title, content) {
    console.log(id, time, title, content);
    timeline.loadTag(id, time, title, content);
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
    $("#mainVideoContent div").not('.fixedLayer').remove();
    console.log(content);
    localContent = content.sort(function(a, b) {
        return new Date(a.time).getTime() - new Date(b.time).getTime();
    });
    renderContent();
});

HyperWebSocket.setOnLookAtTimeRequestHandler(function(time) {
    console.log("setTime",time);
    timeline.setTime(time);
});

HyperWebSocket.setOnUserRemovedHandler(function (uid) {
    $("#vidMember"+uid).remove();
    participants[uid]=false;
    if(uid=="@userId"){
        document.location = "/";
    }
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