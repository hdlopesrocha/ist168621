
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object template2_Scope0 {
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

class template2 extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template0[play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/():play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.4*/("""

"""),format.raw/*3.1*/("""<!DOCTYPE html>
<html lang="en">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta charset="utf-8">
		<title>Holo Theme</title>
		<meta name="generator" content="Bootply" />
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<link href="/assets/css/bootstrap.min.css" rel="stylesheet">
		<!--[if lt IE 9]>
			<script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
		<link href="/assets/css/styles.css" rel="stylesheet">
	</head>
	<body>
<div class="page-container">
  
	<!-- top navbar -->
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    	<div class="navbar-header">
           <button type="button" class="navbar-toggle" data-toggle="offcanvas" data-target=".sidebar-nav">
             <span class="icon-bar"></span>
             <span class="icon-bar"></span>
             <span class="icon-bar"></span>
           </button>
           <a class="navbar-brand" href="#">Bootstrap Holo</a>
    	</div>
    </nav>
      
    <div class="container-fluid">
      <div class="row row-offcanvas row-offcanvas-left">
        
        <!--sidebar-->
        <div class="col-xs-6 col-sm-3 sidebar-offcanvas" id="sidebar" role="navigation">
          <div data-spy="affix" data-offset-top="45" data-offset-bottom="90">
            <ul class="nav" id="sidebar-nav">
              <li><a href="#">Home</a></li>
              <li><a href="#section1">Section 1</a></li>
              <li><a href="#section2">Section 2</a></li>
              <li><a href="#section3">Section 3</a></li>
              <li><a href="#">Holo Theme</a></li>
            </ul>
           </div>
        </div><!--/sidebar-->
  	
        <!--/main-->
        <div class="col-xs-12 col-sm-9" data-spy="scroll" data-target="#sidebar-nav">
          <div class="row">
           	 <div class="col-sm-6">
                <div class="panel panel-default">
                  <div class="panel-heading"><a href="#" class="pull-right">View all</a> <h4>Newest Items</h4></div>
                    <div class="panel-body">
                      <div class="list-group">
                        <a href="#" class="list-group-item active">Active item</a>
                        <a href="#" class="list-group-item">Second item</a>
                        <a href="#" class="list-group-item">Third item</a>
                        <a href="#" class="list-group-item">Another item</a>
                        <a href="#" class="list-group-item">Another item</a>
                        <a href="#" class="list-group-item">Another item</a>
                        <a href="#" class="list-group-item">Another item</a>
                        <a href="#" class="list-group-item">Another item</a>
                        <a href="#" class="list-group-item">Another item</a>
                      </div>
                    </div><!--/panel-body-->
                </div><!--/panel-->
             
                <div class="well"> 
                     <form class="form-horizontal" role="form">
                      <h4>What's New</h4>
                       <div class="form-group" style="padding:14px;">
                        <textarea class="form-control" placeholder="Update your status"></textarea>
                      </div>
                      <button class="btn btn-success pull-right" type="button">Post</button><ul class="list-inline"><li><a href="#"><i class="glyphicon glyphicon-align-left"></i></a></li><li><a href="#"><i class="glyphicon glyphicon-align-center"></i></a></li><li><a href="#"><i class="glyphicon glyphicon-align-right"></i></a></li></ul>
                    </form>
                </div><!--/well-->
             
                <div class="panel panel-default">
                   <div class="panel-heading"><a href="#" class="pull-right">View all</a> <h4>Responsive Design</h4></div>
                    <div class="panel-body">
                      <p><img src="//placehold.it/100" class="img-circle pull-right"> <a href="#">Bootstrap Playground</a></p>
                      <div class="clearfix"></div>
                      <hr>
                      Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis pharetra varius quam sit amet vulputate. 
                      Quisque mauris augue, molestie tincidunt condimentum vitae, gravida a libero. Aenean sit amet felis 
                      dolor, in sagittis nisi. Sed ac orci quis tortor imperdiet venenatis. Duis elementum auctor accumsan. 
                      Aliquam in felis sit amet augue.
                      <hr>
                      <div class="btn-group pull-right btn-toggle"> 
                        <button class="btn btn-default">ON</button>
                        <button class="btn btn-primary active">OFF</button>
                      </div>
                    </div><!--/panel-body-->
                 </div><!--/panel-->
            </div><!--/col-->
            
            <div class="col-sm-6">
                 <div class="well"> 
                     <form class="form">
                      <h4>Sign-up</h4>
                      <div class="input-group text-center">
                      <input type="text" class="form-control input-lg" title="Don't worry. We hate spam, and will not share your email with anyone." placeholder="Enter your email address">
                        <span class="input-group-btn"><button class="btn btn-lg btn-primary" type="button">OK</button></span>
                      </div>
                    </form>
                 </div><!--/well-->
        
                 <div class="panel panel-default">
                   <div class="panel-heading"><a href="#" class="pull-right">View all</a> <h4>People You May Know</h4></div>
                    <div class="panel-body">
                      <p><img src="//placehold.it/100" class="img-circle pull-right"> <a href="#">Bootstrap Playground</a></p>
                      <div class="clearfix"></div>
                      <hr>
                      Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis pharetra varius quam sit amet vulputate. 
                      Quisque mauris augue, molestie tincidunt condimentum vitae, gravida a libero. Aenean sit amet felis 
                      dolor, in sagittis nisi. Sed ac orci quis tortor imperdiet venenatis. Duis elementum auctor accumsan. 
                      Aliquam in felis sit amet augue.
                      <hr>
                      <div class="btn-group pull-right btn-toggle"> 
                        <button class="btn btn-default">ON</button>
                        <button class="btn btn-primary active">OFF</button>
                      </div>
                    </div><!--/panel-body-->
                 </div><!--/panel-->
              
                 <div class="panel panel-default">
                   <div class="panel-heading"><a href="#" class="pull-right">View all</a> <h4>Portlet Heading</h4></div>
                    <div class="panel-body">
                      <ul class="list-group">
                        <li class="list-group-item">
                          <span href="#" class="btn-group btn-toggle pull-right"> 
                        	<button class="btn btn-xs btn-default">On</button>
                        	<button class="btn btn-xs btn-primary active">Off</button>
                       	  </span>  
                          Bootply
                        </li>
                      <li class="list-group-item">
                        <span href="#" class="btn-group btn-toggle pull-right"> 
                        	<button class="btn btn-xs btn-primary active">On</button>
                        	<button class="btn btn-xs btn-default">Off</button>
                       	  </span> Templates</li>
                      <li class="list-group-item">
                        <span href="#" class="btn-group btn-toggle pull-right"> 
                        	<button class="btn btn-xs btn-default">On</button>
                        	<button class="btn btn-xs btn-primary active">Off</button>
                       	  </span> Snippets</li>
                      </ul>
                    </div><!--/panel-body-->
                 </div><!--/panel-->
              </div><!--/col-->
          </div><!--/row-->
          
          <h1 id="section1">Section 1</h1>
  
          <div class="panel panel-default">
          	<div class="panel-heading"><a href="#" class="pull-right">View all</a> <h4>Newest Items</h4></div>
   			<div class="panel-body">
              <div class="list-group">
                <a href="#" class="list-group-item active">Active item</a>
                <a href="#" class="list-group-item">Second item</a>
                <a href="#" class="list-group-item">Third item</a>
              </div>
            </div>
          </div><!--/panel-->
          
          <p>Vestibulum porttitor massa eget pellentesque eleifend. Suspendisse tempor, nisi eu placerat auctor, 
            est erat tempus neque, pellentesque venenatis eros lorem vel quam. Nulla luctus malesuada porttitor. 
            Fusce risus mi, luctus scelerisque hendrerit feugiat, volutpat gravida nisi. Quisque facilisis risus 
            in lacus sagittis malesuada. Suspendisse non purus diam. Nunc commodo felis sit amet tortor 
            adipiscing varius. Fusce commodo nulla quis fermentum hendrerit. Donec vulputate, tellus sed 
            venenatis sodales, purus nibh ullamcorper quam, sit amet tristique justo velit molestie lorem.</p>
    
          <h1 id="section2">Section 2</h1>
          <p>Fusce sollicitudin lacus lacinia mi tincidunt ullamcorper. Aenean velit ipsum, vestibulum nec 
            tincidunt eu, lobortis vitae erat. Nullam ultricies fringilla ultricies. Sed euismod nibh quis 
            tincidunt dapibus. Nulla quam velit, porta sit amet felis eu, auctor fringilla elit. Donec 
            convallis tincidunt nibh, quis pellentesque sapien condimentum a. Phasellus purus dui, rhoncus 
            id suscipit id, ornare et sem. Duis aliquet posuere arcu a ornare. Pellentesque consequat libero 
            id massa accumsan volutpat. Fusce a hendrerit lacus. Nam elementum ac eros eu porttitor. 
            Phasellus enim mi, auctor sit amet luctus a, commodo fermentum arcu. In volutpat scelerisque 
            quam, nec lacinia libero.
          </p>
          <hr>
          <button class="btn btn-primary">View</button> <button class="btn btn-primary">Download</button>
          
          <h1 id="section3">Section 3</h1>
          <p>Aliquam a lacinia orci, iaculis porttitor neque. Nullam cursus dolor tempus mauris posuere, eu 
            scelerisque sem tincidunt. Praesent blandit sapien at sem pulvinar, vel egestas orci varius. 
            Praesent vitae purus at ante aliquet luctus vel quis nibh. Mauris id nulla vitae est lacinia 
            rhoncus a vel justo. Donec iaculis quis sapien vel molestie. Aliquam sed elementum orci. 
            Vestibulum tristique tempor risus et malesuada. Sed eget ligula sed quam placerat dapibus. 
            Integer accumsan ac massa at tempus.</p>
         
          <hr>
  
          <h1 id="section3">Elements</h1>
          <div class="row">
              <div class="col-md-12">
                <div class="alert alert-info alert-dismissable">
                    <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
                    <strong>Heads up!</strong> This alert needs your attention, but it's not super important.
                </div>
              </div>
              <div class="col-sm-6">
                <div class="panel panel-default">
                   <div class="panel-heading"><a href="#" class="pull-right">View all</a> <h4>Buttons &amp; Labels</h4></div>
                    <div class="panel-body">
                      <div class="row">
                        <div class="col-xs-4"><a class="btn btn-default center-block" href="#">Button</a></div>
                        <div class="col-xs-4"><a class="btn btn-primary center-block" href="#">Primary</a></div>
                        <div class="col-xs-4"><a class="btn btn-danger center-block" href="#">Danger</a></div>
                      </div>
                      <br>
                      <div class="row">
                        <div class="col-xs-4"><a class="btn btn-warning center-block" href="#">Warning</a></div>
                        <div class="col-xs-4"><a class="btn btn-info center-block" href="#">Info</a></div>
                        <div class="col-xs-4"><a class="btn btn-success center-block" href="#">Success</a></div>
                      </div>
                      <hr>
                      <div class="btn-group btn-group-sm"><button class="btn btn-default">1</button><button class="btn btn-default">2</button><button class="btn btn-default">3</button></div>              
                      <hr>
                      <div class="row">
                        <div class="col-md-4">
                          <span class="label label-default">Label</span>
                          <span class="label label-success">Success</span>
                        </div>
                        <div class="col-md-4">
                          <span class="label label-warning">Warning</span>  
                          <span class="label label-info">Info</span>
                        </div>
                        <div class="col-md-4">
                          <span class="label label-danger">Danger</span>
                          <span class="label label-primary">Primary</span>
                        </div>
                      </div><!--/row-->
                    </div><!--/panel-body-->
               </div><!--/panel-->
           </div><!--/col-6-->
            
           <div class="col-sm-6">
              <div class="panel panel-default">
                 <div class="panel-heading"><a href="#" class="pull-right">View all</a> <h4>Progress Bars</h4></div>
                  <div class="panel-body">
                    
                    <div class="progress">
                      <div class="progress-bar progress-bar-info" style="width: 20%"></div>
                    </div>
                    <div class="progress">
                      <div class="progress-bar progress-bar-success" style="width: 40%"></div>
                    </div>
                    <div class="progress">
                      <div class="progress-bar progress-bar-warning" style="width: 80%"></div>
                    </div>
                    <div class="progress">
                      <div class="progress-bar progress-bar-danger" style="width: 50%"></div>
                    </div>
                    
                  </div><!--/panel-body-->
              </div><!--/panel-->
          </div><!--/col-6-->
            
          <div class="col-sm-6">
              <div class="panel panel-default">
                 <div class="panel-heading"><a href="#" class="pull-right">View all</a> <h4>Tabs</h4></div>
                  <div class="panel-body">
      
                      <ul class="nav nav-tabs">
                        <li class="active"><a href="#A" data-toggle="tab">Section 1</a></li>
                        <li><a href="#B" data-toggle="tab">Section 2</a></li>
                        <li><a href="#C" data-toggle="tab">Section 3</a></li>
                      </ul>
                      <div class="tabbable">
                        <div class="tab-content">
                          <div class="tab-pane active" id="A">
                            <div class="well well-sm">I'm in Section A.</div>
                          </div>
                          <div class="tab-pane" id="B">
                            <div class="well well-sm">Howdy, I'm in Section B.</div>
                          </div>
                          <div class="tab-pane" id="C">
                            <div class="well well-sm">I've decided that I like wells.</div>
                          </div>
                        </div>
                      </div> <!-- /tabbable -->
                    
                      <div class="col-sm-12 text-center">
                        <ul class="pagination center-block" style="display:inline-block;">
                          <li><a href="#">«</a></li>
                          <li><a href="#">1</a></li>
                          <li><a href="#">2</a></li>
                          <li><a href="#">3</a></li>
                          <li><a href="#">4</a></li>
                          <li><a href="#">5</a></li>
                          <li><a href="#">»</a></li>
                        </ul>
                      </div>
                    
                  </div><!--/panel-body-->
               </div> <!--/panel-->
            </div><!--/col-6-->
          
          	<div class="clearfix"></div>
          
          	<hr>
          	<h4><a href="http://www.bootply.com/ToV8Bzv4GQ">Holo Theme from Bootply</a></h4>
          	<hr>
          
        </div><!--/.col-xs-12-->
      </div><!--/.row-->
    </div>
  </div><!--/.container-->
</div><!--/.page-container-->
  
<footer><!--footer-->
  <div class="container">
      	<div class="row">
          <ul class="list-unstyled text-right">
            <li class="col-sm-4 col-xs-6">
              <a href="#">About</a>
            </li>
            <li class="col-sm-4 col-xs-6">
              <a href="#">Services</a>
            </li>
            <li class="col-sm-4 col-xs-6">
              <a href="#">Studies</a>
            </li>
            <li class="col-sm-4 col-xs-6">
              <a href="#">References</a>
            </li>
            <li class="col-sm-4 col-xs-6">
              <a href="#">Login</a>
            </li>
           <li class="col-sm-4 col-xs-6">
              <a href="#">Press</a>
            </li>
            <li class="col-sm-4 col-xs-6">
              <a href="#">Contact</a>
            </li>
            <li class="col-sm-4 col-xs-6">
              <a href="#">Impressum</a>
            </li>
          </ul>
		</div><!--/row-->
    </div><!--/container-->
</footer>
        
	<!-- script references -->
		<script src="/assets/js/jquery.js"></script>
		<script src="/assets/js/bootstrap.min.js"></script>
		<script src="/assets/js/scripts.js"></script>
	</body>
</html>
"""))
      }
    }
  }

  def render(): play.twirl.api.HtmlFormat.Appendable = apply()

  def f:(() => play.twirl.api.HtmlFormat.Appendable) = () => apply()

  def ref: this.type = this

}


}

/**/
object template2 extends template2_Scope0.template2
              /*
                  -- GENERATED --
                  DATE: Thu Oct 15 16:31:02 WEST 2015
                  SOURCE: /home/hdlopesrocha/ist168621/httpserver/app/views/template2.scala.html
                  HASH: 0fc0e941800ff63edb68cc753b1a699b0963c418
                  MATRIX: 746->1|842->3|870->5
                  LINES: 27->1|32->1|34->3
                  -- GENERATED --
              */
          