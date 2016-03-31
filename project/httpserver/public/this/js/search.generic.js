

var SearchGeneric = new (function() {
	
	this.source = function(query,process){
        $.get( "/api/search?query="+query, function( data ) {
          	console.log(data);
            process(data.data);
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
    		showUserProfile(item.id);
		}else if(item.type=="models.Group"){
			document.location = "/group/"+item.id;
		}
		return item;
	}

	return this;
})();


var SearchPeople = new (function() {

	this.source = SearchGeneric.source;
	this.displayText = SearchGeneric.displayText;

	this.highlighter = SearchGeneric.highlighter;

	this.matcher = function(item){
		return item.type=="models.User";
	};
	this.updater = SearchGeneric.updater;

	return this;
})();

