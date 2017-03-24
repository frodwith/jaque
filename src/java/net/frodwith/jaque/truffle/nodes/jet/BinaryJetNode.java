package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Fragment;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;
import net.frodwith.jaque.truffle.nodes.ReadNode;
import net.frodwith.jaque.truffle.nodes.ReadNodeGen;

public abstract class BinaryJetNode extends ImplementationNode {
  protected abstract Object executeBinary(Object a, Object b);
  @Child private FragmentationNode fragment = new FragmentationNode(6L);
  @Child private ReadNode headNode = ReadNodeGen.create(Fragment.HEAD);
  @Child private ReadNode tailNode = ReadNodeGen.create(Fragment.TAIL);

  @Override
  public Object doJet(Object subject) {
    Object sample = Noun.asCell(fragment.executeFragment(subject)),
        head = headNode.executeRead(sample),
        tail = tailNode.executeRead(sample);

    return this.executeBinary(head, tail);
  }

}
