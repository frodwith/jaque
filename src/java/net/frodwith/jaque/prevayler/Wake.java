package net.frodwith.jaque.prevayler;

import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;

import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Time;
import net.frodwith.jaque.truffle.Context;

public class Wake implements TransactionWithQuery<PrevalentSystem,Boolean> {
  public transient Context context;
  public transient Consumer<Object> effectSink;
  public transient Consumer<Object> slogSink;

  public Wake(Context context, Consumer<Object> slogSink, Consumer<Object> effectSink) {
    this.context = context;
    this.slogSink = slogSink;
    this.effectSink = effectSink;
  }
  
  @Override
  public Boolean executeAndQuery(PrevalentSystem s, Date now) {
    if ( null == context ) {
      return false;
    }
    Map<Cell,Location> loaded = s.locations;
    context.caller = s;
    s.context = context;
    s.effectSink = effectSink;
    s.slogSink = slogSink;
    s.restoreLocations(loaded);
    return (null == s.arvo);
  }

}
