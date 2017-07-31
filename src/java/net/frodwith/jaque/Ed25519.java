package net.frodwith.jaque;

import jnr.ffi.LibraryLoader;
import jnr.ffi.types.size_t;

public class Ed25519 {
  public static interface Ed {
    void ed25519_create_keypair(byte[] publicKey, byte[] privateKey, byte[] seed);
    void ed25519_key_exchange(byte[] sharedSecret, byte[] publicKey, byte[] privateKey);
    void ed25519_sign(byte[] signature, byte[] message, @size_t int len, byte[] publicKey, byte[] privateKey);
    int  ed25519_verify(byte[] signature, byte[] message, @size_t int len, byte[] publicKey);
  }
  
  public static final Ed ed = LibraryLoader.create(Ed.class).load("ed25519");
}
