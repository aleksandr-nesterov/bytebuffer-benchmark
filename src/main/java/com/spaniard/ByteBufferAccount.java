package com.spaniard;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteBufferAccount {

    private static final int TRANSACTIONS = 1_000_000;

    private final Transaction flyweight = new Transaction();
    private final ByteBuffer byteBuffer;
    private int transactionCount;

    public ByteBufferAccount(boolean direct) {
        this.byteBuffer = direct
                ? ByteBuffer.allocateDirect(TRANSACTIONS * flyweight.getObjectSize())
                : ByteBuffer.allocate(TRANSACTIONS * flyweight.getObjectSize());
        this.byteBuffer.order(ByteOrder.nativeOrder());
    }

    public void register(final int price, final int amount, final boolean income) {
        flyweight.setObjectOffset(transactionCount++ * flyweight.getObjectSize());
        flyweight.setPrice(price);
        flyweight.setAmount(amount);
        flyweight.setIncome(income);
    }

    public int balance() {
        int balance = 0;
        int index = 0;
        while (index < transactionCount) {
            flyweight.setObjectOffset(index++ * flyweight.getObjectSize());
            balance += flyweight.getPrice() * flyweight.getAmount() * (flyweight.isIncome() ? 1 : -1);
        }
        return balance;
    }

    public void free() {
        byteBuffer.clear();
    }

    private class Transaction {
        private int offset = 0;

        private final int priceOffset = offset += 0;
        private final int amountOffset = offset += 4;
        private final int incomeOffset = offset += 4;

        private final int objectSize = offset += 1;

        private int objectOffset;

        public int getObjectSize() {
            return objectSize;
        }

        public void setObjectOffset(int objectOffset) {
            this.objectOffset = objectOffset;
        }

        public void setPrice(int price) {
            byteBuffer.putInt(objectOffset + priceOffset, price);
        }

        public int getPrice() {
            return byteBuffer.getInt(objectOffset + priceOffset);
        }

        public void setAmount(int amount) {
            byteBuffer.putInt(objectOffset + amountOffset, amount);
        }

        public int getAmount() {
            return byteBuffer.getInt(objectOffset + amountOffset);
        }

        public void setIncome(boolean income) {
            byteBuffer.put(objectOffset + incomeOffset, (byte) (income ? 1 : -1));
        }

        public boolean isIncome() {
            return byteBuffer.get(objectOffset + incomeOffset) == 1;
        }

    }

}
