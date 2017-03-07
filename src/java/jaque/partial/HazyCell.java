package jaque.partial;

import jaque.noun.Atom;
import jaque.noun.Fragmenter;

public class HazyCell extends PartialSubject {
  private final PartialSubject left;
  private final PartialSubject right;
  private final Atom[] samples;
  
  public HazyCell(PartialSubject left, PartialSubject right) {
    this.left = left;
    this.right = right;

    Atom[] leftSamples = left.getSamples();
    Atom[] rightSamples = right.getSamples();
    this.samples = new Atom[leftSamples.length + rightSamples.length];
    
    int i, j;
    
    for ( j = 0, i = 0; i < leftSamples.length; ++i, ++j ) {
      samples[j] = leftSamples[i].peg(Atom.TWO);
    }

    for ( i = 0; i < rightSamples.length; ++i, ++j ) {
      samples[j] = rightSamples[i].peg(Atom.THREE);
    }
  }
  
  public boolean fine(Object test) {
    return left.fine(test) && right.fine(test);
  }
  
  public Atom[] getSamples() {
    return samples;
  }
  
  // null arguments means we can assume the answer is in our static knowledge
  public Object fragment(Atom axis, Object[] arguments) {
    if (arguments != null) {
      for ( int i = 0; i < samples.length; ++i ) {
        Atom dug = axis.dig(samples[i]);
        if ( !dug.isZero() ) {
          return new Fragmenter(dug).fragment(arguments[i]);
        }
      }
    }
    PartialSubject next = (2 == axis.cap()) ? left : right;
    return next.fragment(axis.mas(), null);
  }
}
