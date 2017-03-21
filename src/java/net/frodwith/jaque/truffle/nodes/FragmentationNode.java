package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.profiles.BranchProfile;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.TypesGen;

public class FragmentationNode extends JaqueNode {
  private final boolean[] path;
  private BranchProfile errorPath = BranchProfile.create();
  
  public FragmentationNode(Object axis) {
    int i, j, bits = Atom.measure(axis);
    if ( Atom.isZero(axis) ) {
      this.path = null;
    }
    else {
      this.path = new boolean[bits-1];
      
      for ( i = 0, j = (bits - 2); j >= 0; ++i, --j ) {
        path[i] = Atom.getNthBit(axis, j);
      }
    }
  }
  
  @ExplodeLoop
  public Object executeFragment(Object subject) {
    CompilerAsserts.compilationConstant(path.length);
    if ( null == path ) {
      errorPath.enter();
      throw new Bail();
    }
    for ( boolean tail : path ) {
      if ( !TypesGen.isCell(subject) ) {
        errorPath.enter();
        throw new Bail();
      }
      Cell c = TypesGen.asCell(subject);
      subject = tail ? c.tail : c.head;
    }
    return subject;
  }
  
}
