package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.BranchProfile;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.TypesGen;

public class FragmentationNode extends JaqueNode {
  @Children private final ReadNode[] reads;
  private final BranchProfile errorPath;
  
  public FragmentationNode(Object axis) {
    this.errorPath = BranchProfile.create();
    assert !Atom.isZero(axis);
    
    int i, j, bits = Atom.measure(axis);
    this.reads = new ReadNode[bits-1];
    
    for ( i = 0, j = (bits - 2); j >= 0; ++i, --j ) {
      this.reads[i] = Atom.getNthBit(axis, j)
          ? new TailNode()
          : new HeadNode();
    }
  }
  
  @ExplodeLoop
  public Object executeFragment(Object subject) {
    CompilerAsserts.compilationConstant(reads.length);
    try {
      for ( ReadNode n : reads ) {
        subject = n.executeRead(TypesGen.expectCell(subject));
      }
      return subject;
    }
    catch (UnexpectedResultException e) {
      errorPath.enter();
      throw new Bail();
    }
  }
  
}
