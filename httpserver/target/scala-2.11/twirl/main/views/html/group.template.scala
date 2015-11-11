
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object group_Scope0 {
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

class group extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template3[String,String,String,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(groupId : String, groupName : String, userId : String):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.57*/(""" """),_display_(/*1.59*/template(1)/*1.70*/{_display_(Seq[Any](format.raw/*1.71*/("""
	"""),format.raw/*2.2*/("""<script type="text/javascript" src="/assets/js/jquery.js"></script>
	<script type="text/javascript" src="/assets/js/adapter.js"></script>
	<script type="text/javascript" src="/assets/js/signaling.js"></script>
	<script type="text/javascript" src="/assets/js/participant.js"></script>
	<script type="text/javascript" src="/assets/js/kurento_main.js"></script>
	<script type="text/javascript" src="/assets/js/webrtc.js"></script>
	<script type="text/javascript" src="/assets/timeline/vis.js"></script>
	<script type="text/javascript" src="/assets/js/hyper_timeline.js"></script>
	<script type="text/javascript" src="/assets/js/bootstrap-list-filter.min.js"></script>
	<link type="text/css" href="/assets/timeline/vis.css" rel="stylesheet" />

	<div class="row row-centered">
		<div class="btn-group col-centered" role="group" id="mybtns">
		  <button type="button" onclick="switchTab('#mytabs','#mybtns',1)" class="btn btn-default"><i class="fa fa-video-camera"></i></button>
		  <button type="button" onclick="switchTab('#mytabs','#mybtns',2)" class="btn btn-default"><i class="fa fa-users"></i></button>
		</div>
	</div>
	<div class="container-fluid">
		<div class="container">	
			<div id="mytabs" style="padding-top: 20px;">
				<div>"""),_display_(/*22.11*/group_video(groupId,userId)),format.raw/*22.38*/("""</div>
				<div>"""),_display_(/*23.11*/group_friends(groupId,userId)),format.raw/*23.40*/("""</div>
			</div>
		</div>
	</div>
	<script>
	

	function friendlyTime(str)"""),format.raw/*30.28*/("""{"""),format.raw/*30.29*/("""
		"""),format.raw/*31.3*/("""var t = new Date(str);
		return t.toLocaleString("pt-PT");
	"""),format.raw/*33.2*/("""}"""),format.raw/*33.3*/("""
	
	
		"""),format.raw/*36.3*/("""$(document).ready(function() """),format.raw/*36.32*/("""{"""),format.raw/*36.33*/("""
			"""),format.raw/*37.4*/("""switchTab("#mytabs","#mybtns",1);
			$("#navbar-list").append("<li><a>"""),_display_(/*38.38*/groupName),format.raw/*38.47*/("""</a></li>");
			
			Kurento.start(""""),_display_(/*40.20*/groupId),format.raw/*40.27*/("""", participantOnline, videoStream, mixedStream, intervalRecorded, messageArrived,tagArrived);
	
			$("#refresh-button").click(function() """),format.raw/*42.42*/("""{"""),format.raw/*42.43*/("""
				"""),format.raw/*43.5*/("""document.location = document.location;
			"""),format.raw/*44.4*/("""}"""),format.raw/*44.5*/(""");
			
			$("#home-button").click(function() """),format.raw/*46.39*/("""{"""),format.raw/*46.40*/("""
				"""),format.raw/*47.5*/("""document.location = "/";
			"""),format.raw/*48.4*/("""}"""),format.raw/*48.5*/(""");
	
			Signaling.getInvite(""""),_display_(/*50.26*/groupId),format.raw/*50.33*/("""",function(token)"""),format.raw/*50.50*/("""{"""),format.raw/*50.51*/("""
				"""),format.raw/*51.5*/("""$("#invite").val($(location).attr('protocol')+"//"+$(location).attr('host')+"/join/"""),_display_(/*51.89*/groupId),format.raw/*51.96*/("""/" + token);	
			"""),format.raw/*52.4*/("""}"""),format.raw/*52.5*/(""");
			
			Signaling.listGroupMembers(""""),_display_(/*54.33*/groupId),format.raw/*54.40*/("""",function(array) """),format.raw/*54.58*/("""{"""),format.raw/*54.59*/("""
				"""),format.raw/*55.5*/("""for ( var i in array) """),format.raw/*55.27*/("""{"""),format.raw/*55.28*/("""
					"""),format.raw/*56.6*/("""var uid = array[i].uid;
					var name = array[i].name;

					var del_button = "<i style='color:red' onclick='del_user_group(\""+ uid+ "\")' class='fa fa-times'></i>";
					var onclick = 'onclick=\'select_user("'+uid+'")\'';
					var style1='style="background-image:url(/photo/'+uid+');height:100px;width:100px"';
					var style2='style="background-image:url(/photo/'+uid+');height:40px;width:40px"';

					$("#membersList").append(
						'<div class="media" '+ onclick+'>'+
						'<a id="vidMember'+uid+'" '+style2+' class="pull-left media-object circular"></a>'+
						'<div class="media-body"><h5>'+name+' | User | '+del_button+'</h5><small class="text-muted">Active From 3 hours</small></div></div>'
					);
					
					$("#videoMembers").append(
						'<div class="media" '+ onclick+'>'+
						'<a id="vidMember'+uid+'" '+style2+' class="pull-left media-object circular circular-offline"></a>'+
						'<div class="media-body"><h5>'+name+' | User</h5><small class="text-muted">Active From 3 hours</small></div></div>'
					);
					
					$("#messengerMembers").append(
						'<div class="media">'+
						'<a id="msgMember'+uid+'" '+style2+' class="pull-left media-object circular circular-offline"></a>'+
						'<div class="media-body"><h5>'+name+' | User</h5><small class="text-muted">Active From 3 hours</small></div></div>'
					);
				"""),format.raw/*81.5*/("""}"""),format.raw/*81.6*/("""
			"""),format.raw/*82.4*/("""}"""),format.raw/*82.5*/(""");
		"""),format.raw/*83.3*/("""}"""),format.raw/*83.4*/(""");	
	</script>
""")))}),format.raw/*85.2*/("""
"""))
      }
    }
  }

  def render(groupId:String,groupName:String,userId:String): play.twirl.api.HtmlFormat.Appendable = apply(groupId,groupName,userId)

  def f:((String,String,String) => play.twirl.api.HtmlFormat.Appendable) = (groupId,groupName,userId) => apply(groupId,groupName,userId)

  def ref: this.type = this

}


}

