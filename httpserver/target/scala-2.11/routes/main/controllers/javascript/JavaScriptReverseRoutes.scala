
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/hdlopesrocha/ist168621/httpserver/conf/routes
// @DATE:Sat Oct 03 23:38:43 WEST 2015

import play.api.routing.JavaScriptReverseRoute
import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:6
package controllers.javascript {
  import ReverseRouteContext.empty

  // @LINE:14
  class ReverseRest(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:16
    def updateUser: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.updateUser",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/user/update"})
        }
      """
    )
  
    // @LINE:46
    def subscribe: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.subscribe",
      """
        function(key,ts) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/pubsub/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("key", encodeURIComponent(key)) + "/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("ts", ts)})
        }
      """
    )
  
    // @LINE:50
    def getFile: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.getFile",
      """
        function(id) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "file/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:32
    def joinGroupInvite: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.joinGroupInvite",
      """
        function(gid,token) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/invite/join/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("gid", encodeURIComponent(gid)) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("token", encodeURIComponent(token))})
        }
      """
    )
  
    // @LINE:36
    def listGroups: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.listGroups",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/group/list"})
        }
      """
    )
  
    // @LINE:42
    def postSdp: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.postSdp",
      """
        function(key) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/group/sdp/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("key", encodeURIComponent(key))})
        }
      """
    )
  
    // @LINE:23
    def denyRelation: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.denyRelation",
      """
        function(user_id) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/relation/del/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("user_id", encodeURIComponent(user_id))})
        }
      """
    )
  
    // @LINE:38
    def searchGroupCandidates: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.searchGroupCandidates",
      """
        function(groupId) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/group/candidates/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("groupId", encodeURIComponent(groupId))})
        }
      """
    )
  
    // @LINE:41
    def removeGroupMember: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.removeGroupMember",
      """
        function(gid,uid) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/group/del/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("gid", encodeURIComponent(gid)) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("uid", encodeURIComponent(uid))})
        }
      """
    )
  
    // @LINE:37
    def listGroupMembers: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.listGroupMembers",
      """
        function(groupId) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/group/members/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("groupId", encodeURIComponent(groupId))})
        }
      """
    )
  
    // @LINE:49
    def getPhoto: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.getPhoto",
      """
        function(id) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "photo/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:31
    def deleteGroupInvite: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.deleteGroupInvite",
      """
        function(gid) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/invite/delete/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("gid", encodeURIComponent(gid))})
        }
      """
    )
  
    // @LINE:29
    def createGroupInvite: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.createGroupInvite",
      """
        function(gid) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/invite/create/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("gid", encodeURIComponent(gid))})
        }
      """
    )
  
    // @LINE:40
    def addGroupMember: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.addGroupMember",
      """
        function(gid,uid) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/group/add/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("gid", encodeURIComponent(gid)) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("uid", encodeURIComponent(uid))})
        }
      """
    )
  
    // @LINE:18
    def getUser: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.getUser",
      """
        function(user_id) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/user/get/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("user_id", encodeURIComponent(user_id))})
        }
      """
    )
  
    // @LINE:24
    def acceptRelation: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.acceptRelation",
      """
        function(user_id) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/relation/add/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("user_id", encodeURIComponent(user_id))})
        }
      """
    )
  
    // @LINE:26
    def listRelations: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.listRelations",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/relation/list"})
        }
      """
    )
  
    // @LINE:21
    def logout: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.logout",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/user/logout"})
        }
      """
    )
  
    // @LINE:15
    def listUsers: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.listUsers",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/user/list"})
        }
      """
    )
  
    // @LINE:35
    def createGroup: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.createGroup",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/group/create"})
        }
      """
    )
  
    // @LINE:20
    def register: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.register",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/user/register"})
        }
      """
    )
  
    // @LINE:17
    def changePassword: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.changePassword",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/user/password"})
        }
      """
    )
  
    // @LINE:14
    def search: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.search",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/user/search"})
        }
      """
    )
  
    // @LINE:25
    def listRelationRequests: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.listRelationRequests",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/relation/requests"})
        }
      """
    )
  
    // @LINE:30
    def getGroupInvite: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.getGroupInvite",
      """
        function(gid) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/invite/get/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("gid", encodeURIComponent(gid))})
        }
      """
    )
  
    // @LINE:43
    def postIceCandidate: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.postIceCandidate",
      """
        function(key,token) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/group/ice/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("key", encodeURIComponent(key)) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("token", encodeURIComponent(token))})
        }
      """
    )
  
    // @LINE:39
    def listGroupMembersProperties: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.listGroupMembersProperties",
      """
        function(groupId) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/group/properties/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("groupId", encodeURIComponent(groupId))})
        }
      """
    )
  
    // @LINE:19
    def login: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.login",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/user/login"})
        }
      """
    )
  
    // @LINE:45
    def publish: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Rest.publish",
      """
        function(key) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/pubsub/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("key", encodeURIComponent(key))})
        }
      """
    )
  
  }

  // @LINE:56
  class ReverseWSController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:56
    def connectToRoom: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.WSController.connectToRoom",
      """
        function(name) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "ws/room/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("name", encodeURIComponent(name))})
        }
      """
    )
  
  }

  // @LINE:53
  class ReverseAssets(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:53
    def versioned: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Assets.versioned",
      """
        function(file) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[PathBindable[Asset]].javascriptUnbind + """)("file", file)})
        }
      """
    )
  
  }

  // @LINE:6
  class ReverseApplication(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def template: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.template",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "template"})
        }
      """
    )
  
    // @LINE:11
    def join: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.join",
      """
        function(gid,token) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "join/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("gid", encodeURIComponent(gid)) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("token", encodeURIComponent(token))})
        }
      """
    )
  
    // @LINE:8
    def group: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.group",
      """
        function(groupId) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "group/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("groupId", encodeURIComponent(groupId))})
        }
      """
    )
  
    // @LINE:7
    def pubsub: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.pubsub",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "pubsub"})
        }
      """
    )
  
    // @LINE:10
    def stream: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.stream",
      """
        function(path) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "stream/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("path", path)})
        }
      """
    )
  
    // @LINE:6
    def index: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.index",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + """"})
        }
      """
    )
  
  }


}