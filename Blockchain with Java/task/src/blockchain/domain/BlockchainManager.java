package blockchain.domain;

import blockchain.utils.BlockchainValidator;
import blockchain.utils.SerializationUtils;
import java.util.ArrayList;
import java.util.List;

public class BlockchainManager {

  BlockChain blockchain;
  private volatile boolean isRunning = true;

  public BlockchainManager(BlockChain blockchain) {
    this.blockchain = blockchain;
  }

  public void startBlockchain(int nbOfThreads, int nbOfBlocksToAdd) {

    int totalNbOfBlocks = nbOfBlocksToAdd + blockchain.getNbOfBlocks();

    List<Thread> threads = new ArrayList<>(nbOfThreads);

    for (int i = 0; i < nbOfThreads; i++) {
      threads.add(new Thread(new Miner(blockchain, totalNbOfBlocks), "#" + (i + 1)));
      threads.get(i).start();
    }

    startTransactionSimulator();

    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    isRunning = false;
  }


  private void startTransactionSimulator() {
    TransactionSimulator transactionSimulator = new TransactionSimulator(blockchain, () -> isRunning);
    Thread transactionThread = new Thread(transactionSimulator);
    transactionThread.start();
  }

  public static BlockChain retrieveOrCreateBlockchain(String fileName) {
    BlockChain blockchain;

    try {
      blockchain = (BlockChain) SerializationUtils.deserialize(fileName);
      System.out.println("Retrieving existing Blockchain");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println("Creating new Blockchain");
      blockchain = BlockChain.getInstance();
    }

    if (!BlockchainValidator.isBlockchainValid()) {
      System.out.println("Invalid blockchain! Creating new one");
      blockchain = BlockChain.getInstance();
    }

    return blockchain;
  }
}
