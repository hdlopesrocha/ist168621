
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object index_Scope0 {
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

class index extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template0[play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/():play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.4*/(""" """),_display_(/*1.6*/template(1)/*1.17*/{_display_(Seq[Any](format.raw/*1.18*/("""

"""),format.raw/*3.1*/("""<div class="container-fluid">
	<div class="container">
		<div class="row">
			<div class="col-sm-6">
				<div class="panel panel-default">
					Search:<input type="text" onchange="user_search(this.value)" />
					<h3>Search Result</h3>
					<ul id="search-users"></ul>
				</div>
			</div>
			<div class="col-sm-6">
				<div class="panel panel-default">
					<h3>Friend Requests</h3>
					<ul id="friend-requests"></ul>
					<h3>Friend List</h3>
					<ul id="friend-list"></ul>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-6">
				<div class="panel panel-default">
					<h3>Create Group</h3>
					&nbsp;Name:<input type="text" id="group_name" />
					<button onclick="group_create($('#group_name').val())">Create</button>
					<h3>Group List</h3>
					<ul id="group-list"></ul>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(
			function() """),format.raw/*37.15*/("""{"""),format.raw/*37.16*/("""
				"""),format.raw/*38.5*/("""$("#logout-button").click(function() """),format.raw/*38.42*/("""{"""),format.raw/*38.43*/("""
					"""),format.raw/*39.6*/("""Signaling.logout(function() """),format.raw/*39.34*/("""{"""),format.raw/*39.35*/("""
						"""),format.raw/*40.7*/("""document.location = "/";
					"""),format.raw/*41.6*/("""}"""),format.raw/*41.7*/(""")
				"""),format.raw/*42.5*/("""}"""),format.raw/*42.6*/(""");

				Signaling.listRequests(function(array) """),format.raw/*44.44*/("""{"""),format.raw/*44.45*/("""
					"""),format.raw/*45.6*/("""var html = "";
					for ( var i in array) """),format.raw/*46.28*/("""{"""),format.raw/*46.29*/("""
						"""),format.raw/*47.7*/("""var add_button = "<button onclick='user_add(\""
								+ array[i].uid + "\")'>accept</button>";
						var del_button = "<button onclick='user_del(\""
								+ array[i].uid + "\")'>deny</button>";

						html += "<li>" + array[i].email + add_button
								+ del_button + "</li>";
					"""),format.raw/*54.6*/("""}"""),format.raw/*54.7*/("""
					"""),format.raw/*55.6*/("""$("#friend-requests").html(html);
				"""),format.raw/*56.5*/("""}"""),format.raw/*56.6*/(""");
				Signaling.listRelations(function(array) """),format.raw/*57.45*/("""{"""),format.raw/*57.46*/("""
					"""),format.raw/*58.6*/("""var html = "";
					for ( var i in array) """),format.raw/*59.28*/("""{"""),format.raw/*59.29*/("""
						"""),format.raw/*60.7*/("""var del_button = "<button onclick='user_del(\""
								+ array[i].uid + "\")'>deny</button>";
						html += "<li>" + array[i].email + del_button + "</li>";
					"""),format.raw/*63.6*/("""}"""),format.raw/*63.7*/("""
					"""),format.raw/*64.6*/("""$("#friend-list").html(html);
				"""),format.raw/*65.5*/("""}"""),format.raw/*65.6*/(""");
				Signaling.listGroups(function(array) """),format.raw/*66.42*/("""{"""),format.raw/*66.43*/("""
					"""),format.raw/*67.6*/("""var html = "";
					for ( var i in array) """),format.raw/*68.28*/("""{"""),format.raw/*68.29*/("""
						"""),format.raw/*69.7*/("""html += "<li><a href='/group/"+array[i].id+"'>"
								+ array[i].name + "</a></li>";
					"""),format.raw/*71.6*/("""}"""),format.raw/*71.7*/("""
					"""),format.raw/*72.6*/("""$("#group-list").html(html);
				"""),format.raw/*73.5*/("""}"""),format.raw/*73.6*/(""");

			"""),format.raw/*75.4*/("""}"""),format.raw/*75.5*/(""");

	function user_del(userId) """),format.raw/*77.28*/("""{"""),format.raw/*77.29*/("""
		"""),format.raw/*78.3*/("""var success = function() """),format.raw/*78.28*/("""{"""),format.raw/*78.29*/("""
			"""),format.raw/*79.4*/("""document.location = "/";
		"""),format.raw/*80.3*/("""}"""),format.raw/*80.4*/("""
		"""),format.raw/*81.3*/("""Signaling.rejectRelation(userId, success);
	"""),format.raw/*82.2*/("""}"""),format.raw/*82.3*/("""

	"""),format.raw/*84.2*/("""function group_create(name) """),format.raw/*84.30*/("""{"""),format.raw/*84.31*/("""
		"""),format.raw/*85.3*/("""var success = function() """),format.raw/*85.28*/("""{"""),format.raw/*85.29*/("""
			"""),format.raw/*86.4*/("""document.location = "/";
		"""),format.raw/*87.3*/("""}"""),format.raw/*87.4*/("""
		"""),format.raw/*88.3*/("""Signaling.createGroup(name, success);
	"""),format.raw/*89.2*/("""}"""),format.raw/*89.3*/("""

	"""),format.raw/*91.2*/("""function user_add(userId) """),format.raw/*91.28*/("""{"""),format.raw/*91.29*/("""
		"""),format.raw/*92.3*/("""var success = function() """),format.raw/*92.28*/("""{"""),format.raw/*92.29*/("""
			"""),format.raw/*93.4*/("""document.location = "/";
		"""),format.raw/*94.3*/("""}"""),format.raw/*94.4*/("""
		"""),format.raw/*95.3*/("""Signaling.addRelation(userId, success);
	"""),format.raw/*96.2*/("""}"""),format.raw/*96.3*/("""

	"""),format.raw/*98.2*/("""function user_search(query) """),format.raw/*98.30*/("""{"""),format.raw/*98.31*/("""
		"""),format.raw/*99.3*/("""var result = function(array) """),format.raw/*99.32*/("""{"""),format.raw/*99.33*/("""
			"""),format.raw/*100.4*/("""var html = "";
			for ( var i in array) """),format.raw/*101.26*/("""{"""),format.raw/*101.27*/("""
				"""),format.raw/*102.5*/("""var add_button = "<button onclick='user_add(\"" + array[i].uid
						+ "\")'>add</button>";
				html += "<li>" + array[i].email
						+ (array[i].state == undefined ? add_button : "")
						+ "</li>";
			"""),format.raw/*107.4*/("""}"""),format.raw/*107.5*/("""
			"""),format.raw/*108.4*/("""$("#search-users").html(html);
		"""),format.raw/*109.3*/("""}"""),format.raw/*109.4*/("""
		"""),format.raw/*110.3*/("""Signaling.search(query, result);
	"""),format.raw/*111.2*/("""}"""),format.raw/*111.3*/("""
"""),format.raw/*112.1*/("""</script>
""")))}))
      }
    }
  }

  def render(): play.twirl.api.HtmlFormat.Appendable = apply()

  def f:(() => play.twirl.api.HtmlFormat.Appendable) = () => apply()

  def ref: this.type = this

}


}

