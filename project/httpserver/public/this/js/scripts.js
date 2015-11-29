function switchTab(classTab, classBtn, number) {
	$(classTab).children().hide();
	$(classTab + " div:nth-child(" + number + ")").show();
	$(classBtn).children().attr("class", "btn btn-default");
	$(classBtn + " button:nth-child(" + number + ")").attr("class",
			"btn btn-primary");
}

function friendlyTime(str) {
	var t = new Date(str);
	return t.toLocaleString("pt-PT");
}

function makeid() {
	var text = "";
	var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	for (var i = 0; i < 10; i++)
		text += possible.charAt(Math.floor(Math.random()
				* possible.length));
	return text;
}

function parseQuery(qstr) {
	var query = {};
	var a = qstr.substr(1).split('&');
	for (var i = 0; i < a.length; i++) {
		var b = a[i].split('=');
		query[decodeURIComponent(b[0])] = decodeURIComponent(b[1] || '');
	}
	return query;
}

function parseTime(time){
	var vec = time.match(/\d+/g); 
	console.log(vec);
	return new Date(vec[2],vec[1]-1,vec[0],vec[3],vec[4],vec[5]);
}

$(document).ready(
	function() {
		$('[data-toggle=offcanvas]').click(function() {
			$('.row-offcanvas').toggleClass('active');
		});
		$('.btn-toggle').click(
				function() {
					$(this).find('.btn').toggleClass('active').toggleClass(
							'btn-default').toggleClass('btn-primary');
				});
	}
);
