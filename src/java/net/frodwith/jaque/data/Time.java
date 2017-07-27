package net.frodwith.jaque.data;

import java.util.Date;

public class Time {
  public static long fromDate(Date d) {
    return 0x8000000cce9e0d80L + d.getTime();
  }
  
  public static long now() {
    return fromDate(new Date());
  }
}
