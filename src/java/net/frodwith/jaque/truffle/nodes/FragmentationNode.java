package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Fragment;
import net.frodwith.jaque.truffle.FragmentationException;
import net.frodwith.jaque.truffle.TypesGen;

public class FragmentationNode extends JaqueNode {
  @Children private final ReadNode[] reads;
  
  public FragmentationNode(Object atom) {
    int i = 0;
    Axis axis = new Axis(atom);
    this.reads = new ReadNode[axis.length];
    
    for ( Fragment f : axis ) {
      this.reads[i++] = (f == Fragment.HEAD)
          ? new ReadHeadNode()
          : new ReadTailNode();
    }
  }
  
  @ExplodeLoop
  public Object executeFragment(Object subject) throws FragmentationException {
    try {
      for ( ReadNode n : reads ) {
        subject = n.executeRead(TypesGen.expectCell(subject));
      }
      return subject;
    }
    catch (UnexpectedResultException e) {
      throw new FragmentationException();
    }
  }
  
}