/**/
object group extends group_Scope0.group
              /*
                  -- GENERATED --
                  DATE: Thu Oct 15 16:31:02 WEST 2015
                  SOURCE: /home/hdlopesrocha/ist168621/httpserver/app/views/group.scala.html
                  HASH: d23dd0d82e7ab738045109142c19bb6ef0469d72
                  MATRIX: 759->1|909->56|937->58|956->69|994->70|1022->72|2286->1309|2334->1336|2378->1353|2428->1382|2530->1456|2559->1457|2589->1460|2676->1520|2704->1521|2738->1528|2795->1557|2824->1558|2855->1562|2953->1633|2983->1642|3046->1678|3074->1685|3239->1822|3268->1823|3300->1828|3369->1870|3397->1871|3470->1916|3499->1917|3531->1922|3586->1950|3614->1951|3671->1981|3699->1988|3744->2005|3773->2006|3805->2011|3916->2095|3944->2102|3988->2119|4016->2120|4082->2159|4110->2166|4156->2184|4185->2185|4217->2190|4267->2212|4296->2213|4329->2219|5696->3559|5724->3560|5755->3564|5783->3565|5815->3570|5843->3571|5889->3587
                  LINES: 27->1|32->1|32->1|32->1|32->1|33->2|53->22|53->22|54->23|54->23|61->30|61->30|62->31|64->33|64->33|67->36|67->36|67->36|68->37|69->38|69->38|71->40|71->40|73->42|73->42|74->43|75->44|75->44|77->46|77->46|78->47|79->48|79->48|81->50|81->50|81->50|81->50|82->51|82->51|82->51|83->52|83->52|85->54|85->54|85->54|85->54|86->55|86->55|86->55|87->56|112->81|112->81|113->82|113->82|114->83|114->83|116->85
                  -- GENERATED --
              */
          