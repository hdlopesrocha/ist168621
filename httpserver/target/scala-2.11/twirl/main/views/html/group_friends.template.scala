
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object group_friends_Scope0 {
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

class group_friends extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template2[String,String,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(groupId : String, userId : String):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.37*/("""


"""),format.raw/*4.1*/("""<div class="row">

	<div class="col-md-8">
		<div class="panel panel-info">
			<div class="panel-heading">MEMBERS</div>
			<div class="panel-body">
				<ul class="media-list" id="membersList">

					
				</ul>
			</div>
		</div>
	</div>
		<div class="col-md-4">
		<div class="panel panel-info">
			<div class="panel-heading">INVITE</div>
			<div class="panel-body">
				<input type="text" id="invite"/>
				<button id="createInvite" onclick="createInvite2()">Create</button>
				<button id="createInvite" onclick="deleteInvite2()">Delete</button>

			</div>
		</div>
	</div>
	
	<div class="col-md-4">
		<div class="panel panel-info">
			<div class="panel-heading">ADD MEMBERS</div>
			<div class="panel-body">
				<div id="imaginary_container"> 
	                <div class="input-group stylish-input-group">
	                    <input type="text" class="form-control"  placeholder="Search" onchange="candidate_search(this.value)">
	                    <span class="input-group-addon">
	                        <button type="submit">
	                            <span class="fa fa-search"></span>
	                        </button>  
	                    </span>
	                </div>
	            </div>
			
			
				<ul class="media-list" id="candidateList">

					
				</ul>
			</div>
		</div>
	</div>
	

</div>
<style>
.stylish-input-group .input-group-addon"""),format.raw/*56.40*/("""{"""),format.raw/*56.41*/("""
    """),format.raw/*57.5*/("""background: white !important; 
"""),format.raw/*58.1*/("""}"""),format.raw/*58.2*/("""
"""),format.raw/*59.1*/(""".stylish-input-group .form-control"""),format.raw/*59.35*/("""{"""),format.raw/*59.36*/("""
	"""),format.raw/*60.2*/("""border-right:0; 
	box-shadow:0 0 0; 
	border-color:#ccc;
"""),format.raw/*63.1*/("""}"""),format.raw/*63.2*/("""
"""),format.raw/*64.1*/(""".stylish-input-group button"""),format.raw/*64.28*/("""{"""),format.raw/*64.29*/("""
    """),format.raw/*65.5*/("""border:0;
    background:transparent;
"""),format.raw/*67.1*/("""}"""),format.raw/*67.2*/("""
"""),format.raw/*68.1*/("""</style>

<script>

	function createInvite2()"""),format.raw/*72.26*/("""{"""),format.raw/*72.27*/("""
		"""),format.raw/*73.3*/("""Signaling.createInvite(""""),_display_(/*73.28*/groupId),format.raw/*73.35*/("""",function(token)"""),format.raw/*73.52*/("""{"""),format.raw/*73.53*/("""
			"""),format.raw/*74.4*/("""$("#invite").val($(location).attr('protocol')+"//"+$(location).attr('host')+"/join/"""),_display_(/*74.88*/groupId),format.raw/*74.95*/("""/" + token);	

		"""),format.raw/*76.3*/("""}"""),format.raw/*76.4*/(""");
	"""),format.raw/*77.2*/("""}"""),format.raw/*77.3*/("""

	"""),format.raw/*79.2*/("""function deleteInvite2()"""),format.raw/*79.26*/("""{"""),format.raw/*79.27*/("""
		"""),format.raw/*80.3*/("""Signaling.deleteInvite(""""),_display_(/*80.28*/groupId),format.raw/*80.35*/("""",function()"""),format.raw/*80.47*/("""{"""),format.raw/*80.48*/("""
			"""),format.raw/*81.4*/("""$("#invite").val("");	

		"""),format.raw/*83.3*/("""}"""),format.raw/*83.4*/(""");
	"""),format.raw/*84.2*/("""}"""),format.raw/*84.3*/("""
	
	"""),format.raw/*86.2*/("""function del_user_group(uid) """),format.raw/*86.31*/("""{"""),format.raw/*86.32*/("""
		"""),format.raw/*87.3*/("""var success = function() """),format.raw/*87.28*/("""{"""),format.raw/*87.29*/("""
			"""),format.raw/*88.4*/("""document.location = document.location;
		"""),format.raw/*89.3*/("""}"""),format.raw/*89.4*/(""";
		Signaling.removeUserGroup(""""),_display_(/*90.31*/groupId),format.raw/*90.38*/("""", uid, success);
	"""),format.raw/*91.2*/("""}"""),format.raw/*91.3*/("""
	
	"""),format.raw/*93.2*/("""function add_user_group(uid) """),format.raw/*93.31*/("""{"""),format.raw/*93.32*/("""
		"""),format.raw/*94.3*/("""var success = function() """),format.raw/*94.28*/("""{"""),format.raw/*94.29*/("""
			"""),format.raw/*95.4*/("""document.location = document.location;
		"""),format.raw/*96.3*/("""}"""),format.raw/*96.4*/(""";
		Signaling.addUserGroup(""""),_display_(/*97.28*/groupId),format.raw/*97.35*/("""", uid, success);
	"""),format.raw/*98.2*/("""}"""),format.raw/*98.3*/("""
	
	"""),format.raw/*100.2*/("""function candidate_search(query) """),format.raw/*100.35*/("""{"""),format.raw/*100.36*/("""
	
		"""),format.raw/*102.3*/("""var result = function(array) """),format.raw/*102.32*/("""{"""),format.raw/*102.33*/("""
			
			"""),format.raw/*104.4*/("""var html = "";
			for ( var i in array) """),format.raw/*105.26*/("""{"""),format.raw/*105.27*/("""
				"""),format.raw/*106.5*/("""var uid = array[i].uid;
				var name = array[i].name;

				var style2='style="background-image:url(/photo/'+uid+');height:40px;width:40px"';
				var add_button = "<i style='color:green' onclick='add_user_group(\"" + uid	+ "\")' class='fa fa-plus'></i>";

				
				
				html += '<div class="media"><a '+style2+' class="pull-left media-object circular"></a>'+
				'<div class="media-body"><h5>'+name+' | User | '+add_button+'</h5></div></div>';
				
			"""),format.raw/*117.4*/("""}"""),format.raw/*117.5*/("""
			"""),format.raw/*118.4*/("""$("#candidateList").html(html);
			
		
			
			
		"""),format.raw/*123.3*/("""}"""),format.raw/*123.4*/(""";
		Signaling.searchGroupCandidates(""""),_display_(/*124.37*/groupId),format.raw/*124.44*/("""", query, result);
	"""),format.raw/*125.2*/("""}"""),format.raw/*125.3*/("""
"""),format.raw/*126.1*/("""</script>"""))
      }
    }
  }

  def render(groupId:String,userId:String): play.twirl.api.HtmlFormat.Appendable = apply(groupId,userId)

  def f:((String,String) => play.twirl.api.HtmlFormat.Appendable) = (groupId,userId) => apply(groupId,userId)

  def ref: this.type = this

}


}

