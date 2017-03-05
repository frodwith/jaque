package jaque.truffle;

import com.oracle.truffle.api.nodes.NodeInfo;

import jaque.noun.Cell;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "cons")
public class ConsFormula extends SafeFormula {
  @Child private Formula head;
  @Child private Formula tail;
  
  public ConsFormula(Formula head, Formula tail) {
    this.head = head;
    this.tail = tail;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return executeCell(frame);
  }
  
  @Override
  public Cell executeCell(VirtualFrame frame) {
    Object subject = getSubject(frame);
    Object h = head.executeSafe(frame);
    setSubject(frame, subject);
    Object t = tail.executeSafe(frame);
    return new Cell(h, t);
  }

  public Cell toCell() {
    return new Cell(head.toCell(), tail.toCell());
  }
}
