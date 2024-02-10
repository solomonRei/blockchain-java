package blockchain.domain;

import java.security.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class BotUser {

  private static final Map<String, BotUser> users = new HashMap<>();

  private final String name;
  private final BlockChain blockchain;
  private long balance;
  private PrivateKey privateKey;
  private PublicKey publicKey;

  private BotUser(String name) {
    this.name = name;
    this.balance = 100;
    this.blockchain = BlockChain.getInstance();
    users.put(name, this);
    createKeyPair();
  }

  public void createKeyPair() {
    try {
      KeyPair keyPair = KeyPairCreator.createKeyPair();
      privateKey = keyPair.getPrivate();
      publicKey = keyPair.getPublic();
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
      e.printStackTrace();
    }
  }

  public void sendMessage(String textMessage) {
    byte[] signature;
    try {
      LocalDateTime dateTime = LocalDateTime.now();
      long id = blockchain.getNewDataId();
      signature = signMessage(name + textMessage + dateTime.toString() + id);
      Message message = new Message(name, textMessage, dateTime, id, signature, publicKey);
      blockchain.addMessage(message);
    } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
      e.printStackTrace();
    }
  }


  private byte[] signMessage(String data)
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    Signature rsa = Signature.getInstance("SHA1withRSA");
    rsa.initSign(privateKey);
    rsa.update(data.getBytes());
    return rsa.sign();
  }
}
