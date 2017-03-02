package jaque.truffle;

import com.oracle.truffle.api.nodes.NodeInfo;

import jaque.noun.Cell;
import jaque.noun.Noun;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "esc")
@NodeChildren({@NodeChild("ref"), @NodeChild("sam")})
public abstract class EscapeFormula extends Formula {
  public abstract Formula getRef();
  public abstract Formula getSam();

  @Specialization
  public Object escape(VirtualFrame frame, Object ref, Object sam) {
    return getContext(frame).escape(Noun.coerceNoun(ref), Noun.coerceNoun(sam));
  }

  public Cell toCell() {
    return new Cell(11, new Cell(getRef().toCell(), getSam().toCell()));
  }
}
