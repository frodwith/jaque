package net.frodwith.jaque.data;

import java.time.Instant;

public class Time {
  public static Object fromInstant(Instant i) {
    int  micros  = i.getNano() / 1000;
    long seconds = 0x8000000cce9e0d80L + i.getEpochSecond(),
         fractos = Long.divideUnsigned((((long) micros) * 65536L), 1000000L) << 48;
    return Atom.mix(fractos, Atom.lsh((byte)0, 64, seconds));
  }
  
  public static Object now() {
    return fromInstant(Instant.now());
  }
}
