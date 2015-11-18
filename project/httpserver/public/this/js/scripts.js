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

function parseQuery(qstr) {
	var query = {};
	var a = qstr.substr(1).split('&');
	for (var i = 0; i < a.length; i++) {
		var b = a[i].split('=');
		query[decodeURIComponent(b[0])] = decodeURIComponent(b[1] || '');
	}
	return query;
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