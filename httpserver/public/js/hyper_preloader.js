

var HyperPreLoader = new (function() {

	var requestBlobs = {};
	var cachedBlobs = {};
	
	this.load = function(path,type){
		if(cachedBlobs[path]){
			return cachedBlobs[path];
		}
		else {
			if(!requestBlobs[path]){
				requestBlobs[path]= true;
				
				var xhr = new XMLHttpRequest();
				xhr.onreadystatechange = function(){
				    if (this.readyState == 4 && this.status == 200){
				        //this.response is what you're looking for
						var blob = new Blob([this.response], {type : type});
						console.log("XXXXX",blob);
						var url = window.URL.createObjectURL(blob);
						cachedBlobs[path] = url;
				        
				    }
				}
				xhr.open('GET', path);
				xhr.responseType = 'blob';
				xhr.send();
				
				
				
				
				
				
			}
		}
		return path;
	}
	
	
	
	return this;
})();







