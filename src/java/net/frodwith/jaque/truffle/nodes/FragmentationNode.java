package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Fragment;

public class FragmentationNode extends JaqueNode {
  @Children private final ReadNode[] reads;
  
  public FragmentationNode(Object atom) {
    Axis axis = new Axis(atom);
    this.reads = new ReadNode[axis.length];
    
    int i = 0;
    for ( Fragment f : axis ) {
      this.reads[i++] = ReadNodeGen.create(f);
    }
    
  }
  
  public Object executeFragment(Object o) {
    for ( ReadNode r : reads ) {
      o = r.executeRead(o);
    }
    return o;
  }
  
}
