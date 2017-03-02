package jaque.truffle;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;

import jaque.interpreter.Machine;
import jaque.interpreter.Result;
import jaque.noun.Cell;
import jaque.noun.Noun;

public class NockLanguage extends TruffleLanguage<NockContext> {

  public static final Result nock(Machine m, Noun subject, Cell formula) {
    NockContext c = new NockContext(m);
    Formula f = Formula.fromCell(formula);
    CallTarget t = Truffle.getRuntime().createCallTarget(new NockRootNode(f));
    DirectCallNode callNode = DirectCallNode.create(t);
    Object[] arguments = new Object[] {c, subject};
    VirtualFrame frame = Truffle.getRuntime().createVirtualFrame(arguments, new FrameDescriptor());
    Object result = callNode.call(frame, arguments);
    return new Result(c.m, Noun.coerceNoun(result));
  }
  
  @Override
  protected NockContext createContext(com.oracle.truffle.api.TruffleLanguage.Env env) {
    // TODO: This isn't quite implemented...
    return null;
  }

  @Override
  protected Object findExportedSymbol(NockContext context, String globalName, boolean onlyExplicit) {
    // no symbols are exported
    return null;
  }

  @Override
  protected Object getLanguageGlobal(NockContext context) {
    // there are no language globals
    return null;
  }

  @Override
  protected boolean isObjectOfLanguage(Object object) {
    return false;
  }

}
