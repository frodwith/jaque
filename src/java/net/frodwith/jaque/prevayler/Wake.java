package net.frodwith.jaque.prevayler;

import java.util.Date;
import java.util.function.Consumer;

import org.prevayler.TransactionWithQuery;

import net.frodwith.jaque.truffle.driver.Arm;

public class Wake implements TransactionWithQuery<PrevalentSystem,Boolean> {
  public Arm[] arms;
  public transient Consumer<Object> slogSink;
  public transient Consumer<Object> effectSink;
  public boolean profile;

  public Wake(Arm[] arms, Consumer<Object> slogSink, Consumer<Object> effectSink, boolean profile) {
    this.arms = arms;
    this.slogSink = slogSink;
    this.effectSink = effectSink;
    this.profile = profile;
  }
  
  @Override
  public Boolean executeAndQuery(PrevalentSystem s, Date now) {
    if ( null == slogSink ) {
      profile = false;
    }
    s.slogSink = slogSink;
    s.effectSink = effectSink;
    s.context.wake(arms, s, profile);
    return (null == s.arvo);
  }
}
