package jaque.truffle;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.source.*;
import jaque.noun.*;

public class NockLanguage extends TruffleLanguage<NockContext> {
  protected NockContext createContext (TruffleLanguage.Env env) {
    return new NockContext(env);
  }

  protected CallTarget parse(TruffleLanguage.ParsingRequest r) throws Exception {
    Noun src = Noun.read(r.getSource().getCode());
    assert src instanceof Cell;
    return nounToAst((Cell) src);
  }
}
