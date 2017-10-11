package net.frodwith.jaque.data;

import java.time.Duration;
import java.time.Instant;

public class Time {
  private static long EPOCH_DIFF = 0x8000000cce9e0d80L;

  public static Object fromInstant(Instant i) {
    int  micros  = i.getNano() / 1000;
    long seconds = EPOCH_DIFF + i.getEpochSecond(),
         fractos = Long.divideUnsigned((((long) micros) * 65536L), 1000000L) << 48;
    return Atom.mix(fractos, Atom.lsh((byte)0, 64, seconds));
  }
  
  public static Object now() {
    return fromInstant(Instant.now());
  }
  
  /* we round up because because some time is closer than no time when there are fractos */
  private static long fractosToMillis(long frac) {
    long num = (frac >>> 48) * 1000,
         div = 65536L,
         truc = num / div;
    return ( num % div == 0 )
        ? truc
        : truc + 1;
  }
  
  /*
  public static Instant toInstant(Object atom) {
    long fractos = Atom.expectLong(Atom.cut((byte) 6, 0L, 1L, atom)),
         seconds = Atom.expectLong(Atom.cut((byte) 6, 1L, 1L, atom)),
         epoch   = seconds - EPOCH_DIFF,
         micros  = ((fractos >> 48) * 1000000L) / 65536L;
    return Instant.ofEpochSecond(epoch, micros * 1000);
  }
  */
  
  public static long gapMs(Object now, Object wen) {
    if ( -1 == Atom.compare(now(), wen) ) {
      Object dif = Atom.sub(wen,  now);
      long fra = Atom.expectLong(Atom.cut((byte) 6, 0L, 1L, dif)),
           sec = Atom.expectLong(Atom.cut((byte) 6, 1L, 1L, dif));
      return (sec * 1000) + fractosToMillis(fra);
    }
    else {
      return 0;
    }
  }
}
