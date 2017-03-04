package jaque.truffle;

import com.oracle.truffle.api.nodes.NodeInfo;

import jaque.noun.Cell;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "cons")
public class ConsFormula extends Formula {
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
    return new Cell(head.executeSafe(frame), tail.executeSafe(frame));
  }

  public Cell toCell() {
    return new Cell(head.toCell(), tail.toCell());
  }
}
