package jaque.interpreter;

import clojure.lang.PersistentVector;
import clojure.lang.Seqable;
import clojure.lang.ISeq;
import jaque.noun.*;

public final class Result implements Seqable {
  public Machine m;
  public Noun r;

  public Result(Machine m, Noun r) {
    this.m = m;
    this.r = r;
  }

  public Result(ISeq s) {
    this.m = (Machine) s.first();
    this.r = (Noun) s.next().first();
  }

  public ISeq seq() {
    return PersistentVector.create(m, r).seq();
  }
}
