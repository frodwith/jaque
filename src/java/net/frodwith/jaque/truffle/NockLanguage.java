package net.frodwith.jaque.truffle;

/* This mostly just exists to make RootNode happy/ */

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;

public class NockLanguage extends TruffleLanguage<Context> {
  
  @Override
  protected Context createContext(com.oracle.truffle.api.TruffleLanguage.Env env) {
    //Arm[] jetDrivers = (Arm[]) env.getConfig().get("jetDrivers");
    return new Context(); // this shit has never worked
  }
  
  public Node contextNode() {
    return createFindContextNode();
  }
  
  public Context context(Node contextNode) {
    return findContext(contextNode);
  }

  @Override
  protected Object findExportedSymbol(Context context, String globalName, boolean onlyExplicit) {
    // nock doesn't have this concept
    return null;
  }

  @Override
  protected Object getLanguageGlobal(Context context) {
    // nock doesn't have this concept
    return null;
  }

  @Override
  protected boolean isObjectOfLanguage(Object object) {
    return TypesGen.isCell(object) || TypesGen.isImplicitIntArray(object);
  }
}
