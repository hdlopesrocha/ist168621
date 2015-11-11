
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/hdlopesrocha/ist168621/httpserver/conf/routes
// @DATE:Sat Oct 03 23:38:43 WEST 2015

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseRest Rest = new controllers.ReverseRest(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseWSController WSController = new controllers.ReverseWSController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAssets Assets = new controllers.ReverseAssets(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseApplication Application = new controllers.ReverseApplication(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseRest Rest = new controllers.javascript.ReverseRest(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseWSController WSController = new controllers.javascript.ReverseWSController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAssets Assets = new controllers.javascript.ReverseAssets(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseApplication Application = new controllers.javascript.ReverseApplication(RoutesPrefix.byNamePrefix());
  }

}
