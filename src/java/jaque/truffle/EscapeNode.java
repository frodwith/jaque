package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

public final class EscapeNode extends Formula {
  @Child private Formula ref;
  @Child private Formula sam;
  @Child private Node contextNode;

  public EscapeNode(Formula ref, Formula sam) {
    this.ref         = ref;
    this.sam         = sam;
    this.contextNode = NockLanguage.createFindContextNode();
  }

  public Noun execute(VirtualFrame frame) {
    Noun r = ref.executeNoun(frame);
    Noun s = sam.executeNoun(frame);

    return NockLanguage.findContext(contextNode).escape(r, s);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(11), new Cell(ref.toNoun(), sam.toNoun()));
  }
}