/**/
object group_friends extends group_friends_Scope0.group_friends
              /*
                  -- GENERATED --
                  DATE: Thu Oct 15 16:31:02 WEST 2015
                  SOURCE: /home/hdlopesrocha/ist168621/httpserver/app/views/group_friends.scala.html
                  HASH: 6d37ff57fba0091a2407a252ac7f7fadc463d899
                  MATRIX: 768->1|898->36|927->39|2320->1404|2349->1405|2381->1410|2439->1441|2467->1442|2495->1443|2557->1477|2586->1478|2615->1480|2699->1537|2727->1538|2755->1539|2810->1566|2839->1567|2871->1572|2936->1610|2964->1611|2992->1612|3065->1657|3094->1658|3124->1661|3176->1686|3204->1693|3249->1710|3278->1711|3309->1715|3420->1799|3448->1806|3492->1823|3520->1824|3551->1828|3579->1829|3609->1832|3661->1856|3690->1857|3720->1860|3772->1885|3800->1892|3840->1904|3869->1905|3900->1909|3953->1935|3981->1936|4012->1940|4040->1941|4071->1945|4128->1974|4157->1975|4187->1978|4240->2003|4269->2004|4300->2008|4368->2049|4396->2050|4455->2082|4483->2089|4529->2108|4557->2109|4588->2113|4645->2142|4674->2143|4704->2146|4757->2171|4786->2172|4817->2176|4885->2217|4913->2218|4969->2247|4997->2254|5043->2273|5071->2274|5103->2278|5165->2311|5195->2312|5228->2317|5286->2346|5316->2347|5352->2355|5421->2395|5451->2396|5484->2401|5963->2852|5992->2853|6024->2857|6101->2906|6130->2907|6196->2945|6225->2952|6273->2972|6302->2973|6331->2974
                  LINES: 27->1|32->1|35->4|87->56|87->56|88->57|89->58|89->58|90->59|90->59|90->59|91->60|94->63|94->63|95->64|95->64|95->64|96->65|98->67|98->67|99->68|103->72|103->72|104->73|104->73|104->73|104->73|104->73|105->74|105->74|105->74|107->76|107->76|108->77|108->77|110->79|110->79|110->79|111->80|111->80|111->80|111->80|111->80|112->81|114->83|114->83|115->84|115->84|117->86|117->86|117->86|118->87|118->87|118->87|119->88|120->89|120->89|121->90|121->90|122->91|122->91|124->93|124->93|124->93|125->94|125->94|125->94|126->95|127->96|127->96|128->97|128->97|129->98|129->98|131->100|131->100|131->100|133->102|133->102|133->102|135->104|136->105|136->105|137->106|148->117|148->117|149->118|154->123|154->123|155->124|155->124|156->125|156->125|157->126
                  -- GENERATED --
              */
          