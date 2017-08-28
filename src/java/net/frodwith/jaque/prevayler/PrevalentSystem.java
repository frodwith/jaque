package net.frodwith.jaque.prevayler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.function.Consumer;

import net.frodwith.jaque.Caller;
import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.truffle.Context;

public class PrevalentSystem implements Serializable, Caller {
  public Object arvo;
  public Object now;
  public Object wen;
  public Object sev;
  public Object sen;
  public Context context;
  public Consumer<Object> slogSink, tankSink;
  
  public PrevalentSystem(Context context, Consumer<Object> slogSink, Consumer<Object> tankSink) {
    this.context = context;
    this.slogSink = slogSink;
    this.tankSink = tankSink;
    context.caller = this;
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeObject(arvo);
    out.writeObject(now);
    out.writeObject(wen);
    out.writeObject(sev);
    out.writeObject(sen);
    out.writeObject(context.locations);
  }
  
  @SuppressWarnings("unchecked")
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    this.arvo = in.readObject();
    this.now = in.readObject();
    this.wen = in.readObject();
    this.sev = in.readObject();
    this.sen = in.readObject();
    this.context.locations.clear();
    this.context.locations.putAll((Map<Cell, Location>) in.readObject());
  }

  public Cell axisGate(Object axis) {
    return Cell.expect(context.nock(arvo, new Qual(9L, axis, 0L, 1L).toCell()));
  }
  
  @Override
  public Object kernel(String gateName, Object sample) {
    Cell wishGate = axisGate(20L);
    Object cord = Atom.stringToCord(gateName);
    Cell targetGate = Cell.expect(context.wrapSlam(wishGate, cord));
    return context.wrapSlam(targetGate, sample);
  }
  
  @Override
  public void slog(Object tank) {
    this.slogSink.accept(tank);
  }
}
