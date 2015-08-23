

function createTimeline(items,divId, selected) {
    var main = document.getElementById(divId);
    var options = {
        stack: false,
        align: 'center',
        showCurrentTime:false,
       // editable:true,
     // editable: true,
     // clickToUse: true
    };

    var timeline =  new vis.Timeline(main, items, options);
	timeline.addCustomTime(new Date(),"time");
    
    timeline.on('rangechange', function(properties){
    	var start = properties.start;
    	var end = properties.end;
    	var avg = new Date((start.getTime()+end.getTime())/2);

    	timeline.removeCustomTime("time");
    	timeline.addCustomTime(avg,"time");
    	timeline.setCurrentTime(avg);
    });
    
    timeline.on('select', function (properties) {
    	var sel = properties.items[0];
	    if(sel!=undefined){
	    	selected(sel);
	    }
    });    

	followerWorker(timeline);
    return timeline;
}

function followerWorker(timeline){
	var now = timeline.getCurrentTime();
	timeline.moveTo(now);
	setTimeout(function(){followerWorker(timeline);},1000);
}

