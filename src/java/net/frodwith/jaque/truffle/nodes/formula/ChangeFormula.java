package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;

@NodeChildren({
  @NodeChild(value = "f", type = Formula.class),
  @NodeChild(value = "g", type = Formula.class)})
public abstract class ChangeFormula extends JumpFormula {
  public abstract Formula getF();
  public abstract Formula getG();

}
