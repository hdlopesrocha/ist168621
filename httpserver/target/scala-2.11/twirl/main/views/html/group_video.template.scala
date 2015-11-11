
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object group_video_Scope0 {
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import java.lang._
import java.util._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.data._
import play.api.data.Field
import play.mvc.Http.Context.Implicit._

class group_video extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template2[String,String,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(groupId : String, userId : String):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.37*/("""

"""),format.raw/*3.1*/("""<div class="row">
	<div class="col-sm-12">
		<div class="panel panel-info">
			<div class="panel-heading">
				<h4 class="panel-title">
					<a data-toggle="collapse" href="#collapse3">TIMELINE</a>
				</h4>
			</div>
			<div id="collapse3" class="panel-collapse collapse in">
				<button type="button" class="btn btn-warning" onclick="playStop()"
					id="playStop">
					<i class="fa fa-pause"></i>
				</button>
				<div class="form-inline" style="float: right">
					<div class="form-group">
						<label class="control-label" for="tagContent">Create TAG</label>
						<div class="input-group">
							<input type="text" class="form-control" id="tagTitle"
								onkeydown="if(event.keyCode==13)"""),format.raw/*21.41*/("""{"""),format.raw/*21.42*/("""addTag();"""),format.raw/*21.51*/("""}"""),format.raw/*21.52*/(""""> <span
								class="input-group-addon success" onclick="addTag()"><i
								class="fa fa-plus"></i></span>
						</div>
					</div>
					<button type="button" class="btn btn-primary hidden"
						onclick="saveTags()" id="saveButton">
						<i class="fa fa-floppy-o"></i>
					</button>
				</div>
				<div class="panel panel-default container-full" style="margin-bottom: 0px;">
					<div id="timeline"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="col-md-6">
		<div class="panel panel-info">
			<div class="panel-heading">
				<h4 class="panel-title">
					<a data-toggle="collapse" href="#collapse0">VIDEO</a>
				</h4>
			</div>
			<div id="collapse0" class="panel-collapse collapse in">
				<div class="panel-body">
					<video id='myvideo' autoplay width='480px' height='360px'
						style="margin: 0px auto; display: block;"></video>
					<video id='mymixed' autoplay width='480px' height='360px'
						style="margin: 0px auto; display: block;"></video>
				</div>
			</div>
		</div>
	</div>
	<div class="col-md-6">
		<div class="panel panel-info">
			<div class="panel-heading">
				<h4 class="panel-title">
					<a data-toggle="collapse" href="#collapse2">RECENT CHAT HISTORY</a>
				</h4>
			</div>
			<div id="collapse2" class="panel-collapse collapse in">
				<div class="panel-body"  style="max-height: 336px;overflow: auto;" id="messagesPanel">
					<ul class="media-list" id="messagesList">
					</ul>
				</div>
				<div class="panel-footer">
					<div class="input-group">
						<input type="text" class="form-control"
							placeholder="Enter Message" id="messageText"
							onkeydown="if(event.keyCode==13)"""),format.raw/*72.40*/("""{"""),format.raw/*72.41*/("""sendMessage();"""),format.raw/*72.55*/("""}"""),format.raw/*72.56*/("""" /> <span
							class="input-group-btn">
							<button class="btn btn-info" type="button" id="sendMessageButton"
								onclick="sendMessage()">SEND</button>
						</span>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="col-md-6">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h4 class="panel-title">
					<a data-toggle="collapse" href="#collapse1">USERS</a>
				</h4>
			</div>
			<div id="collapse1" class="panel-collapse collapse in">
				<div class="panel-body">
					<ul class="media-list">
						<li class="media">
							<div class="media-body" id="videoMembers"></div>
						</li>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div class="col-md-6">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h4 class="panel-title">
					<a data-toggle="collapse" href="#collapse1">TAGS</a>
				</h4>
			</div>
			<div id="collapse1" class="panel-collapse collapse in">
				<div class="panel-body">
					<ul class="media-list">
						<li class="media">
						    <div class="form-group">
						        <input id="searchinput" class="form-control" type="search" placeholder="Search..." />
						    </div>
						
							<div class="media-body list-group" id="tagList">
							
							</div>
						</li>
					</ul>
				</div>
			</div>
		</div>
	</div>
</div>


<script>
	var selected_user = """"),_display_(/*128.24*/userId),format.raw/*128.30*/("""";
	var real_time = true;
	var unsaved_tags = [];
	var firstRecording = true;
	var participants = """),format.raw/*132.21*/("""{"""),format.raw/*132.22*/("""}"""),format.raw/*132.23*/(""";
	var timeline = null;
	var current_offset=0;
	var rendered_intervals = """),format.raw/*135.27*/("""{"""),format.raw/*135.28*/("""}"""),format.raw/*135.29*/(""";
	var oldestMsg = null;

	// TIMELINE
	var items = new vis.DataSet("""),format.raw/*139.30*/("""{"""),format.raw/*139.31*/("""
		"""),format.raw/*140.3*/("""type : """),format.raw/*140.10*/("""{"""),format.raw/*140.11*/("""
			"""),format.raw/*141.4*/("""start : 'ISODate',
			end : 'ISODate'
		"""),format.raw/*143.3*/("""}"""),format.raw/*143.4*/("""
	"""),format.raw/*144.2*/("""}"""),format.raw/*144.3*/(""");
	

	
	
	function receiveMore()"""),format.raw/*149.24*/("""{"""),format.raw/*149.25*/("""
		"""),format.raw/*150.3*/("""if(oldestMsg)"""),format.raw/*150.16*/("""{"""),format.raw/*150.17*/("""
			"""),format.raw/*151.4*/("""Kurento.receiveMore(oldestMsg,1);
		"""),format.raw/*152.3*/("""}"""),format.raw/*152.4*/("""
	"""),format.raw/*153.2*/("""}"""),format.raw/*153.3*/("""
	
	"""),format.raw/*155.2*/("""$(document).ready(function() """),format.raw/*155.31*/("""{"""),format.raw/*155.32*/("""
	    """),format.raw/*156.6*/("""$('#tagList').btsListFilter('#searchinput', """),format.raw/*156.50*/("""{"""),format.raw/*156.51*/("""itemChild: 'span'"""),format.raw/*156.68*/("""}"""),format.raw/*156.69*/(""");
		
		$( "#messagesPanel" ).scroll(function() """),format.raw/*158.43*/("""{"""),format.raw/*158.44*/("""
			"""),format.raw/*159.4*/("""if($("#messagesPanel").scrollTop()==0)"""),format.raw/*159.42*/("""{"""),format.raw/*159.43*/("""
				"""),format.raw/*160.5*/("""receiveMore();
				$("#messagesPanel").scrollTop(1);
			"""),format.raw/*162.4*/("""}"""),format.raw/*162.5*/("""
		"""),format.raw/*163.3*/("""}"""),format.raw/*163.4*/(""");
		
		// plugtrade.com - jQuery detect vertical scrollbar function //
		(function($) """),format.raw/*166.16*/("""{"""),format.raw/*166.17*/("""
		    """),format.raw/*167.7*/("""$.fn.hasScrollBar = function() """),format.raw/*167.38*/("""{"""),format.raw/*167.39*/("""
		        """),format.raw/*168.11*/("""var divnode = this.get(0);
		        if(divnode.scrollHeight > divnode.clientHeight)
		            return true;
		    """),format.raw/*171.7*/("""}"""),format.raw/*171.8*/("""
		"""),format.raw/*172.3*/("""}"""),format.raw/*172.4*/(""")(jQuery);
		
		
		
		
		timeline = HyperTimeline.create(items, "timeline",
			function(offset) """),format.raw/*178.21*/("""{"""),format.raw/*178.22*/("""
				"""),format.raw/*179.5*/("""console.log("HISTORIC",offset);
				// current request
				current_offset = offset;
				Kurento.receiveHistoric(selected_user,current_offset);
				real_time = false;
			"""),format.raw/*184.4*/("""}"""),format.raw/*184.5*/(""", 
			function() """),format.raw/*185.15*/("""{"""),format.raw/*185.16*/("""
		
				"""),format.raw/*187.5*/("""console.log("REALTIME");
				// realtime request
				Kurento.receiveRealtime(selected_user);

				real_time = true;
			"""),format.raw/*192.4*/("""}"""),format.raw/*192.5*/("""
		"""),format.raw/*193.3*/(""");
	"""),format.raw/*194.2*/("""}"""),format.raw/*194.3*/(""");
	
	function intervalRecorded(array)"""),format.raw/*196.34*/("""{"""),format.raw/*196.35*/("""
		"""),format.raw/*197.3*/("""console.log("REC",array);
		for (var id in array)"""),format.raw/*198.24*/("""{"""),format.raw/*198.25*/("""
			"""),format.raw/*199.4*/("""var start = array[id][0];
			var end = array[id][1];
			var inter = rendered_intervals[id];
			
			if(inter)"""),format.raw/*203.13*/("""{"""),format.raw/*203.14*/("""
				"""),format.raw/*204.5*/("""items.remove(id);
			"""),format.raw/*205.4*/("""}"""),format.raw/*205.5*/("""
	
			"""),format.raw/*207.4*/("""items.add("""),format.raw/*207.14*/("""{"""),format.raw/*207.15*/("""
				"""),format.raw/*208.5*/("""id : id,
				group:'rec',
				content : "&nbsp;",
		      	editable: false,
		      	selectable: false,
		      	className:'danger',
				start : start,
				end : end
			"""),format.raw/*216.4*/("""}"""),format.raw/*216.5*/(""");
			rendered_intervals[id] = true;
		"""),format.raw/*218.3*/("""}"""),format.raw/*218.4*/("""
		"""),format.raw/*219.3*/("""if (firstRecording) """),format.raw/*219.23*/("""{"""),format.raw/*219.24*/("""
			"""),format.raw/*220.4*/("""firstRecording = false;

			timeline.fit("""),format.raw/*222.17*/("""{"""),format.raw/*222.18*/("""
				"""),format.raw/*223.5*/("""animation : false
			"""),format.raw/*224.4*/("""}"""),format.raw/*224.5*/(""");
		"""),format.raw/*225.3*/("""}"""),format.raw/*225.4*/("""
	"""),format.raw/*226.2*/("""}"""),format.raw/*226.3*/("""
	
	"""),format.raw/*228.2*/("""function videoStream(streamUrl) """),format.raw/*228.34*/("""{"""),format.raw/*228.35*/("""
		"""),format.raw/*229.3*/("""var video = document.getElementById("myvideo");
		video.src = streamUrl;
		return video;
	"""),format.raw/*232.2*/("""}"""),format.raw/*232.3*/("""
	
	"""),format.raw/*234.2*/("""function mixedStream(streamUrl) """),format.raw/*234.34*/("""{"""),format.raw/*234.35*/("""
		"""),format.raw/*235.3*/("""var video = document.getElementById("mymixed");
		video.src = streamUrl;
		return video;
	"""),format.raw/*238.2*/("""}"""),format.raw/*238.3*/("""
	
	"""),format.raw/*240.2*/("""function participantOnline(userId, email) """),format.raw/*240.44*/("""{"""),format.raw/*240.45*/("""
		"""),format.raw/*241.3*/("""if (!participants[userId]) """),format.raw/*241.30*/("""{"""),format.raw/*241.31*/("""
			"""),format.raw/*242.4*/("""$("#msgMember"+userId).attr('class', "pull-left media-object circular circular-online" );				
			$("#vidMember"+userId).attr('class', "pull-left media-object circular circular-online" );				
			participants[userId] = email;
		"""),format.raw/*245.3*/("""}"""),format.raw/*245.4*/("""
	"""),format.raw/*246.2*/("""}"""),format.raw/*246.3*/("""
	
	"""),format.raw/*248.2*/("""function jumpTo(date)"""),format.raw/*248.23*/("""{"""),format.raw/*248.24*/("""
		"""),format.raw/*249.3*/("""timeline.lookAt(date);

		
	"""),format.raw/*252.2*/("""}"""),format.raw/*252.3*/("""
	
	"""),format.raw/*254.2*/("""function tagArrived(id,time,title,content)"""),format.raw/*254.44*/("""{"""),format.raw/*254.45*/("""
		"""),format.raw/*255.3*/("""console.log(id,time,title,content);
		timeline.loadTag(id,time, title,content);
		
		
		$("#tagList").append(  '<div class="list-group-item" onclick="jumpTo(\''+time+'\')"><h5><span>'+title+'</span> <small>'+friendlyTime(time)+'</small></h5></div>')

	"""),format.raw/*261.2*/("""}"""),format.raw/*261.3*/("""

	
	"""),format.raw/*264.2*/("""function messageArrived(userId,time,text, name,mid,seq)"""),format.raw/*264.57*/("""{"""),format.raw/*264.58*/("""
		"""),format.raw/*265.3*/("""var html = '<li class="media"><div class="media-body"><div class="media">' +
		'<a class="pull-left" href="#"><img class="media-object img-circle"  style="height: 40px;" src="/photo/'+userId+'" /></a>' +
		'<div class="media-body">' +text+ '<br /> <small class="text-muted">' +name +' | '+time+'</small>'+
		'<hr /></div></div></div></li>';
				
		if(!oldestMsg || seq<oldestMsg)"""),format.raw/*270.34*/("""{"""),format.raw/*270.35*/("""
			"""),format.raw/*271.4*/("""oldestMsg = seq;
			var oldHeight = $("#messagesList").height();
			$("#messagesList").prepend(html);
			var newHeight = $("#messagesList").height();
			$("div").scrollTop(newHeight-oldHeight);
					
		"""),format.raw/*277.3*/("""}"""),format.raw/*277.4*/(""" """),format.raw/*277.5*/("""else """),format.raw/*277.10*/("""{"""),format.raw/*277.11*/("""
			"""),format.raw/*278.4*/("""$("#messagesList").append(html);
			$("div").scrollTop($("#messagesList").height());
		"""),format.raw/*280.3*/("""}"""),format.raw/*280.4*/("""
		
		
		"""),format.raw/*283.3*/("""if(!$('#messagesPanel').hasScrollBar())"""),format.raw/*283.42*/("""{"""),format.raw/*283.43*/("""
			"""),format.raw/*284.4*/("""receiveMore();
		"""),format.raw/*285.3*/("""}"""),format.raw/*285.4*/("""
		
		
	"""),format.raw/*288.2*/("""}"""),format.raw/*288.3*/("""
	
	"""),format.raw/*290.2*/("""function sendMessage()"""),format.raw/*290.24*/("""{"""),format.raw/*290.25*/("""
		"""),format.raw/*291.3*/("""var text = $("#messageText").val();
		Kurento.sendMessage(text);	
		$("#messageText").val("");

	"""),format.raw/*295.2*/("""}"""),format.raw/*295.3*/("""
	
	"""),format.raw/*297.2*/("""function makeid() """),format.raw/*297.20*/("""{"""),format.raw/*297.21*/("""
		"""),format.raw/*298.3*/("""var text = "";
		var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		for (var i = 0; i < 10; i++)
			text += possible.charAt(Math.floor(Math.random() * possible.length));
		return text;
	"""),format.raw/*303.2*/("""}"""),format.raw/*303.3*/("""

	"""),format.raw/*305.2*/("""function playStop() """),format.raw/*305.22*/("""{"""),format.raw/*305.23*/("""
		"""),format.raw/*306.3*/("""timeline.togglePlayStop(function() """),format.raw/*306.38*/("""{"""),format.raw/*306.39*/("""
			"""),format.raw/*307.4*/("""$("#playStop").html('<i class="fa fa-pause"></i>');
			$("#playStop").attr('class', 'btn btn-warning');
			Kurento.setPlay(true);
		"""),format.raw/*310.3*/("""}"""),format.raw/*310.4*/(""", function() """),format.raw/*310.17*/("""{"""),format.raw/*310.18*/("""
			"""),format.raw/*311.4*/("""$("#playStop").html('<i class="fa fa-play"></i>');
			$("#playStop").attr('class', 'btn btn-success');
			Kurento.setPlay(false);
		"""),format.raw/*314.3*/("""}"""),format.raw/*314.4*/(""");
	"""),format.raw/*315.2*/("""}"""),format.raw/*315.3*/("""

	"""),format.raw/*317.2*/("""function select_user(userId) """),format.raw/*317.31*/("""{"""),format.raw/*317.32*/("""
		"""),format.raw/*318.3*/("""selected_user = userId;

		if (real_time) """),format.raw/*320.18*/("""{"""),format.raw/*320.19*/("""
			"""),format.raw/*321.4*/("""Kurento.receiveRealtime(selected_user);
		"""),format.raw/*322.3*/("""}"""),format.raw/*322.4*/(""" """),format.raw/*322.5*/("""else """),format.raw/*322.10*/("""{"""),format.raw/*322.11*/("""
			"""),format.raw/*323.4*/("""Kurento.receiveHistoric(selected_user, current_offset);
		"""),format.raw/*324.3*/("""}"""),format.raw/*324.4*/("""
	"""),format.raw/*325.2*/("""}"""),format.raw/*325.3*/("""

	"""),format.raw/*327.2*/("""function saveTags() """),format.raw/*327.22*/("""{"""),format.raw/*327.23*/("""
		"""),format.raw/*328.3*/("""console.log(unsaved_tags);
		for ( var i in unsaved_tags) """),format.raw/*329.32*/("""{"""),format.raw/*329.33*/("""
			"""),format.raw/*330.4*/("""var tag = unsaved_tags[i];
			var visTag = items.get(tag.id);
			var time = new Date(visTag.start);
		
			var title = tag.title;
			var content = tag.content;
			Kurento.createTag(time,title,content);
			items.remove(tag.id);
		"""),format.raw/*338.3*/("""}"""),format.raw/*338.4*/("""
		"""),format.raw/*339.3*/("""unsaved_tags = [];
		$("#saveButton").addClass("hidden");
	"""),format.raw/*341.2*/("""}"""),format.raw/*341.3*/("""

	"""),format.raw/*343.2*/("""function addTag() """),format.raw/*343.20*/("""{"""),format.raw/*343.21*/("""
		"""),format.raw/*344.3*/("""var id = makeid();
		var title = $("#tagTitle").val();
		var content = "";
		
		unsaved_tags.push( """),format.raw/*348.22*/("""{"""),format.raw/*348.23*/("""id:id,title:title,content:content"""),format.raw/*348.56*/("""}"""),format.raw/*348.57*/(""");
		timeline.addTag(id, title);
		$("#saveButton").removeClass("hidden");
		
	"""),format.raw/*352.2*/("""}"""),format.raw/*352.3*/("""
"""),format.raw/*353.1*/("""</script>"""))
      }
    }
  }

  def render(groupId:String,userId:String): play.twirl.api.HtmlFormat.Appendable = apply(groupId,userId)

  def f:((String,String) => play.twirl.api.HtmlFormat.Appendable) = (groupId,userId) => apply(groupId,userId)

  def ref: this.type = this

}


}

