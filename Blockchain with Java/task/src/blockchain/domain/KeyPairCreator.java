package blockchain.domain;

import java.security.*;

public class KeyPairCreator {

  private KeyPairCreator() {}

  public static KeyPair createKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

    SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
    keyGen.initialize(1024, random);

    return keyGen.generateKeyPair();
  }
}
