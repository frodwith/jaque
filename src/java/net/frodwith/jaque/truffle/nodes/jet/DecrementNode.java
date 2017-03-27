package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class DecrementNode extends UnaryJetNode {

  @Specialization
  protected long decrement(long atom) {
    if ( atom == 0 ) {
      System.err.print("decrement underflow");
      throw new Bail();
    }
    return atom - 1L;
  }
  
  @Specialization
  protected Object decrement(int[] atom) {
    return Atom.decrement(atom);
  }

  @Specialization
  protected Object decrement(Object atom) {
    if ( !Noun.isAtom(atom) ) {
      throw new Bail();
    }
    return Atom.decrement(atom);
  }
}