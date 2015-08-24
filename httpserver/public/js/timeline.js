var time_offset = 0;

function createTimeline(items,divId, selected) {
    var main = document.getElementById(divId);
    var options = {
        stack: false,
        align: 'center',
        showCurrentTime:true,
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

    	var customTime = timeline.getCustomTime("time");
    	timeline.setCustomTime(avg,"time");
    });
	
    timeline.on('rangechanged', function(properties){
    	if(properties.byUser){
	    	var start = properties.start;
	    	var end = properties.end;
	    	var avg = (start.getTime()+end.getTime())/2;
	    	
	    	time_offset = new Date().getTime() - avg;	    	
    	}
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
	var now = new Date().getTime()-time_offset;
	timeline.moveTo(new Date(now),{animation: {duration: 1000,easingFunction: "linear"}});
	setTimeout(function(){followerWorker(timeline);},1000);
}

