package jaque.truffle;

import jaque.noun.Atom;
import jaque.noun.Cell;

public class KickLabel {
  public Cell battery;
  public Atom axis;

  public KickLabel(Cell battery, Atom axis) {
    this.battery = battery;
    this.axis    = axis;
  }
}