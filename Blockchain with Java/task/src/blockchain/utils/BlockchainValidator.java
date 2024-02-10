package blockchain.utils;

import blockchain.domain.Block;
import blockchain.domain.BlockChain;
import blockchain.domain.Message;

import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;

public class BlockchainValidator {

  private BlockchainValidator() {}

  public static boolean isBlockchainValid() {
    BlockChain blockchain = BlockChain.getInstance();
    List<Block> blocks = blockchain.getBlocks();

    if (isBlockchainEmpty()) {
      return true;
    }
    if (!isFirstBlockValid(blocks.get(0))) {
      return false;
    }
    for (int i = 1; i < blocks.size(); i++) {
      if (hasWrongPreviousHash(blocks.get(i), blocks.get(i - 1))) {
        return false;
      }
      if (hasOneOfTheMessagesAnInvalidId(blocks.get(i), i - 1)) {
        return false;
      }
      if (hasOneOfTheMessagesAnInvalidSignature(blocks.get(i))) {
        return false;
      }
    }
    return true;
  }

  public static boolean isNewBlockValid(Block block) {
    if (isBlockchainEmpty()) {
      return isFirstBlockValid(block);
    }
    Block lastBlock = BlockChain.getInstance().getLastBlock();
    if (hasWrongPreviousHash(block, lastBlock)) {
      return false;
    }
    int indexOfLastBlock = BlockChain.getInstance().getNbOfBlocks() - 1;
    if (hasOneOfTheMessagesAnInvalidId(block, indexOfLastBlock)) {
      return false;
    }
    if (hasOneOfTheMessagesAnInvalidSignature(block)) {
      return false;
    }
    if (hasWrongNumberOfZerosInHash(block)) {
      return false;
    }
    return true;
  }

  private static boolean isBlockchainEmpty() {
    List<Block> blocks = BlockChain.getInstance().getBlocks();
    return blocks.size() == 0;
  }

  private static boolean isFirstBlockValid(Block block) {
    return "0".equals(block.getHashOfPreviousBlock());
  }

  private static boolean hasOneOfTheMessagesAnInvalidId(Block block, int indexOfPreviousBlock) {
    if (indexOfPreviousBlock == 0) {
      return false;
    }
    List<Block> blocks = BlockChain.getInstance().getBlocks();
    OptionalLong previousBlockMax =
        blocks.get(indexOfPreviousBlock).getMessages().stream()
            .map(Message::getId)
            .mapToLong(l -> l)
            .max();
    if (previousBlockMax.isPresent()) {
      return block.getMessages().stream()
          .anyMatch(message -> message.getId() <= previousBlockMax.getAsLong());
    }
    return hasOneOfTheMessagesAnInvalidId(block, indexOfPreviousBlock - 1);
  }

  private static boolean hasWrongPreviousHash(Block block, Block previousBlock) {
    return !Objects.equals(previousBlock.getHash(), block.getHashOfPreviousBlock());
  }

  private static boolean hasOneOfTheMessagesAnInvalidSignature(Block block) {
    return block.getMessages().stream().anyMatch(message -> !SignatureChecker.check(message));
  }

  private static boolean hasWrongNumberOfZerosInHash(Block block) {
    long nbOfZeros = BlockChain.getInstance().getNbOfZeros();
    String pattern = String.format("0{%s}.*", nbOfZeros);
    return !block.getHash().matches(pattern);
  }
}
