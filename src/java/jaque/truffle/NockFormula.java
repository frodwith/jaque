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
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;

@NodeInfo(shortName = "nock")
public class NockFormula extends Formula {
  @Child private Formula subjectF;
  @Child private Formula formulaF;
  @Child private NockDispatchNode dispatch;
  
  public NockFormula(Formula subjectF, Formula formulaF) {
    this.subjectF = subjectF;
    this.formulaF = formulaF;
    this.dispatch = NockDispatchNodeGen.create();
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    Object subject = subjectF.executeSafe(frame);
    Cell formula;
    try {
      formula = formulaF.executeCell(frame);
    } 
    catch (UnexpectedResultException e) {
      throw new Bail();
    }
    return dispatch.executeNock(frame, subject, formula);
  }

  public Cell toCell() {
    return new Cell(2, new Cell(subjectF.toCell(), formulaF.toCell()));
  }
}
