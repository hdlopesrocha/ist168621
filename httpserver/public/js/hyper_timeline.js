

var HyperTimeline = new (function() {

	function followerWorker(timeline){
    	if(timeline.timeRunning){
			var now = new Date().getTime()-timeline.hyper_offset+1000; // plus 1000 because it's the duration of the animation
			var nowTime = new Date(now);			
			timeline.moveTo(nowTime,{animation: {duration: 1000,easingFunction: "linear"}});
    	}
		setTimeout(function(){followerWorker(timeline);},1000);
	}
	
	function onAdd(item, callback){
		item.group = 'tag';
		item.editable = true;
		var date = item.start._i;
		item.start = new Date(date.getTime()-10000);
		item.end =  new Date(date.getTime()+10000);	
		item.className= 'vis-tag';
		callback(item);
	}
	
	this.real_time = true;

	this.create = function(items,divId, current, realtime) {
		
	    var main = document.getElementById(divId);
	    var options = {
	    	stack: false,
	        align: 'center',
	        showCurrentTime:true,
	        selectable:true,
	        onAdd:onAdd,
	        margin: {
	            item: 0,  // distance between items
	            axis: 0   // distance between items and the time axis
	          },
	       // editable:true,
	      	editable: {
	      		add:true, 	
	      		remove:true,
	      		updateGroup:false, 
	      		updateTime:true 
	      	}
	     // clickToUse: true
	    };

	    var groups = [
	          {
                id: 'tag',
                content: 'Tags'
              },
              {
                id: 'rec',
                content: 'Recordings'
              }
        ];
	    
	    var timeline =  new vis.Timeline(main, items,groups, options);
	    timeline.hyper_offset = 0;
		timeline.addCustomTime(new Date(),"time");
	  
		timeline.on('rangechange', function(properties){
    		var start = properties.start;
	    	var end = properties.end;
	    	var avg = new Date((start.getTime()+end.getTime())/2);
	    	var customTime = timeline.getCustomTime("time");
	    	//this.options.editable.add
	    	
	    	timeline.setCustomTime(avg,"time");
	    	//timeline.customTimes[0].bar.contentEditable=false;
	    	//timeline.customTimes[0].options.editable=false;
    	
	    });
		timeline.items = items;
		
	    timeline.on('rangechanged', function(properties){
		    
	    	if(properties.byUser){
		    	var start = properties.start;
		    	var end = properties.end;
		    	var avg = (start.getTime()+end.getTime())/2;
		    	timeline.hyper_offset = new Date().getTime() - avg;
				if (timeline.hyper_offset < 0){
					var wasRealtime = timeline.real_time;
		    		
					timeline.real_time = true;
					timeline.moveTo(new Date());
					timeline.hyper_offset = 0;
			    	
			    	if(!wasRealtime){
						realtime();
			    	}
				}
				else {
					timeline.real_time = false ;
					current(timeline.hyper_offset);
				}
	    	}
	    });
	    
	    
	    
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
	    
	    timeline.timeRunning = true;
	   
	    timeline.togglePlayStop=function(play,stop){
	    	if(timeline.timeRunning){
	    		stop();
	    	}else {
	    		play();
	    	}
	    	
	    	var customTime = timeline.getCustomTime("time");
	    	
	    	timeline.hyper_offset = new Date().getTime() - customTime.getTime();
			timeline.moveTo(customTime,{animation: false});
	    	timeline.timeRunning = !timeline.timeRunning;
	    }
	    
	    timeline.addTag= function(id,date, content){

	    	timeline.items.add({
				id : id,
				content : content,
				start : new Date(date.getTime()-1000),
				end :  new Date(date.getTime()+1000),			
	 	        selectable:true,
		        editable:true,
				className: 'vis-tag',
				group:'tag'
			});
	    }
	    
	    
	    timeline.getMyTime = function(){
	    	return new Date(new Date().getTime()-timeline.hyper_offset);
	    }

		followerWorker(timeline);
	    return timeline;
	}
	
	
	
	return this;
})();







