package blockchain.domain;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class TransactionSimulator implements Runnable {
    private final BlockChain blockchain;
    private final List<String> users = List.of("Tom", "Alice", "Bob", "Nick", "miner");
    private final Supplier<Boolean> isRunning;

    public TransactionSimulator(BlockChain blockchain, Supplier<Boolean> isRunning) {
        this.blockchain = blockchain;
        this.isRunning = isRunning;
    }

    @Override
    public void run() {
        Random rand = new Random();
        while (isRunning.get()) {
            try {
                TimeUnit.SECONDS.sleep(rand.nextInt(4) + 1);
                String from = users.get(rand.nextInt(users.size()));
                String to;
                do {
                    to = users.get(rand.nextInt(users.size()));
                } while (from.equals(to));
                int amount = rand.nextInt(50) + 1;
                Transaction transaction = new Transaction(from, to, amount);
                    blockchain.addTransaction(transaction);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
