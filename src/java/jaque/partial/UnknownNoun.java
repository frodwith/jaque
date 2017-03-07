package jaque.partial;

import jaque.noun.Atom;
import jaque.noun.Fragmenter;

public class UnknownNoun extends PartialSubject {
  public final UnknownNoun INSTANCE = new UnknownNoun();
  public final Atom[] all = { Atom.ONE };

  private UnknownNoun() {
  }

  public boolean fine(Object test) {
    return true;
  }
  
  public Atom[] getSamples() {
    return all;
  }
  
  public Object fragment(Atom axis, Object[] arguments) {
    return new Fragmenter(axis).fragment(arguments[0]);
  }
}
