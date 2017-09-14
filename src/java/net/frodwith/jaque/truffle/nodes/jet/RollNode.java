package net.frodwith.jaque.truffle.nodes.jet;

import java.util.function.Function;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.truffle.nodes.JaqueNode;
import net.frodwith.jaque.truffle.nodes.formula.BailNode;

public abstract class RollNode extends PairGateNode {
  @Child private JaqueNode holder = new BailNode();

  @Specialization
  public Object roll(VirtualFrame frame, Object list, Cell gate) {
    Function<Object, Object> f = getContext().internalSlam(frame, holder, gate);
    Object seed = Cell.expect(Cell.expect(gate.tail).head).tail;
    return List.roll(f, seed, list);
  }

}