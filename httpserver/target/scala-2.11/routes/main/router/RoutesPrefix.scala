
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/hdlopesrocha/ist168621/httpserver/conf/routes
// @DATE:Sat Oct 03 23:38:43 WEST 2015


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
