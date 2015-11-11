
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object template_Scope0 {
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

class template extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template2[Integer,Html,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(tab : Integer)(content : Html):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.33*/("""

"""),format.raw/*3.1*/("""<!DOCTYPE html>
<html lang="en">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta charset="utf-8">
		<title>HyperCommunications</title>
		<meta name="generator" content="Bootply" />
		<meta name="viewport"
			content="width=device-width, initial-scale=1, maximum-scale=1">
		<link href="/assets/css/bootstrap.min.css" rel="stylesheet">
		<link href="/assets/css/font-awesome.min.css" rel="stylesheet">
		<link href="/assets/css/main.css" rel="stylesheet">
		<script src="/assets/js/jquery.js"></script>
		<script src="/assets/js/bootstrap.min.js"></script>
		<script src="/assets/js/scripts.js"></script>
		<script type="text/javascript" src="/assets/js/jquery.js"></script>	
		<script type="text/javascript" src="/assets/js/signaling.js"></script>
	

	</head>
	<body>
	<div class="page-container">
		<nav class="navbar navbar-inverse navbar-fixed-top">
			<div class="container-fluid">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed"
						data-toggle="collapse" data-target="#navbar" aria-expanded="false"
						aria-controls="navbar">
						<span class="sr-only">Toggle navigation</span> <span
							class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="#">HyperCommunications</a>
				</div>
				<div id="navbar" class="navbar-collapse collapse">
					<ul class="nav navbar-nav" id="navbar-list">
						<li class="active"><a href="/">Home</a></li>
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li class="dropdown"><a href="#" class="dropdown-toggle"
							data-toggle="dropdown" role="button" aria-haspopup="true"
							aria-expanded="true">Dropdown <span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="#">About</a></li>
								<li><a href="#">Services</a></li>
								<li><a href="#">Studies</a></li>
								<li><a href="#">References</a></li>
								<li><a href="#">Login</a></li>
								<li><a href="#">Press</a></li>
								<li><a href="#">Contact</a></li>
								<li><a href="#">Impressum</a></li>
							</ul></li>
						<li id="logout-button"><a>Logout</a></li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
			<!--/.container-fluid -->
		</nav>

		"""),_display_(/*63.4*/content),format.raw/*63.11*/("""
		
	"""),format.raw/*65.2*/("""</div>
	<script type="text/javascript">
			function switchTab(classTab,classBtn,number)"""),format.raw/*67.48*/("""{"""),format.raw/*67.49*/("""
				"""),format.raw/*68.5*/("""$(classTab).children().hide();
				$(classTab+" div:nth-child("+number+")").show();
				$(classBtn).children().attr("class","btn btn-default");
				$(classBtn+" button:nth-child("+number+")").attr("class","btn btn-primary");
			"""),format.raw/*72.4*/("""}"""),format.raw/*72.5*/("""
			
			
			"""),format.raw/*75.4*/("""$( document ).ready(function() """),format.raw/*75.35*/("""{"""),format.raw/*75.36*/("""
	
				"""),format.raw/*77.5*/("""$("#logout-button").click(function()"""),format.raw/*77.41*/("""{"""),format.raw/*77.42*/("""
					"""),format.raw/*78.6*/("""Signaling.logout(function()"""),format.raw/*78.33*/("""{"""),format.raw/*78.34*/("""
						"""),format.raw/*79.7*/("""document.location = document.location;
					"""),format.raw/*80.6*/("""}"""),format.raw/*80.7*/(""")
				"""),format.raw/*81.5*/("""}"""),format.raw/*81.6*/(""");	
			"""),format.raw/*82.4*/("""}"""),format.raw/*82.5*/(""");		
		</script>	
	</body>
</html>
"""))
      }
    }
  }

  def render(tab:Integer,content:Html): play.twirl.api.HtmlFormat.Appendable = apply(tab)(content)

  def f:((Integer) => (Html) => play.twirl.api.HtmlFormat.Appendable) = (tab) => (content) => apply(tab)(content)

  def ref: this.type = this

}


}

/**/
object template extends template_Scope0.template
              /*
                  -- GENERATED --
                  DATE: Thu Oct 15 16:31:02 WEST 2015
                  SOURCE: /home/hdlopesrocha/ist168621/httpserver/app/views/template.scala.html
                  HASH: 6ed580519e8c22dfaf06768373e3b43edd677b57
                  MATRIX: 757->1|883->32|911->34|3242->2339|3270->2346|3302->2351|3417->2438|3446->2439|3478->2444|3733->2672|3761->2673|3800->2685|3859->2716|3888->2717|3922->2724|3986->2760|4015->2761|4048->2767|4103->2794|4132->2795|4166->2802|4237->2846|4265->2847|4298->2853|4326->2854|4360->2861|4388->2862
                  LINES: 27->1|32->1|34->3|94->63|94->63|96->65|98->67|98->67|99->68|103->72|103->72|106->75|106->75|106->75|108->77|108->77|108->77|109->78|109->78|109->78|110->79|111->80|111->80|112->81|112->81|113->82|113->82
                  -- GENERATED --
              */
          