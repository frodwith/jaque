package net.frodwith.jaque.data;

import com.oracle.truffle.api.object.ObjectType;

public class JaqueObjectType extends ObjectType {
  public static JaqueObjectType INSTANCE = new JaqueObjectType();
  
  private JaqueObjectType() {
  }
}
