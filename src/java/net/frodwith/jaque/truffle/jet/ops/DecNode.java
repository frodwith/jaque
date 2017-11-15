package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.UnaryOpNode;

@NodeField(name="context", type=Context.class)
public abstract class DecNode extends UnaryOpNode {
  public abstract Context getContext();

  @Specialization
  protected long dec(long atom) {
    if ( atom == 0 ) {
      getContext().err("decrement underflow");
      throw new Bail();
    }
    return atom - 1L;
  }
  
  @Specialization
  protected Object dec(int[] atom) {
    return Atom.dec(atom);
  }
}