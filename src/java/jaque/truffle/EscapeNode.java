package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class EscapeNode extends Formula {
  @Child private Formula ref;
  @Child private Formula sam;
  @Child private Node contextNode;

  public EscapeNode(Formula ref, Formula sam) {
    this.ref         = ref;
    this.sam         = sam;
    this.contextNode = NockLanguage.createFindContextNode();
  }

  @Override
  public Noun execute(VirtualFrame frame) {
    Noun r = ref.executeNoun(frame);
    Noun s = sam.executeNoun(frame);

    return NockLanguage.findContext(contextNode).escape(r, s);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(11), new Cell(ref.toNoun(), sam.toNoun()));
  }
}