/**/
object index extends index_Scope0.index
              /*
                  -- GENERATED --
                  DATE: Thu Oct 15 16:31:02 WEST 2015
                  SOURCE: /home/hdlopesrocha/ist168621/httpserver/app/views/index.scala.html
                  HASH: 96db8ce2d351ebce684640d740967936b6b3a15b
                  MATRIX: 738->1|834->3|861->5|880->16|918->17|946->19|1867->912|1896->913|1928->918|1993->955|2022->956|2055->962|2111->990|2140->991|2174->998|2231->1028|2259->1029|2292->1035|2320->1036|2395->1083|2424->1084|2457->1090|2527->1132|2556->1133|2590->1140|2904->1427|2932->1428|2965->1434|3030->1472|3058->1473|3133->1520|3162->1521|3195->1527|3265->1569|3294->1570|3328->1577|3517->1739|3545->1740|3578->1746|3639->1780|3667->1781|3739->1825|3768->1826|3801->1832|3871->1874|3900->1875|3934->1882|4053->1974|4081->1975|4114->1981|4174->2014|4202->2015|4236->2022|4264->2023|4323->2054|4352->2055|4382->2058|4435->2083|4464->2084|4495->2088|4549->2115|4577->2116|4607->2119|4678->2163|4706->2164|4736->2167|4792->2195|4821->2196|4851->2199|4904->2224|4933->2225|4964->2229|5018->2256|5046->2257|5076->2260|5142->2299|5170->2300|5200->2303|5254->2329|5283->2330|5313->2333|5366->2358|5395->2359|5426->2363|5480->2390|5508->2391|5538->2394|5606->2435|5634->2436|5664->2439|5720->2467|5749->2468|5779->2471|5836->2500|5865->2501|5897->2505|5966->2545|5996->2546|6029->2551|6261->2755|6290->2756|6322->2760|6383->2793|6412->2794|6443->2797|6505->2831|6534->2832|6563->2833
                  LINES: 27->1|32->1|32->1|32->1|32->1|34->3|68->37|68->37|69->38|69->38|69->38|70->39|70->39|70->39|71->40|72->41|72->41|73->42|73->42|75->44|75->44|76->45|77->46|77->46|78->47|85->54|85->54|86->55|87->56|87->56|88->57|88->57|89->58|90->59|90->59|91->60|94->63|94->63|95->64|96->65|96->65|97->66|97->66|98->67|99->68|99->68|100->69|102->71|102->71|103->72|104->73|104->73|106->75|106->75|108->77|108->77|109->78|109->78|109->78|110->79|111->80|111->80|112->81|113->82|113->82|115->84|115->84|115->84|116->85|116->85|116->85|117->86|118->87|118->87|119->88|120->89|120->89|122->91|122->91|122->91|123->92|123->92|123->92|124->93|125->94|125->94|126->95|127->96|127->96|129->98|129->98|129->98|130->99|130->99|130->99|131->100|132->101|132->101|133->102|138->107|138->107|139->108|140->109|140->109|141->110|142->111|142->111|143->112
                  -- GENERATED --
              */
          