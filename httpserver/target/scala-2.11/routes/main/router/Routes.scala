
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/hdlopesrocha/ist168621/httpserver/conf/routes
// @DATE:Sat Oct 03 23:38:43 WEST 2015

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._

import _root_.controllers.Assets.Asset
import _root_.play.libs.F

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:6
  Application_1: controllers.Application,
  // @LINE:14
  Rest_2: controllers.Rest,
  // @LINE:53
  Assets_0: controllers.Assets,
  // @LINE:56
  WSController_3: controllers.WSController,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:6
    Application_1: controllers.Application,
    // @LINE:14
    Rest_2: controllers.Rest,
    // @LINE:53
    Assets_0: controllers.Assets,
    // @LINE:56
    WSController_3: controllers.WSController
  ) = this(errorHandler, Application_1, Rest_2, Assets_0, WSController_3, "/")

  import ReverseRouteContext.empty

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, Application_1, Rest_2, Assets_0, WSController_3, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.Application.index()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """pubsub""", """controllers.Application.pubsub()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """group/$groupId<[^/]+>""", """controllers.Application.group(groupId:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """template""", """controllers.Application.template()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """stream/$path<.+>""", """controllers.Application.stream(path:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """join/$gid<[^/]+>/$token<[^/]+>""", """controllers.Application.join(gid:String, token:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/user/search""", """controllers.Rest.search()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/user/list""", """controllers.Rest.listUsers()"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/user/update""", """controllers.Rest.updateUser()"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/user/password""", """controllers.Rest.changePassword()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/user/get/$user_id<[^/]+>""", """controllers.Rest.getUser(user_id:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/user/login""", """controllers.Rest.login()"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/user/register""", """controllers.Rest.register()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/user/logout""", """controllers.Rest.logout()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/relation/del/$user_id<[^/]+>""", """controllers.Rest.denyRelation(user_id:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/relation/add/$user_id<[^/]+>""", """controllers.Rest.acceptRelation(user_id:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/relation/requests""", """controllers.Rest.listRelationRequests()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/relation/list""", """controllers.Rest.listRelations()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/invite/create/$gid<[^/]+>""", """controllers.Rest.createGroupInvite(gid:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/invite/get/$gid<[^/]+>""", """controllers.Rest.getGroupInvite(gid:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/invite/delete/$gid<[^/]+>""", """controllers.Rest.deleteGroupInvite(gid:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/invite/join/$gid<[^/]+>/$token<[^/]+>""", """controllers.Rest.joinGroupInvite(gid:String, token:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/group/create""", """controllers.Rest.createGroup()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/group/list""", """controllers.Rest.listGroups()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/group/members/$groupId<[^/]+>""", """controllers.Rest.listGroupMembers(groupId:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/group/candidates/$groupId<[^/]+>""", """controllers.Rest.searchGroupCandidates(groupId:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/group/properties/$groupId<[^/]+>""", """controllers.Rest.listGroupMembersProperties(groupId:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/group/add/$gid<[^/]+>/$uid<[^/]+>""", """controllers.Rest.addGroupMember(gid:String, uid:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/group/del/$gid<[^/]+>/$uid<[^/]+>""", """controllers.Rest.removeGroupMember(gid:String, uid:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/group/sdp/$key<[^/]+>""", """controllers.Rest.postSdp(key:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/group/ice/$key<[^/]+>/$token<[^/]+>""", """controllers.Rest.postIceCandidate(key:String, token:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/pubsub/$key<[^/]+>""", """controllers.Rest.publish(key:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/pubsub/$key<[^/]+>/$ts<[^/]+>""", """controllers.Rest.subscribe(key:String, ts:Long)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """photo/$id<.+>""", """controllers.Rest.getPhoto(id:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """file/$id<.+>""", """controllers.Rest.getFile(id:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""", """controllers.Assets.versioned(path:String = "/public", file:Asset)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """ws/room/$name<[^/]+>""", """controllers.WSController.connectToRoom(name:String)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:6
  private[this] lazy val controllers_Application_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val controllers_Application_index0_invoker = createInvoker(
    Application_1.index(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "index",
      Nil,
      "GET",
      """ Home page""",
      this.prefix + """"""
    )
  )

  // @LINE:7
  private[this] lazy val controllers_Application_pubsub1_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("pubsub")))
  )
  private[this] lazy val controllers_Application_pubsub1_invoker = createInvoker(
    Application_1.pubsub(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "pubsub",
      Nil,
      "GET",
      """""",
      this.prefix + """pubsub"""
    )
  )

  // @LINE:8
  private[this] lazy val controllers_Application_group2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("group/"), DynamicPart("groupId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Application_group2_invoker = createInvoker(
    Application_1.group(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "group",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """group/$groupId<[^/]+>"""
    )
  )

  // @LINE:9
  private[this] lazy val controllers_Application_template3_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("template")))
  )
  private[this] lazy val controllers_Application_template3_invoker = createInvoker(
    Application_1.template(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "template",
      Nil,
      "GET",
      """""",
      this.prefix + """template"""
    )
  )

  // @LINE:10
  private[this] lazy val controllers_Application_stream4_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("stream/"), DynamicPart("path", """.+""",false)))
  )
  private[this] lazy val controllers_Application_stream4_invoker = createInvoker(
    Application_1.stream(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "stream",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """stream/$path<.+>"""
    )
  )

  // @LINE:11
  private[this] lazy val controllers_Application_join5_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("join/"), DynamicPart("gid", """[^/]+""",true), StaticPart("/"), DynamicPart("token", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Application_join5_invoker = createInvoker(
    Application_1.join(fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "join",
      Seq(classOf[String], classOf[String]),
      "GET",
      """""",
      this.prefix + """join/$gid<[^/]+>/$token<[^/]+>"""
    )
  )

  // @LINE:14
  private[this] lazy val controllers_Rest_search6_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/user/search")))
  )
  private[this] lazy val controllers_Rest_search6_invoker = createInvoker(
    Rest_2.search(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "search",
      Nil,
      "GET",
      """""",
      this.prefix + """api/user/search"""
    )
  )

  // @LINE:15
  private[this] lazy val controllers_Rest_listUsers7_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/user/list")))
  )
  private[this] lazy val controllers_Rest_listUsers7_invoker = createInvoker(
    Rest_2.listUsers(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "listUsers",
      Nil,
      "GET",
      """""",
      this.prefix + """api/user/list"""
    )
  )

  // @LINE:16
  private[this] lazy val controllers_Rest_updateUser8_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/user/update")))
  )
  private[this] lazy val controllers_Rest_updateUser8_invoker = createInvoker(
    Rest_2.updateUser(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "updateUser",
      Nil,
      "POST",
      """""",
      this.prefix + """api/user/update"""
    )
  )

  // @LINE:17
  private[this] lazy val controllers_Rest_changePassword9_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/user/password")))
  )
  private[this] lazy val controllers_Rest_changePassword9_invoker = createInvoker(
    Rest_2.changePassword(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "changePassword",
      Nil,
      "POST",
      """""",
      this.prefix + """api/user/password"""
    )
  )

  // @LINE:18
  private[this] lazy val controllers_Rest_getUser10_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/user/get/"), DynamicPart("user_id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_getUser10_invoker = createInvoker(
    Rest_2.getUser(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "getUser",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """api/user/get/$user_id<[^/]+>"""
    )
  )

  // @LINE:19
  private[this] lazy val controllers_Rest_login11_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/user/login")))
  )
  private[this] lazy val controllers_Rest_login11_invoker = createInvoker(
    Rest_2.login(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "login",
      Nil,
      "POST",
      """""",
      this.prefix + """api/user/login"""
    )
  )

  // @LINE:20
  private[this] lazy val controllers_Rest_register12_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/user/register")))
  )
  private[this] lazy val controllers_Rest_register12_invoker = createInvoker(
    Rest_2.register(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "register",
      Nil,
      "POST",
      """""",
      this.prefix + """api/user/register"""
    )
  )

  // @LINE:21
  private[this] lazy val controllers_Rest_logout13_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/user/logout")))
  )
  private[this] lazy val controllers_Rest_logout13_invoker = createInvoker(
    Rest_2.logout(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "logout",
      Nil,
      "GET",
      """""",
      this.prefix + """api/user/logout"""
    )
  )

  // @LINE:23
  private[this] lazy val controllers_Rest_denyRelation14_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/relation/del/"), DynamicPart("user_id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_denyRelation14_invoker = createInvoker(
    Rest_2.denyRelation(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "denyRelation",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """api/relation/del/$user_id<[^/]+>"""
    )
  )

  // @LINE:24
  private[this] lazy val controllers_Rest_acceptRelation15_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/relation/add/"), DynamicPart("user_id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_acceptRelation15_invoker = createInvoker(
    Rest_2.acceptRelation(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "acceptRelation",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """api/relation/add/$user_id<[^/]+>"""
    )
  )

  // @LINE:25
  private[this] lazy val controllers_Rest_listRelationRequests16_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/relation/requests")))
  )
  private[this] lazy val controllers_Rest_listRelationRequests16_invoker = createInvoker(
    Rest_2.listRelationRequests(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "listRelationRequests",
      Nil,
      "GET",
      """""",
      this.prefix + """api/relation/requests"""
    )
  )

  // @LINE:26
  private[this] lazy val controllers_Rest_listRelations17_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/relation/list")))
  )
  private[this] lazy val controllers_Rest_listRelations17_invoker = createInvoker(
    Rest_2.listRelations(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "listRelations",
      Nil,
      "GET",
      """""",
      this.prefix + """api/relation/list"""
    )
  )

  // @LINE:29
  private[this] lazy val controllers_Rest_createGroupInvite18_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/invite/create/"), DynamicPart("gid", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_createGroupInvite18_invoker = createInvoker(
    Rest_2.createGroupInvite(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "createGroupInvite",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """api/invite/create/$gid<[^/]+>"""
    )
  )

  // @LINE:30
  private[this] lazy val controllers_Rest_getGroupInvite19_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/invite/get/"), DynamicPart("gid", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_getGroupInvite19_invoker = createInvoker(
    Rest_2.getGroupInvite(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "getGroupInvite",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """api/invite/get/$gid<[^/]+>"""
    )
  )

  // @LINE:31
  private[this] lazy val controllers_Rest_deleteGroupInvite20_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/invite/delete/"), DynamicPart("gid", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_deleteGroupInvite20_invoker = createInvoker(
    Rest_2.deleteGroupInvite(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "deleteGroupInvite",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """api/invite/delete/$gid<[^/]+>"""
    )
  )

  // @LINE:32
  private[this] lazy val controllers_Rest_joinGroupInvite21_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/invite/join/"), DynamicPart("gid", """[^/]+""",true), StaticPart("/"), DynamicPart("token", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_joinGroupInvite21_invoker = createInvoker(
    Rest_2.joinGroupInvite(fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "joinGroupInvite",
      Seq(classOf[String], classOf[String]),
      "GET",
      """""",
      this.prefix + """api/invite/join/$gid<[^/]+>/$token<[^/]+>"""
    )
  )

  // @LINE:35
  private[this] lazy val controllers_Rest_createGroup22_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/group/create")))
  )
  private[this] lazy val controllers_Rest_createGroup22_invoker = createInvoker(
    Rest_2.createGroup(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "createGroup",
      Nil,
      "GET",
      """""",
      this.prefix + """api/group/create"""
    )
  )

  // @LINE:36
  private[this] lazy val controllers_Rest_listGroups23_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/group/list")))
  )
  private[this] lazy val controllers_Rest_listGroups23_invoker = createInvoker(
    Rest_2.listGroups(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "listGroups",
      Nil,
      "GET",
      """""",
      this.prefix + """api/group/list"""
    )
  )

  // @LINE:37
  private[this] lazy val controllers_Rest_listGroupMembers24_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/group/members/"), DynamicPart("groupId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_listGroupMembers24_invoker = createInvoker(
    Rest_2.listGroupMembers(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "listGroupMembers",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """api/group/members/$groupId<[^/]+>"""
    )
  )

  // @LINE:38
  private[this] lazy val controllers_Rest_searchGroupCandidates25_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/group/candidates/"), DynamicPart("groupId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_searchGroupCandidates25_invoker = createInvoker(
    Rest_2.searchGroupCandidates(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "searchGroupCandidates",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """api/group/candidates/$groupId<[^/]+>"""
    )
  )

  // @LINE:39
  private[this] lazy val controllers_Rest_listGroupMembersProperties26_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/group/properties/"), DynamicPart("groupId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_listGroupMembersProperties26_invoker = createInvoker(
    Rest_2.listGroupMembersProperties(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "listGroupMembersProperties",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """api/group/properties/$groupId<[^/]+>"""
    )
  )

  // @LINE:40
  private[this] lazy val controllers_Rest_addGroupMember27_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/group/add/"), DynamicPart("gid", """[^/]+""",true), StaticPart("/"), DynamicPart("uid", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_addGroupMember27_invoker = createInvoker(
    Rest_2.addGroupMember(fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "addGroupMember",
      Seq(classOf[String], classOf[String]),
      "GET",
      """""",
      this.prefix + """api/group/add/$gid<[^/]+>/$uid<[^/]+>"""
    )
  )

  // @LINE:41
  private[this] lazy val controllers_Rest_removeGroupMember28_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/group/del/"), DynamicPart("gid", """[^/]+""",true), StaticPart("/"), DynamicPart("uid", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_removeGroupMember28_invoker = createInvoker(
    Rest_2.removeGroupMember(fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "removeGroupMember",
      Seq(classOf[String], classOf[String]),
      "GET",
      """""",
      this.prefix + """api/group/del/$gid<[^/]+>/$uid<[^/]+>"""
    )
  )

  // @LINE:42
  private[this] lazy val controllers_Rest_postSdp29_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/group/sdp/"), DynamicPart("key", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_postSdp29_invoker = createInvoker(
    Rest_2.postSdp(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "postSdp",
      Seq(classOf[String]),
      "POST",
      """""",
      this.prefix + """api/group/sdp/$key<[^/]+>"""
    )
  )

  // @LINE:43
  private[this] lazy val controllers_Rest_postIceCandidate30_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/group/ice/"), DynamicPart("key", """[^/]+""",true), StaticPart("/"), DynamicPart("token", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_postIceCandidate30_invoker = createInvoker(
    Rest_2.postIceCandidate(fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "postIceCandidate",
      Seq(classOf[String], classOf[String]),
      "POST",
      """""",
      this.prefix + """api/group/ice/$key<[^/]+>/$token<[^/]+>"""
    )
  )

  // @LINE:45
  private[this] lazy val controllers_Rest_publish31_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/pubsub/"), DynamicPart("key", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_publish31_invoker = createInvoker(
    Rest_2.publish(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "publish",
      Seq(classOf[String]),
      "POST",
      """""",
      this.prefix + """api/pubsub/$key<[^/]+>"""
    )
  )

  // @LINE:46
  private[this] lazy val controllers_Rest_subscribe32_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/pubsub/"), DynamicPart("key", """[^/]+""",true), StaticPart("/"), DynamicPart("ts", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Rest_subscribe32_invoker = createInvoker(
    Rest_2.subscribe(fakeValue[String], fakeValue[Long]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "subscribe",
      Seq(classOf[String], classOf[Long]),
      "GET",
      """""",
      this.prefix + """api/pubsub/$key<[^/]+>/$ts<[^/]+>"""
    )
  )

  // @LINE:49
  private[this] lazy val controllers_Rest_getPhoto33_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("photo/"), DynamicPart("id", """.+""",false)))
  )
  private[this] lazy val controllers_Rest_getPhoto33_invoker = createInvoker(
    Rest_2.getPhoto(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "getPhoto",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """photo/$id<.+>"""
    )
  )

  // @LINE:50
  private[this] lazy val controllers_Rest_getFile34_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("file/"), DynamicPart("id", """.+""",false)))
  )
  private[this] lazy val controllers_Rest_getFile34_invoker = createInvoker(
    Rest_2.getFile(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Rest",
      "getFile",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """file/$id<.+>"""
    )
  )

  // @LINE:53
  private[this] lazy val controllers_Assets_versioned35_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("assets/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_versioned35_invoker = createInvoker(
    Assets_0.versioned(fakeValue[String], fakeValue[Asset]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "versioned",
      Seq(classOf[String], classOf[Asset]),
      "GET",
      """ Map static resources from the /public folder to the /assets URL path""",
      this.prefix + """assets/$file<.+>"""
    )
  )

  // @LINE:56
  private[this] lazy val controllers_WSController_connectToRoom36_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("ws/room/"), DynamicPart("name", """[^/]+""",true)))
  )
  private[this] lazy val controllers_WSController_connectToRoom36_invoker = createInvoker(
    WSController_3.connectToRoom(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.WSController",
      "connectToRoom",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """ws/room/$name<[^/]+>"""
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:6
    case controllers_Application_index0_route(params) =>
      call { 
        controllers_Application_index0_invoker.call(Application_1.index())
      }
  
    // @LINE:7
    case controllers_Application_pubsub1_route(params) =>
      call { 
        controllers_Application_pubsub1_invoker.call(Application_1.pubsub())
      }
  
    // @LINE:8
    case controllers_Application_group2_route(params) =>
      call(params.fromPath[String]("groupId", None)) { (groupId) =>
        controllers_Application_group2_invoker.call(Application_1.group(groupId))
      }
  
    // @LINE:9
    case controllers_Application_template3_route(params) =>
      call { 
        controllers_Application_template3_invoker.call(Application_1.template())
      }
  
    // @LINE:10
    case controllers_Application_stream4_route(params) =>
      call(params.fromPath[String]("path", None)) { (path) =>
        controllers_Application_stream4_invoker.call(Application_1.stream(path))
      }
  
    // @LINE:11
    case controllers_Application_join5_route(params) =>
      call(params.fromPath[String]("gid", None), params.fromPath[String]("token", None)) { (gid, token) =>
        controllers_Application_join5_invoker.call(Application_1.join(gid, token))
      }
  
    // @LINE:14
    case controllers_Rest_search6_route(params) =>
      call { 
        controllers_Rest_search6_invoker.call(Rest_2.search())
      }
  
    // @LINE:15
    case controllers_Rest_listUsers7_route(params) =>
      call { 
        controllers_Rest_listUsers7_invoker.call(Rest_2.listUsers())
      }
  
    // @LINE:16
    case controllers_Rest_updateUser8_route(params) =>
      call { 
        controllers_Rest_updateUser8_invoker.call(Rest_2.updateUser())
      }
  
    // @LINE:17
    case controllers_Rest_changePassword9_route(params) =>
      call { 
        controllers_Rest_changePassword9_invoker.call(Rest_2.changePassword())
      }
  
    // @LINE:18
    case controllers_Rest_getUser10_route(params) =>
      call(params.fromPath[String]("user_id", None)) { (user_id) =>
        controllers_Rest_getUser10_invoker.call(Rest_2.getUser(user_id))
      }
  
    // @LINE:19
    case controllers_Rest_login11_route(params) =>
      call { 
        controllers_Rest_login11_invoker.call(Rest_2.login())
      }
  
    // @LINE:20
    case controllers_Rest_register12_route(params) =>
      call { 
        controllers_Rest_register12_invoker.call(Rest_2.register())
      }
  
    // @LINE:21
    case controllers_Rest_logout13_route(params) =>
      call { 
        controllers_Rest_logout13_invoker.call(Rest_2.logout())
      }
  
    // @LINE:23
    case controllers_Rest_denyRelation14_route(params) =>
      call(params.fromPath[String]("user_id", None)) { (user_id) =>
        controllers_Rest_denyRelation14_invoker.call(Rest_2.denyRelation(user_id))
      }
  
    // @LINE:24
    case controllers_Rest_acceptRelation15_route(params) =>
      call(params.fromPath[String]("user_id", None)) { (user_id) =>
        controllers_Rest_acceptRelation15_invoker.call(Rest_2.acceptRelation(user_id))
      }
  
    // @LINE:25
    case controllers_Rest_listRelationRequests16_route(params) =>
      call { 
        controllers_Rest_listRelationRequests16_invoker.call(Rest_2.listRelationRequests())
      }
  
    // @LINE:26
    case controllers_Rest_listRelations17_route(params) =>
      call { 
        controllers_Rest_listRelations17_invoker.call(Rest_2.listRelations())
      }
  
    // @LINE:29
    case controllers_Rest_createGroupInvite18_route(params) =>
      call(params.fromPath[String]("gid", None)) { (gid) =>
        controllers_Rest_createGroupInvite18_invoker.call(Rest_2.createGroupInvite(gid))
      }
  
    // @LINE:30
    case controllers_Rest_getGroupInvite19_route(params) =>
      call(params.fromPath[String]("gid", None)) { (gid) =>
        controllers_Rest_getGroupInvite19_invoker.call(Rest_2.getGroupInvite(gid))
      }
  
    // @LINE:31
    case controllers_Rest_deleteGroupInvite20_route(params) =>
      call(params.fromPath[String]("gid", None)) { (gid) =>
        controllers_Rest_deleteGroupInvite20_invoker.call(Rest_2.deleteGroupInvite(gid))
      }
  
    // @LINE:32
    case controllers_Rest_joinGroupInvite21_route(params) =>
      call(params.fromPath[String]("gid", None), params.fromPath[String]("token", None)) { (gid, token) =>
        controllers_Rest_joinGroupInvite21_invoker.call(Rest_2.joinGroupInvite(gid, token))
      }
  
    // @LINE:35
    case controllers_Rest_createGroup22_route(params) =>
      call { 
        controllers_Rest_createGroup22_invoker.call(Rest_2.createGroup())
      }
  
    // @LINE:36
    case controllers_Rest_listGroups23_route(params) =>
      call { 
        controllers_Rest_listGroups23_invoker.call(Rest_2.listGroups())
      }
  
    // @LINE:37
    case controllers_Rest_listGroupMembers24_route(params) =>
      call(params.fromPath[String]("groupId", None)) { (groupId) =>
        controllers_Rest_listGroupMembers24_invoker.call(Rest_2.listGroupMembers(groupId))
      }
  
    // @LINE:38
    case controllers_Rest_searchGroupCandidates25_route(params) =>
      call(params.fromPath[String]("groupId", None)) { (groupId) =>
        controllers_Rest_searchGroupCandidates25_invoker.call(Rest_2.searchGroupCandidates(groupId))
      }
  
    // @LINE:39
    case controllers_Rest_listGroupMembersProperties26_route(params) =>
      call(params.fromPath[String]("groupId", None)) { (groupId) =>
        controllers_Rest_listGroupMembersProperties26_invoker.call(Rest_2.listGroupMembersProperties(groupId))
      }
  
    // @LINE:40
    case controllers_Rest_addGroupMember27_route(params) =>
      call(params.fromPath[String]("gid", None), params.fromPath[String]("uid", None)) { (gid, uid) =>
        controllers_Rest_addGroupMember27_invoker.call(Rest_2.addGroupMember(gid, uid))
      }
  
    // @LINE:41
    case controllers_Rest_removeGroupMember28_route(params) =>
      call(params.fromPath[String]("gid", None), params.fromPath[String]("uid", None)) { (gid, uid) =>
        controllers_Rest_removeGroupMember28_invoker.call(Rest_2.removeGroupMember(gid, uid))
      }
  
    // @LINE:42
    case controllers_Rest_postSdp29_route(params) =>
      call(params.fromPath[String]("key", None)) { (key) =>
        controllers_Rest_postSdp29_invoker.call(Rest_2.postSdp(key))
      }
  
    // @LINE:43
    case controllers_Rest_postIceCandidate30_route(params) =>
      call(params.fromPath[String]("key", None), params.fromPath[String]("token", None)) { (key, token) =>
        controllers_Rest_postIceCandidate30_invoker.call(Rest_2.postIceCandidate(key, token))
      }
  
    // @LINE:45
    case controllers_Rest_publish31_route(params) =>
      call(params.fromPath[String]("key", None)) { (key) =>
        controllers_Rest_publish31_invoker.call(Rest_2.publish(key))
      }
  
    // @LINE:46
    case controllers_Rest_subscribe32_route(params) =>
      call(params.fromPath[String]("key", None), params.fromPath[Long]("ts", None)) { (key, ts) =>
        controllers_Rest_subscribe32_invoker.call(Rest_2.subscribe(key, ts))
      }
  
    // @LINE:49
    case controllers_Rest_getPhoto33_route(params) =>
      call(params.fromPath[String]("id", None)) { (id) =>
        controllers_Rest_getPhoto33_invoker.call(Rest_2.getPhoto(id))
      }
  
    // @LINE:50
    case controllers_Rest_getFile34_route(params) =>
      call(params.fromPath[String]("id", None)) { (id) =>
        controllers_Rest_getFile34_invoker.call(Rest_2.getFile(id))
      }
  
    // @LINE:53
    case controllers_Assets_versioned35_route(params) =>
      call(Param[String]("path", Right("/public")), params.fromPath[Asset]("file", None)) { (path, file) =>
        controllers_Assets_versioned35_invoker.call(Assets_0.versioned(path, file))
      }
  
    // @LINE:56
    case controllers_WSController_connectToRoom36_route(params) =>
      call(params.fromPath[String]("name", None)) { (name) =>
        controllers_WSController_connectToRoom36_invoker.call(WSController_3.connectToRoom(name))
      }
  }
}