package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;

public abstract class AesEcbNode extends SampleContextNode {
  public abstract Object executeEcb(Object key, Object block);
  
  public Object doSampleContext(Object sample, Object context) {
    Object conSam = Cell.expect(Cell.expect(context).tail).head;
    return executeEcb(conSam, sample);
  }

}