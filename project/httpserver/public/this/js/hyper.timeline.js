

var HyperTimeline = new (function() {



	
	this.create = function(divId, historic, realtime) {
		
		
		
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
	      		add:false, 	
	      		remove:true,
	      		updateGroup:false, 
	      		updateTime:true 
	      	}
	     // clickToUse: true
	    };

	    var groups = [
	          {
                id: 'tag',
                content: 'Tags',
                className:'tagsGroup'
              },
              {
                id: 'rec',
                content: 'Recordings',
                className:'recsGroup'
              }
        ];

		// TIMELINE
		var items = new vis.DataSet({
			type : {
				start : 'ISODate',
				end : 'ISODate'
			}
		});

	    
	    
	    var timeline =  new vis.Timeline(main, items,groups, options);
	    timeline.real_time = true;
	    timeline.hyper_offset = 0;
		timeline.addCustomTime(new Date(),"time");
	  
		timeline.on('rangechange', function(properties){
    		var start = properties.start;
	    	var end = properties.end;
	    	var avg = new Date((start.getTime()+end.getTime())/2);
	    	var customTime = this.getCustomTime("time");
	    	//this.options.editable.add
	    	
	    	this.setCustomTime(avg,"time");
	    	$(timeline.customTimes[0].bar).css("pointer-events: none;");
	    //	console.log(timeline.customTimes[0]);
	    	//timeline.customTimes[0].options.editable=false;
    	
	    });
		timeline.items = items;
		
		
		timeline.setHistoric = function(date){
			var date = new Date(date);
			
			this.hyper_offset = new Date().getTime() -date.getTime();
			this.moveTo(date,{animation: {duration: 500,easingFunction: "linear"}});
			this.real_time = false ;
			historic(this.hyper_offset);

		}
		
		timeline.setRealtime = function(){
			var wasRealtime = this.real_time;
			this.real_time = true;
			this.moveTo(new Date());
			this.hyper_offset = 0;
	    	
	    	if(!wasRealtime){
				realtime();
	    	}
		}
		
	    timeline.on('rangechanged', function(properties){
		    
	    	if(properties.byUser){
		    	var start = properties.start;
		    	var end = properties.end;
		    	var avg = (start.getTime()+end.getTime())/2;
		    	this.hyper_offset = new Date().getTime() - avg;
				if (this.hyper_offset < 0){
					this.setRealtime();
				}
				else {
					this.real_time = false ;
					historic(this.hyper_offset);
				}
	    	}
	    });
	    
	    timeline.removeRecord = function(id){
			items.remove(id);
		}
		
	    timeline.recordSet = {};
	    
		timeline.setRecord = function(id,start,end){
			var inter = timeline.recordSet[id];

			if (inter) {
				timeline.removeRecord(id);
			}
			
			
			items.add({
				id : id,
				group : 'rec',
				content : "&nbsp;",
				editable : false,
				selectable : false,
				className : 'danger',
				start : start,
				end : end
			});
			timeline.recordSet[id] = true;
		}
		
		
		timeline.getRecord = function(id){
			return items.get(id);
		}
	    
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
	    
		timeline.loadTag= function(id,time, title,content){
			var start = new Date(time);
			this.items.add({
				id : id,
				content : title,
				start:start,
				//start : new Date(start+3*len/8),
				//end :  new Date(start+5*len/8),			
	 	      //  selectable:true,
		      //  editable:true,
				className: 'info',
				group:'tag'
			});
		}

	    
	    timeline.addTag= function(id, content){
	    	var start = (this.range.start +this.range.end)/2;
	    	//var end = this.range.end;
	    	//var len = end - start;
	    	
	    	this.items.add({
				id : id,
				content : content,
				start:start,
				//start : new Date(start+3*len/8),
				//end :  new Date(start+5*len/8),			
	 	        selectable:true,
		        editable:true,
				className: 'default',
				group:'tag'
			});
	    }
	    
	    timeline.removeTag= function(id){
			this.items.remove(id);

	    }
	    
	    
	    timeline.getMyTime = function(){
	    	return timeline.getCustomTime("time");
	    }

		followerWorker(timeline);
	    return timeline;
	}
	
	
	
	return this;
})();







