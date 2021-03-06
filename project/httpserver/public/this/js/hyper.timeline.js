

var HyperTimeline = new (function() {

    function makeid() {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (var i = 0; i < 10; i++)
            text += possible.charAt(Math.floor(Math.random()
                    * possible.length));
        return text;
    }


	this.create = function(divId, historic, realTime, onCurrentTag, onDrop, onDirty) {
	    var tags = [];
	    var tempTags = {};
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
               tempTags[item.id] = {title:null};
               onDirty();
               callback(item); // send back adjusted item
	      	},
	      	onMove: function(item, callback){
               console.log("onMove", item);
               tempTags[item.id] = {title:item.content,time:item.start};
               onDirty();
	      	}
	     // clickToUse: true
	    };

	    var groups = [
	          {
                id: 'tag',
                content: 'Annotations',
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
        var date = new Date();


        timeline.setWindow(date.getTime()-120*1000, date.getTime()+120*1000, {
            animation: false
        });

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
        timeline.removeTempTags = function(){
           for(var i in tempTags){
                timeline.removeTag(i);
           }
           tempTags = {};
        }

        timeline.iterateTempTags = function(callback){
            console.log("iterateTempTags", tempTags);

            for(var i in tempTags){
                var tag =tempTags[i];
                tag.id = i;
                callback(tag);
            }
        }



		timeline.loadTag= function(id,time, title){
			timeline.removeTag(id);
			var start = new Date(time);
			tags.push({text:title,time:start,id:id});
			this.items.add({
				id : id,
				content : title,
				start:start,
				className: 'info',
				group:'tag'
			});
		}

	    
	    timeline.addTempTag= function(title){
	    	var start = (this.range.start +this.range.end)/2;
	    	//var end = this.range.end;
	    	//var len = end - start;
	    	var id = makeid();
	    	this.items.add({
				id : id,
				content : title,
				start:start,
	 	        selectable:true,
		        editable:true,
				className: 'default',
				group:'tag'
			});


			tempTags[id] = { title : title, time : new Date(start)};
			onDirty();
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







