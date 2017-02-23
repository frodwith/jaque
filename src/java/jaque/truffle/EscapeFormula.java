package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "esc")
@NodeChildren({@NodeChild("ref"), @NodeChild("sam")})
public abstract class EscapeFormula extends Formula {
  public abstract Formula getRef();
  public abstract Formula getSam();

  @Specialization
  public Noun escape(VirtualFrame frame, Noun ref, Noun sam) {
    return getContext(frame).escape(ref, sam);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(11), new Cell(getRef().toNoun(), getSam().toNoun()));
  }
}