/**/
object group_video extends group_video_Scope0.group_video
              /*
                  -- GENERATED --
                  DATE: Thu Oct 15 16:31:02 WEST 2015
                  SOURCE: /home/hdlopesrocha/ist168621/httpserver/app/views/group_video.scala.html
                  HASH: 9661b376656c8c1751cb3500b61c412afbad8a76
                  MATRIX: 764->1|894->36|922->38|1650->738|1679->739|1716->748|1745->749|3440->2416|3469->2417|3511->2431|3540->2432|4931->3795|4959->3801|5086->3899|5116->3900|5146->3901|5248->3974|5278->3975|5308->3976|5405->4044|5435->4045|5466->4048|5502->4055|5532->4056|5564->4060|5632->4100|5661->4101|5691->4103|5720->4104|5782->4137|5812->4138|5843->4141|5885->4154|5915->4155|5947->4159|6011->4195|6040->4196|6070->4198|6099->4199|6131->4203|6189->4232|6219->4233|6253->4239|6326->4283|6356->4284|6402->4301|6432->4302|6509->4350|6539->4351|6571->4355|6638->4393|6668->4394|6701->4399|6785->4455|6814->4456|6845->4459|6874->4460|6990->4547|7020->4548|7055->4555|7115->4586|7145->4587|7185->4598|7331->4716|7360->4717|7391->4720|7420->4721|7545->4817|7575->4818|7608->4823|7805->4992|7834->4993|7880->5010|7910->5011|7946->5019|8093->5138|8122->5139|8153->5142|8185->5146|8214->5147|8281->5185|8311->5186|8342->5189|8420->5238|8450->5239|8482->5243|8619->5351|8649->5352|8682->5357|8731->5378|8760->5379|8794->5385|8833->5395|8863->5396|8896->5401|9093->5570|9122->5571|9189->5610|9218->5611|9249->5614|9298->5634|9328->5635|9360->5639|9430->5680|9460->5681|9493->5686|9542->5707|9571->5708|9604->5713|9633->5714|9663->5716|9692->5717|9724->5721|9785->5753|9815->5754|9846->5757|9964->5847|9993->5848|10025->5852|10086->5884|10116->5885|10147->5888|10265->5978|10294->5979|10326->5983|10397->6025|10427->6026|10458->6029|10514->6056|10544->6057|10576->6061|10830->6287|10859->6288|10889->6290|10918->6291|10950->6295|11000->6316|11030->6317|11061->6320|11117->6348|11146->6349|11178->6353|11249->6395|11279->6396|11310->6399|11590->6651|11619->6652|11652->6657|11736->6712|11766->6713|11797->6716|12205->7095|12235->7096|12267->7100|12497->7302|12526->7303|12555->7304|12589->7309|12619->7310|12651->7314|12766->7401|12795->7402|12832->7411|12900->7450|12930->7451|12962->7455|13007->7472|13036->7473|13072->7481|13101->7482|13133->7486|13184->7508|13214->7509|13245->7512|13370->7609|13399->7610|13431->7614|13478->7632|13508->7633|13539->7636|13785->7854|13814->7855|13845->7858|13894->7878|13924->7879|13955->7882|14019->7917|14049->7918|14081->7922|14241->8054|14270->8055|14312->8068|14342->8069|14374->8073|14534->8205|14563->8206|14595->8210|14624->8211|14655->8214|14713->8243|14743->8244|14774->8247|14845->8289|14875->8290|14907->8294|14977->8336|15006->8337|15035->8338|15069->8343|15099->8344|15131->8348|15217->8406|15246->8407|15276->8409|15305->8410|15336->8413|15385->8433|15415->8434|15446->8437|15533->8495|15563->8496|15595->8500|15851->8728|15880->8729|15911->8732|15998->8791|16027->8792|16058->8795|16105->8813|16135->8814|16166->8817|16294->8916|16324->8917|16386->8950|16416->8951|16523->9030|16552->9031|16581->9032
                  LINES: 27->1|32->1|34->3|52->21|52->21|52->21|52->21|103->72|103->72|103->72|103->72|159->128|159->128|163->132|163->132|163->132|166->135|166->135|166->135|170->139|170->139|171->140|171->140|171->140|172->141|174->143|174->143|175->144|175->144|180->149|180->149|181->150|181->150|181->150|182->151|183->152|183->152|184->153|184->153|186->155|186->155|186->155|187->156|187->156|187->156|187->156|187->156|189->158|189->158|190->159|190->159|190->159|191->160|193->162|193->162|194->163|194->163|197->166|197->166|198->167|198->167|198->167|199->168|202->171|202->171|203->172|203->172|209->178|209->178|210->179|215->184|215->184|216->185|216->185|218->187|223->192|223->192|224->193|225->194|225->194|227->196|227->196|228->197|229->198|229->198|230->199|234->203|234->203|235->204|236->205|236->205|238->207|238->207|238->207|239->208|247->216|247->216|249->218|249->218|250->219|250->219|250->219|251->220|253->222|253->222|254->223|255->224|255->224|256->225|256->225|257->226|257->226|259->228|259->228|259->228|260->229|263->232|263->232|265->234|265->234|265->234|266->235|269->238|269->238|271->240|271->240|271->240|272->241|272->241|272->241|273->242|276->245|276->245|277->246|277->246|279->248|279->248|279->248|280->249|283->252|283->252|285->254|285->254|285->254|286->255|292->261|292->261|295->264|295->264|295->264|296->265|301->270|301->270|302->271|308->277|308->277|308->277|308->277|308->277|309->278|311->280|311->280|314->283|314->283|314->283|315->284|316->285|316->285|319->288|319->288|321->290|321->290|321->290|322->291|326->295|326->295|328->297|328->297|328->297|329->298|334->303|334->303|336->305|336->305|336->305|337->306|337->306|337->306|338->307|341->310|341->310|341->310|341->310|342->311|345->314|345->314|346->315|346->315|348->317|348->317|348->317|349->318|351->320|351->320|352->321|353->322|353->322|353->322|353->322|353->322|354->323|355->324|355->324|356->325|356->325|358->327|358->327|358->327|359->328|360->329|360->329|361->330|369->338|369->338|370->339|372->341|372->341|374->343|374->343|374->343|375->344|379->348|379->348|379->348|379->348|383->352|383->352|384->353
                  -- GENERATED --
              */
          