package jaque.truffle;

import jaque.interpreter.Machine;
import jaque.noun.Noun;

public final class Environment {
  public final Machine machine;
  public final Noun    subject;

  public Environment(Machine machine, Noun subject) {
    this.machine = machine;
    this.subject = subject;
  }
}
