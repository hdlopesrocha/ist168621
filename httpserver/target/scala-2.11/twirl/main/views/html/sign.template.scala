
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object sign_Scope0 {
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

class sign extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template0[play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/():play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.4*/("""

"""),_display_(/*3.2*/template(1)/*3.13*/{_display_(Seq[Any](format.raw/*3.14*/("""
"""),format.raw/*4.1*/("""<div class="container-fluid">
	<div class="row">
		<div class="container" data-spy="scroll" data-target="#sidebar-nav">

			<h3>Login</h3>
			<form id="login-form">
				Email:<input type="text" name="email" /><br> Password:<input
					type="password" name="password"><br> <input
					type="submit" />
			</form>
			<h3>Register</h3>
			<form id="register-form">
				Email:<input type="text" name="email" /> <br> Name:<input
					type="text" name="name" /> <br> Password:<input
					type="password" name="password" /><br> Confirm Password:<input
					type="password" name="password2" /><br> Photo:<input
					type="file" name="photo" /> <br> <input type="submit" />
			</form>
		</div>
	</div>
</div>

<script>
		$( document ).ready(function() """),format.raw/*27.34*/("""{"""),format.raw/*27.35*/("""
			"""),format.raw/*28.4*/("""$( "#login-form" ).submit(function( event ) """),format.raw/*28.48*/("""{"""),format.raw/*28.49*/("""
				"""),format.raw/*29.5*/("""var email = this.elements["email"].value;
				var password = this.elements["password"].value;
				var success = function()"""),format.raw/*31.29*/("""{"""),format.raw/*31.30*/("""
					"""),format.raw/*32.6*/("""document.location = "/";
				"""),format.raw/*33.5*/("""}"""),format.raw/*33.6*/(""" 
				"""),format.raw/*34.5*/("""var error = function()"""),format.raw/*34.27*/("""{"""),format.raw/*34.28*/("""
					"""),format.raw/*35.6*/("""alert("fail");
				"""),format.raw/*36.5*/("""}"""),format.raw/*36.6*/(""" 
				"""),format.raw/*37.5*/("""Signaling.login(email,password,success,error);
				event.preventDefault();
			"""),format.raw/*39.4*/("""}"""),format.raw/*39.5*/(""");
			
			$( "#register-form" ).submit(function( event ) """),format.raw/*41.51*/("""{"""),format.raw/*41.52*/("""					
				"""),format.raw/*42.5*/("""//grab all form data  
				var success = function()"""),format.raw/*43.29*/("""{"""),format.raw/*43.30*/("""
					"""),format.raw/*44.6*/("""alert("success");
				"""),format.raw/*45.5*/("""}"""),format.raw/*45.6*/(""" 
				"""),format.raw/*46.5*/("""var error = function()"""),format.raw/*46.27*/("""{"""),format.raw/*46.28*/("""
					"""),format.raw/*47.6*/("""alert("fail");
				"""),format.raw/*48.5*/("""}"""),format.raw/*48.6*/(""" 

				
				
				"""),format.raw/*52.5*/("""var email = this.elements["email"].value;
				var password1 = this.elements["password"].value;
				var password2 = this.elements["password2"].value;
				var photo = this.elements["photo"].files[0];
				var name = this.elements["name"].value;
				var formData = new FormData();
				formData.append("name",name);
				formData.append("photo",photo);
				
				Signaling.register(email,password1,password2,formData,success,error);
				event.preventDefault();

			"""),format.raw/*64.4*/("""}"""),format.raw/*64.5*/(""");
		"""),format.raw/*65.3*/("""}"""),format.raw/*65.4*/(""");

		
	</script>
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
object sign extends sign_Scope0.sign
              /*
                  -- GENERATED --
                  DATE: Thu Oct 15 16:31:02 WEST 2015
                  SOURCE: /home/hdlopesrocha/ist168621/httpserver/app/views/sign.scala.html
                  HASH: 72c9049260c1e1b918ec7f42184c3f78cd182095
                  MATRIX: 736->1|832->3|860->6|879->17|917->18|944->19|1715->762|1744->763|1775->767|1847->811|1876->812|1908->817|2058->939|2087->940|2120->946|2176->975|2204->976|2237->982|2287->1004|2316->1005|2349->1011|2395->1030|2423->1031|2456->1037|2561->1115|2589->1116|2674->1173|2703->1174|2740->1184|2819->1235|2848->1236|2881->1242|2930->1264|2958->1265|2991->1271|3041->1293|3070->1294|3103->1300|3149->1319|3177->1320|3221->1337|3706->1795|3734->1796|3766->1801|3794->1802
                  LINES: 27->1|32->1|34->3|34->3|34->3|35->4|58->27|58->27|59->28|59->28|59->28|60->29|62->31|62->31|63->32|64->33|64->33|65->34|65->34|65->34|66->35|67->36|67->36|68->37|70->39|70->39|72->41|72->41|73->42|74->43|74->43|75->44|76->45|76->45|77->46|77->46|77->46|78->47|79->48|79->48|83->52|95->64|95->64|96->65|96->65
                  -- GENERATED --
              */
          