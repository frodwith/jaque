package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;

public abstract class AesCbcNode extends SampleContextNode {
  public abstract Object executeCbc(Object key, Object iv, Object msg);
  
  public Object doSampleContext(Object sample, Object context) {
    Cell conSam = Cell.expect(Cell.expect(Cell.expect(context).tail).head);
    return executeCbc(conSam.head, conSam.tail, sample);
  }

}