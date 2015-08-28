

var HyperTimeline = new (function() {

	function followerWorker(timeline){
		var now = new Date().getTime()-timeline.hyper_offset+1000; // plus 1000 because it's the duration of the animation
		var nowTime = new Date(now);
		
		timeline.moveTo(nowTime,{animation: {duration: 1000,easingFunction: "linear"}});
		setTimeout(function(){followerWorker(timeline);},1000);
	}
	
	this.real_time = true;

	
	this.create = function(items,divId, current) {
	    var main = document.getElementById(divId);
	    var options = {
	        stack: false,
	        align: 'center',
	        showCurrentTime:true,
	        selectable:false
	       // editable:true,
	     // editable: true,
	     // clickToUse: true
	    };

	    var timeline =  new vis.Timeline(main, items, options);
	    timeline.hyper_offset = 0;
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
		    	timeline.hyper_offset = new Date().getTime() - avg;
				if (timeline.hyper_offset < 0){
					timeline.setRealTime();
				}
				else {
					timeline.real_time = false ;
				}
		    	current();
	    	}
	    });
	    
	    
	    timeline.setRealTime = function(){
			timeline.real_time = true;
			timeline.moveTo(new Date());
			timeline.hyper_offset = 0;
	    };
	    
	    timeline.intersects = function(start,end){
	    	var customTime = timeline.getCustomTime("time");
	    	var customMS = customTime.getTime();
	    	var startMS = start.getTime();
	    	var endMS = end.getTime();
	    	
	    	
	    	
	    	var ans = startMS<=customMS && customMS <= endMS;
	    	if(ans){
	    		return customMS - startMS;
	    	}
	    	
	    	return null;
	    };
	    
	    timeline.getMyTime = function(){
	    	return new Date(new Date().getTime()-timeline.hyper_offset);
	    }

		followerWorker(timeline);
	    return timeline;
	}
	
	
	
	return this;
})();







