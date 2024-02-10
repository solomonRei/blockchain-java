package blockchain.domain;

import java.io.Serializable;

public class Transaction implements Serializable {
  private static final long serialVersionUID = 1L;

  private final String from;
  private final String to;
  private final int amount;

  public Transaction(String from, String to, int amount) {
    this.from = from;
    this.to = to;
    this.amount = amount;
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

  public int getAmount() {
    return amount;
  }

  @Override
  public String toString() {
    return from + " sent " + amount + " VC to " + to;
  }
}
