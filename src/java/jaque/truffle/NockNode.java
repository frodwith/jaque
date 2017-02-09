package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.dsl.Specialization;

@NodeInfo(shortName = "nock")
@NodeField(value = "dispatch", type = NockDispatchNode.class)
public abstract class NockNode extends Formula {
  @Child private Formula subject;
  @Child private Formula formula;

  @Specialization
  public Noun nock(VirtualFrame frame, Noun subject, Cell formula) {
    return dispatch.executeNock(frame, subject, formula);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(2), new Cell(subject.toNoun(), formula.toNoun()));
  }
}
