package jaque.truffle;

import jaque.interpreter.Bail;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;

@NodeInfo(shortName = "nock")
public class NockFormula extends UnsafeFormula {
  @Child private Formula subjectF;
  @Child private Formula formulaF;
  
  public NockFormula(Formula subjectF, Formula formulaF) {
    this.subjectF = subjectF;
    this.formulaF = formulaF;
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    Object old = getSubject(frame);
    Object subject = subjectF.executeSafe(frame);
    setSubject(frame, old);
    Cell formula;
    try {
      formula = formulaF.executeCell(frame);
    } 
    catch (UnexpectedResultException e) {
      throw new Bail();
    }
    throw new NockCallException(getContext(frame).getNockTarget(formula), subject);
  }

  public Cell toCell() {
    return new Cell(2L, new Cell(subjectF.toCell(), formulaF.toCell()));
  }
}
