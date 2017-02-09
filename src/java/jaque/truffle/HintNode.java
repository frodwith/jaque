package jaque.truffle;

import jaque.interpreter.Result;
import jaque.interpreter.Hint;
import jaque.noun.*;

public abstract class HintNode extends Formula {
  @Child private Node contextNode;
  @Child private Atom kind;

  public abstract Noun clue(VirtualFrame frame);
  public abstract Formula next();

  protected HintNode(Atom kind) {
    this.kind        = kind;
    this.contextNode = NockLanguage.createFindContextNode();
  }

  public Noun executeNoun(VirtualFrame frame) {
    NockContext c = NockLanguage.findContext(contextNode);
    Formula nextF = next();
    Noun  subject = frame.getArguments()[0];
    Hint        h = new Hint(kind, clue(frame), subject, nextF.source());
    Noun      pro = c.startHint(h);

    if ( null == pro ) {
      pro = nextF.executeNoun(frame);
      c.endHint(h, pro);
    }

    return pro;
  }
}
