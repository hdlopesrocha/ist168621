
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object pubsub_Scope0 {
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

class pubsub extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template0[play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/():play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.4*/("""

"""),format.raw/*3.1*/("""<html>
	<head>
		<script type="text/javascript" src="/assets/js/jquery.js"></script>	
		<script type="text/javascript" src="/assets/js/signaling.js"></script>	
	</head>
	<body>
		<button onclick="publish2()" >Publish</button><input type="text" id="keypub"/><input type="text" id="datapub"/>
		<br>
		<button onclick="subscribe2()" >Subscribe</button><input type="text" id="keysub"/>
		
		
		<script>
			function publish2()"""),format.raw/*15.23*/("""{"""),format.raw/*15.24*/("""
				"""),format.raw/*16.5*/("""Signaling.publish($("#keypub").val(),$("#datapub").val(), function()"""),format.raw/*16.73*/("""{"""),format.raw/*16.74*/("""}"""),format.raw/*16.75*/(""",function()"""),format.raw/*16.86*/("""{"""),format.raw/*16.87*/("""}"""),format.raw/*16.88*/(""");	
			"""),format.raw/*17.4*/("""}"""),format.raw/*17.5*/("""
			
			"""),format.raw/*19.4*/("""var ts = 0;
			function subscribe2()"""),format.raw/*20.25*/("""{"""),format.raw/*20.26*/("""
				"""),format.raw/*21.5*/("""Signaling.subscribe($("#keysub").val(),ts,function(result)"""),format.raw/*21.63*/("""{"""),format.raw/*21.64*/("""
					"""),format.raw/*22.6*/("""alert(result);
					ts = JSON.parse(result).ts;
				"""),format.raw/*24.5*/("""}"""),format.raw/*24.6*/(""" """),format.raw/*24.7*/(""");	
			"""),format.raw/*25.4*/("""}"""),format.raw/*25.5*/("""
		"""),format.raw/*26.3*/("""</script>
		
		
	</body>
</html>"""))
      }
    }
  }

  def render(): play.twirl.api.HtmlFormat.Appendable = apply()

  def f:(() => play.twirl.api.HtmlFormat.Appendable) = () => apply()

  def ref: this.type = this

}


}

/**/
object pubsub extends pubsub_Scope0.pubsub
              /*
                  -- GENERATED --
                  DATE: Thu Oct 15 16:31:02 WEST 2015
                  SOURCE: /home/hdlopesrocha/ist168621/httpserver/app/views/pubsub.scala.html
                  HASH: aeb0bfdb345f1234dc6590bde076340d518a12cb
                  MATRIX: 740->1|836->3|864->5|1314->427|1343->428|1375->433|1471->501|1500->502|1529->503|1568->514|1597->515|1626->516|1660->523|1688->524|1723->532|1787->568|1816->569|1848->574|1934->632|1963->633|1996->639|2075->691|2103->692|2131->693|2165->700|2193->701|2223->704
                  LINES: 27->1|32->1|34->3|46->15|46->15|47->16|47->16|47->16|47->16|47->16|47->16|47->16|48->17|48->17|50->19|51->20|51->20|52->21|52->21|52->21|53->22|55->24|55->24|55->24|56->25|56->25|57->26
                  -- GENERATED --
              */
          