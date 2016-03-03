

var HyperTimeline = new (function() {

	this.create = function(divId, historic, realTime, onCurrentTag, onDrop,onTagRemoved) {
	    var tags = [];
        var currentTag = null;

		function followerWorker(timeline){
		    var now = timeline.getCurrentTime() -timeline.hyper_offset;
            setCurrentTag(now);
	    	if(timeline.timeRunning){
				var nowTime = new Date(now+1000);			// plus 1000 because it's the duration of the animation
				timeline.moveTo(nowTime,{animation: {duration: 1000,easingFunction: "linear"}});
	    	}
			setTimeout(function(){followerWorker(timeline);},1000);
		}

	    function setCurrentTag(now){
              var minTag =null;
                var minDiff=null;
                for(var i in tags){
                    var tag = tags[i];
                    var dif = now - tag.time.getTime();
                    if(dif > 0 && (minDiff==null || dif < minDiff)){
                        minTag = tag;
                        minDiff=dif;
                    }
                }
                if(currentTag!=minTag){
                    currentTag = minTag;
                    onCurrentTag(minTag);
                }
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
	      	},
	      	onRemove:function (item, callback) {
                if(item.id){
                    onTagRemoved( item.id);
                }
                callback(null); // send back adjusted item
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
		timeline.addCustomTime(timeline.getCurrentTime(),"time");
	  
		timeline.on('rangechange', function(properties){
    		var start = properties.start;
	    	var end = properties.end;
	    	var avg = new Date((start.getTime()+end.getTime())/2);
	    	var customTime = this.getCustomTime("time");
	    	//this.options.editable.add
	    	
	    	this.setCustomTime(avg,"time");
	    	$(this.customTimes[0].bar).css("pointer-events: none;");

    	    //console.log(timeline.customTimes[0]);
	    	//timeline.customTimes[0].options.editable=false;
    	
	    });
		timeline.items = items;
		
		timeline.setTime = function(date){
            var date = new Date(date);

            this.hyper_offset = timeline.getCurrentTime().getTime() -date.getTime();
            this.moveTo(date,{animation: {duration: 500,easingFunction: "linear"}});
            this.real_time = false ;

        }


        timeline.sync = function(serverTime){
            this.setCurrentTime(serverTime);
            console.log("SERVER TIME",serverTime,new Date());
        }


		timeline.setHistoric = function(date){
			timeline.setTime(date);
			historic(this.hyper_offset);

		}
		
		timeline.setRealTime = function(){
			var wasRealTime = this.real_time;
			this.real_time = true;
			this.moveTo(timeline.getCurrentTime());
			this.hyper_offset = 0;
	    	
	    	if(!wasRealTime){
				realTime();
	    	}
		}
		
	    timeline.on('rangechanged', function(properties){
		    
	    	if(properties.byUser){

		    	var start = properties.start;
		    	var end = properties.end;
		    	var avg = (start.getTime()+end.getTime())/2;
		    	this.hyper_offset = timeline.getCurrentTime().getTime() - avg;
				if (this.hyper_offset < 0){
					this.setRealTime();
				}
				else {
					this.real_time = false ;
					historic(this.hyper_offset);
				}
		    	onDrop(avg);
	    	}
	    });
	    
	    timeline.removeRecord = function(id){
			this.items.remove(id);
		}
		
	    timeline.recordSet = {};
	    
		timeline.setRecord = function(id,start,end){
			var inter = this.recordSet[id];

			if (inter) {
				this.removeRecord(id);
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
			this.recordSet[id] = true;
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
	    	
	    	var customTime = this.getCustomTime("time");
	    	
	    	this.hyper_offset = timeline.getCurrentTime().getTime() - customTime.getTime();
	    	this.moveTo(customTime,{animation: false});
	    	this.timeRunning = !this.timeRunning;
	    }

		timeline.loadTag= function(id,time, title,content){
			var start = new Date(time);
			tags.push({text:title,time:start,id:id});
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
	    	return this.getCustomTime("time");
	    }

		followerWorker(timeline);
	    return timeline;
	}

	return this;
})();







