package blockchain;

import blockchain.domain.BlockChain;
import blockchain.domain.BlockchainManager;
import blockchain.domain.TransactionSimulator;
import blockchain.utils.BlockchainValidator;

public class Main {
  public static String fileName = "blockchain.data";

  public static void main(String[] args) {

    BlockChain blockchain = BlockchainManager.retrieveOrCreateBlockchain(fileName);
    blockchain.setFileName(fileName);

    BlockchainManager blockchainManager = new BlockchainManager(blockchain);

    int nbOfBlocksToAdd = 15;
    int nbOfThreads = 6;

    System.out.printf("Adding %d blocks...%n", nbOfBlocksToAdd);
    blockchainManager.startBlockchain(nbOfThreads, nbOfBlocksToAdd);
    System.out.println("Is blockchain valid? " + BlockchainValidator.isBlockchainValid());

    System.out.println();
    System.out.println(blockchain.toString());
  }
}

