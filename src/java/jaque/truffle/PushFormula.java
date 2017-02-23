package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "push")
public final class PushFormula extends Formula {
  @Child private Formula f;
  @Child private Formula g;

  public PushFormula(Formula f, Formula g) {
    this.f = f;
    this.g = g;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    Noun head;
    try {
      head = f.executeNoun(frame);
    } catch (UnexpectedResultException e) {
      throw new UnsupportedSpecializationException(this, new Node[] {f}, e);
    }
    Object[] a = frame.getArguments();
    a[0] = new Cell(head, (Noun) a[0]);
    return g.execute(frame);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(8), new Cell(f.toNoun(), g.toNoun()));
  }
}
