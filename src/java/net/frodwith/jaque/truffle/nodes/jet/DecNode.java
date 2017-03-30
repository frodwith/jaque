package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;

public abstract class DecNode extends UnaryGateNode {

  @Specialization
  protected long dec(long atom) {
    if ( atom == 0 ) {
      System.err.print("decrement underflow");
      throw new Bail();
    }
    return atom - 1L;
  }
  
  @Specialization
  protected Object dec(int[] atom) {
    return Atom.dec(atom);
  }

}