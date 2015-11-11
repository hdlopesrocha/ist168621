
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/hdlopesrocha/ist168621/httpserver/conf/routes
// @DATE:Sat Oct 03 23:38:43 WEST 2015

import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:6
package controllers {

  // @LINE:14
  class ReverseRest(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:16
    def updateUser(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/user/update")
    }
  
    // @LINE:46
    def subscribe(key:String, ts:Long): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/pubsub/" + implicitly[PathBindable[String]].unbind("key", dynamicString(key)) + "/" + implicitly[PathBindable[Long]].unbind("ts", ts))
    }
  
    // @LINE:50
    def getFile(id:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "file/" + implicitly[PathBindable[String]].unbind("id", id))
    }
  
    // @LINE:32
    def joinGroupInvite(gid:String, token:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/invite/join/" + implicitly[PathBindable[String]].unbind("gid", dynamicString(gid)) + "/" + implicitly[PathBindable[String]].unbind("token", dynamicString(token)))
    }
  
    // @LINE:36
    def listGroups(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/group/list")
    }
  
    // @LINE:42
    def postSdp(key:String): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/group/sdp/" + implicitly[PathBindable[String]].unbind("key", dynamicString(key)))
    }
  
    // @LINE:23
    def denyRelation(user_id:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/relation/del/" + implicitly[PathBindable[String]].unbind("user_id", dynamicString(user_id)))
    }
  
    // @LINE:38
    def searchGroupCandidates(groupId:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/group/candidates/" + implicitly[PathBindable[String]].unbind("groupId", dynamicString(groupId)))
    }
  
    // @LINE:41
    def removeGroupMember(gid:String, uid:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/group/del/" + implicitly[PathBindable[String]].unbind("gid", dynamicString(gid)) + "/" + implicitly[PathBindable[String]].unbind("uid", dynamicString(uid)))
    }
  
    // @LINE:37
    def listGroupMembers(groupId:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/group/members/" + implicitly[PathBindable[String]].unbind("groupId", dynamicString(groupId)))
    }
  
    // @LINE:49
    def getPhoto(id:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "photo/" + implicitly[PathBindable[String]].unbind("id", id))
    }
  
    // @LINE:31
    def deleteGroupInvite(gid:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/invite/delete/" + implicitly[PathBindable[String]].unbind("gid", dynamicString(gid)))
    }
  
    // @LINE:29
    def createGroupInvite(gid:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/invite/create/" + implicitly[PathBindable[String]].unbind("gid", dynamicString(gid)))
    }
  
    // @LINE:40
    def addGroupMember(gid:String, uid:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/group/add/" + implicitly[PathBindable[String]].unbind("gid", dynamicString(gid)) + "/" + implicitly[PathBindable[String]].unbind("uid", dynamicString(uid)))
    }
  
    // @LINE:18
    def getUser(user_id:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/user/get/" + implicitly[PathBindable[String]].unbind("user_id", dynamicString(user_id)))
    }
  
    // @LINE:24
    def acceptRelation(user_id:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/relation/add/" + implicitly[PathBindable[String]].unbind("user_id", dynamicString(user_id)))
    }
  
    // @LINE:26
    def listRelations(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/relation/list")
    }
  
    // @LINE:21
    def logout(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/user/logout")
    }
  
    // @LINE:15
    def listUsers(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/user/list")
    }
  
    // @LINE:35
    def createGroup(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/group/create")
    }
  
    // @LINE:20
    def register(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/user/register")
    }
  
    // @LINE:17
    def changePassword(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/user/password")
    }
  
    // @LINE:14
    def search(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/user/search")
    }
  
    // @LINE:25
    def listRelationRequests(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/relation/requests")
    }
  
    // @LINE:30
    def getGroupInvite(gid:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/invite/get/" + implicitly[PathBindable[String]].unbind("gid", dynamicString(gid)))
    }
  
    // @LINE:43
    def postIceCandidate(key:String, token:String): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/group/ice/" + implicitly[PathBindable[String]].unbind("key", dynamicString(key)) + "/" + implicitly[PathBindable[String]].unbind("token", dynamicString(token)))
    }
  
    // @LINE:39
    def listGroupMembersProperties(groupId:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/group/properties/" + implicitly[PathBindable[String]].unbind("groupId", dynamicString(groupId)))
    }
  
    // @LINE:19
    def login(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/user/login")
    }
  
    // @LINE:45
    def publish(key:String): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/pubsub/" + implicitly[PathBindable[String]].unbind("key", dynamicString(key)))
    }
  
  }

  // @LINE:56
  class ReverseWSController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:56
    def connectToRoom(name:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "ws/room/" + implicitly[PathBindable[String]].unbind("name", dynamicString(name)))
    }
  
  }

  // @LINE:53
  class ReverseAssets(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:53
    def versioned(file:Asset): Call = {
      implicit val _rrc = new ReverseRouteContext(Map(("path", "/public")))
      Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[PathBindable[Asset]].unbind("file", file))
    }
  
  }

  // @LINE:6
  class ReverseApplication(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def template(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "template")
    }
  
    // @LINE:11
    def join(gid:String, token:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "join/" + implicitly[PathBindable[String]].unbind("gid", dynamicString(gid)) + "/" + implicitly[PathBindable[String]].unbind("token", dynamicString(token)))
    }
  
    // @LINE:8
    def group(groupId:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "group/" + implicitly[PathBindable[String]].unbind("groupId", dynamicString(groupId)))
    }
  
    // @LINE:7
    def pubsub(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "pubsub")
    }
  
    // @LINE:10
    def stream(path:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "stream/" + implicitly[PathBindable[String]].unbind("path", path))
    }
  
    // @LINE:6
    def index(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix)
    }
  
  }


}