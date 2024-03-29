package blockchain.domain;

import blockchain.utils.HashCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Miner implements Runnable {

  private final BlockChain blockchain;
  private final int nbOfBlocks;
  private int currentNbOfBlocks;

  public Miner(BlockChain blockchain, int nbOfBlocks) {
    this.blockchain = blockchain;
    this.nbOfBlocks = nbOfBlocks;
  }

  private Block generateBlock(
      int id, String hashOfPreviousBlock, List<Message> messages, long nbOfZeros) {
    List<Transaction> transactions = collectValidTransactions();
    Random rand = new Random();
    int magicNumber = rand.nextInt(Integer.MAX_VALUE);
    long timestamp = System.currentTimeMillis();
    String minerId = Thread.currentThread().getName();
    String pattern = String.format("0{%s}.*", nbOfZeros);
    String hash =
        HashCreator.createHash(hashOfPreviousBlock, id, timestamp, magicNumber, messages, minerId);
    while (!hash.matches(pattern)) {
      if (blockHasBeenAdded()) return null;
      magicNumber = rand.nextInt(Integer.MAX_VALUE);
      hash =
          HashCreator.createHash(
              hashOfPreviousBlock, id, timestamp, magicNumber, messages, minerId);
    }
    int generationDuration = (int) (System.currentTimeMillis() - timestamp) / 1000;
    return new Block(
        id,
        hashOfPreviousBlock,
        timestamp,
        magicNumber,
        generationDuration,
        messages,
        minerId,
        transactions);
  }

  private boolean blockHasBeenAdded() {
    return blockchain.getNbOfBlocks() > currentNbOfBlocks;
  }

  private List<Transaction> collectValidTransactions() {
    return new ArrayList<>();
  }

  @Override
  public void run() {
    currentNbOfBlocks = blockchain.getNbOfBlocks();
    while (currentNbOfBlocks < nbOfBlocks) {
      long nbOfZeros = blockchain.getNbOfZeros();
      Block lastBlock = blockchain.getLastBlock();
      List<Message> messages = blockchain.getNewMessages();
      String previousHash = currentNbOfBlocks > 0 ? lastBlock.getHash() : "0";
      int id = currentNbOfBlocks > 0 ? lastBlock.getId() + 1 : 1;
      Block block = generateBlock(id, previousHash, messages, nbOfZeros);
      if (block != null) {
        synchronized (Miner.class) {
          blockchain.addBlock(block);
        }
      }
      currentNbOfBlocks = blockchain.getNbOfBlocks();
    }
  }
}
