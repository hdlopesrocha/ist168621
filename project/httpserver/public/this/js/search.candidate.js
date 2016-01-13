

var SearchCandidate = new (function() {
	
	this.source = function(query,process){
		Signaling.search(query, function(array) {
			console.log(array);
			process(array);
		});					
	};
	
	this.displayText = function(item){
		return item.name;
	};
	
	this.highlighter = function(item){
		if(item.type=="models.User"){
			return "<i class='fa fa-user'></i> "  + item.name;
		}else if(item.type=="models.Group"){
			return "<i class='fa fa-group'></i> " + item.name;
		}else {
			return JSON.stringify(item);
		}
	};
	
	this.matcher = function(item){
		return true;
	};
	
	this.updater = function(item){
		console.log(item);
		if(item.type=="models.User"){
			document.location = "/user/"+item.id;
		}else if(item.type=="models.Group"){
			document.location = "/group/"+item.id;
		}
		return item;
	}

	return this;
})();




	
	
	
	
	
		