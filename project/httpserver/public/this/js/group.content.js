var localContent = [];
var moreContent = true;
var contentTimer = null;

function clearContent(){
    $("#contentText").val("");
    $("#previewContent").html("");

    $(".selectedDelete").each(function(index){
        HyperWebSocket.deleteContent($(this).attr("cid"));
    });
}

function createContent(){
    var start = $("#contentStart").data("DateTimePicker").date().toDate();
    var duration = $("#contentDuration").data("DateTimePicker").date().toDate();
    var dur = duration.getSeconds() + duration.getMinutes()*60 + duration.getHours()*3600;
    console.log("CC",start,dur);
    var end = new Date(start.getTime()+dur*1000);
    var content = getContent();
    if(content){
        HyperWebSocket.createContent(start.toISOString(),end.toISOString(),content);
        toastr.remove();
        toastr.success('Content Editor!', 'Saved content successfully!');
    }
    clearContent();
}

function getContent(){
    var content = $("#contentText").val();
    if(!content || content.length==0){
        return null;
    }
    if($("#contentCaption").is(':checked')){
        content = '<div class="caption"><span>'+content+'</span></div>';
    }
    return content;
}

function previewContent(){
    $("#previewContent").html(getContent());
}

function addContent(key, content) {
    $("#dynamicContent").append('<div id="content'+ key + '">'+content+'</div>');
    $("#content"+key).click(function(){
        if($(this).attr("selected")){
            $(this).attr("selected",false);
            $(this).attr("class","");
        }else {
            $(this).attr("selected",true);
            $(this).attr("class","selectedDelete");
            $(this).attr("cid",key);
        }
    });
}

function removeContent(key) {
    $("#content" + key).remove();
    delete localContent[key];
}

function renderContent() {
    var currentTime = timeline.getCurrentTime() - timeline.hyper_offset;
    while (localContent.length != 0) {
        var entry = localContent[0];
        var time = new Date(entry.time);
        if (time.getTime() <= currentTime) {
            localContent.shift();
            if (entry.type == "start") {
                addContent(entry.id, entry.content);
            } else if (entry.type == "end") {
                removeContent(entry.id);
            }
            console.log("render",entry,localContent);
        } else {
            break;
        }
    }
    var hasStarts = false;
    for ( var i in localContent) {
        var entry = localContent[i];
        if (entry.type == "start") {
            hasStarts = true;
            break;
        }
    }
    if (localContent.length == 0) {
        // no content, keep calm
        return;
    }
    if (!hasStarts && moreContent) {
        // all events started, check if others may start
        HyperWebSocket.getContent();
    } else {
        // wait for next event
        scheduleRender(currentTime);
    }
}

function scheduleRender(currentTime){
    if(timeline.timeRunning){
        var time = new Date(localContent[0].time);
        if(contentTimer!=null){
            clearTimeout(contentTimer);
        }
        contentTimer = setTimeout(function() {
            contentTimer = null;
            renderContent();
        }, time.getTime() - currentTime);
    }
}

function generateQRCode(){
    $("#qrCode").html("");
    $("#qrCode").qrcode({
        "size": 800,
        "color": "#3a3",
        "text": getContent()
    });
    $("#qrCodeModal").modal("show");
}