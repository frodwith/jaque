package jaque.partial;

import jaque.noun.Atom;

public abstract class PartialSubject {
  public abstract boolean fine(Object test);
  public abstract Atom[] getSamples();
  public abstract Object fragment(Atom axis, Object[] arguments);
}
