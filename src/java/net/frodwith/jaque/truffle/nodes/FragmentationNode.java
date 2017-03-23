package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.ValueProfile;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.TypesGen;

public class FragmentationNode extends JaqueNode {
  @Children private final ReadNode[] reads;
//  private final ValueProfile constantSubject = ValueProfile.createIdentityProfile();
  
  public FragmentationNode(Object axis) {
    assert !Atom.isZero(axis);
    
    int i, j, bits = Atom.measure(axis);
    this.reads = new ReadNode[bits-1];
    
    for ( i = 0, j = (bits - 2); j >= 0; ++i, --j ) {
      this.reads[i] = Atom.getNthBit(axis, j)
          ? new ReadTailNode()
          : new ReadHeadNode();
    }
  }
  
  @ExplodeLoop
  public Object executeFragment(Object o) {
    try {
      for ( ReadNode n : reads ) {
        o = n.executeRead(TypesGen.asCell(o));
      }
      return o;
    }
    catch (ClassCastException e) {
      throw new Bail();
    }
  }
  
}
