function switchTab(classTab, classBtn, number) {
	$(classTab).children().hide();
	$(classTab + " div:nth-child(" + number + ")").show();
	$(classBtn).children().attr("class", "btn btn-default");
	$(classBtn + " button:nth-child(" + number + ")").attr("class",
			"btn btn-primary");
}

$(document).ready(function() {
	$('[data-toggle=offcanvas]').click(function() {
		$('.row-offcanvas').toggleClass('active');
	});
	
	$('.btn-toggle').click(
			function() {
				$(this).find('.btn').toggleClass('active').toggleClass(
						'btn-default').toggleClass('btn-primary');
			}
	);

});