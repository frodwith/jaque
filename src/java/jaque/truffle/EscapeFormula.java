package jaque.truffle;

import com.oracle.truffle.api.nodes.NodeInfo;

import jaque.noun.Cell;
import jaque.noun.Noun;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "esc")
public class EscapeFormula extends Formula {
  @Child private Formula ref;
  @Child private Formula sam;
  
  public EscapeFormula(Formula ref, Formula sam) {
    this.ref = ref;
    this.sam = sam;
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    Noun r = Noun.coerceNoun(ref.executeSafe(frame));
    Noun s = Noun.coerceNoun(sam.executeSafe(frame));
    return getContext(frame).escape(r, s);
  }

  public Cell toCell() {
    return new Cell(11, new Cell(ref.toCell(), sam.toCell()));
  }
}
