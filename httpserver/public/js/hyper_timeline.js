

var HyperTimeline = new (function() {


	
	
	
	this.real_time = true;

	
	this.create = function(items,divId, current, realtime) {
		function followerWorker(timeline){
	    	if(timeline.timeRunning){
				var now = new Date().getTime()-timeline.hyper_offset+1000; // plus 1000 because it's the duration of the animation
				var nowTime = new Date(now);			
				timeline.moveTo(nowTime,{animation: {duration: 1000,easingFunction: "linear"}});
	    	}
			setTimeout(function(){followerWorker(timeline);},1000);
		}
	
/*		
		this.onAdd = function (item, callback){
			console.log(this);
			
			item.group = 'tag';
			item.editable = true;
			var date = item.start._i;
			item.start = new Date(date.getTime()-10000);
			item.end =  new Date(date.getTime()+10000);	
			item.className= 'vis-tag';
			callback(item);
		};
*/		
	    var main = document.getElementById(divId);
	    var options = {
	    	stack: false,
	        align: 'center',
	        showCurrentTime:true,
	        selectable:true,
	        //onAdd: this.onAdd,
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
	    	var customTime = this.getCustomTime("time");
	    	//this.options.editable.add
	    	
	    	this.setCustomTime(avg,"time");
	    	//timeline.customTimes[0].bar.contentEditable=false;
	    	//timeline.customTimes[0].options.editable=false;
    	
	    });
		timeline.items = items;
		
	    timeline.on('rangechanged', function(properties){
		    
	    	if(properties.byUser){
		    	var start = properties.start;
		    	var end = properties.end;
		    	var avg = (start.getTime()+end.getTime())/2;
		    	this.hyper_offset = new Date().getTime() - avg;
				if (this.hyper_offset < 0){
					var wasRealtime = this.real_time;
		    		
					this.real_time = true;
					this.moveTo(new Date());
					this.hyper_offset = 0;
			    	
			    	if(!wasRealtime){
						realtime();
			    	}
				}
				else {
					this.real_time = false ;
					current(this.hyper_offset);
				}
	    	}
	    });
	    
	    
	    
	    timeline.intersects = function(start,end){
	    	var customTime = this.getCustomTime("time");
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
	    	if(this.timeRunning){
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
	    	var start = this.range.start;
	    	var end = this.range.end;
	    	var len = end - start;
	    	
	    	this.items.add({
				id : id,
				content : content,
				start : new Date(start+3*len/8),
				end :  new Date(start+5*len/8),			
	 	        selectable:true,
		        editable:true,
				className: 'vis-tag',
				group:'tag'
			});
	    }
	    
	    
	    timeline.getMyTime = function(){
	    	return new Date(new Date().getTime()-this.hyper_offset);
	    }

		followerWorker(timeline);
	    return timeline;
	}
	
	
	
	return this;
})();







