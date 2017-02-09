package jaque.truffle;

import jaque.interpreter.Hint;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class HintNode extends Formula {
  @Child private Node contextNode;
  @Child private Atom kind;

  public abstract Noun clue(VirtualFrame frame);
  public abstract Formula next();

  protected HintNode(Atom kind) {
    this.kind        = kind;
    this.contextNode = NockLanguage.createFindContextNode();
  }

  @Override
  public Object execute(VirtualFrame frame) {
    NockContext c = NockLanguage.findContext(contextNode);
    Formula nextF = next();
    Hint        h = new Hint(kind, clue(frame), getSubject(frame), nextF.toNoun());
    Noun      pro = c.startHint(h);

    if ( null == pro ) {
      pro = nextF.executeNoun(frame);
      c.endHint(h, pro);
    }

    return pro;
  }
}
