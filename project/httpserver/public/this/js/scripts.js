
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

function dateStr(date) {
    var curr_year = date.getFullYear();

    var curr_month = date.getMonth() + 1; //Months are zero based
    if (curr_month < 10)
        curr_month = "0" + curr_month;

    var curr_date = date.getDate();
    if (curr_date < 10)
        curr_date = "0" + curr_date;

    var curr_hour = date.getHours();
    if (curr_hour < 10)
        curr_hour = "0" + curr_hour;

    var curr_min = date.getMinutes();
    if (curr_min < 10)
        curr_min = "0" + curr_min;

    var curr_sec = date.getSeconds();
    if (curr_sec < 10)
        curr_sec = "0" + curr_sec;

    return curr_date + "/" + curr_month + "/" + curr_year + " " + curr_hour + ":" + curr_min + ":" + curr_sec;

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


