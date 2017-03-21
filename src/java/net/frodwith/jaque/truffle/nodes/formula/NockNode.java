package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TailException;
import net.frodwith.jaque.truffle.nodes.DispatchNode;
import net.frodwith.jaque.truffle.nodes.DispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.NockDispatchNode;

@NodeField(name="dispatch", type=NockDispatchNode.class)
public abstract class NockNode extends BinaryFormulaNode {
  public abstract NockDispatchNode getDispatch();

  @Specialization
  protected Object doNock(VirtualFrame frame, Object subject, Cell formula) {
    return getDispatch().executeNock(frame, subject, formula);
  }
}
