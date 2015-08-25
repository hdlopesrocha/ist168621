var time_offset = 0;

function createTimeline(items,divId, current) {
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
    

	followerWorker(timeline,current);
    return timeline;
}


function followerWorker(timeline,current){
	var now = new Date().getTime()-time_offset;
	var nowTime = new Date(now);
	current(nowTime);
	
	timeline.moveTo(nowTime,{animation: {duration: 1000,easingFunction: "linear"}});
	setTimeout(function(){followerWorker(timeline,current);},1000);
}

