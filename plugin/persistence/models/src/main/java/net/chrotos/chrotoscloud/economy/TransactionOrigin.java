package net.chrotos.chrotoscloud.economy;

import lombok.Getter;

@Getter
public enum TransactionOrigin {
    PURCHASE,
    SALE,
    SEND,
    RECEIVE,
    WITHDRAW,
    DEPOSIT,
    SERVER_DEDUCT,
    SERVER_ADD;

    private TransactionType transactionType;
    private TransactionOrigin inverse;

    static {
        // Transaction Types
        PURCHASE.transactionType = TransactionType.CREDIT;
        SALE.transactionType = TransactionType.DEBIT;
        SEND.transactionType = TransactionType.CREDIT;
        RECEIVE.transactionType = TransactionType.DEBIT;
        WITHDRAW.transactionType = TransactionType.CREDIT;
        DEPOSIT.transactionType = TransactionType.DEBIT;
        SERVER_DEDUCT.transactionType = TransactionType.CREDIT;
        SERVER_ADD.transactionType = TransactionType.DEBIT;

        PURCHASE.inverse = SALE;
        SALE.inverse = PURCHASE;
        SEND.inverse = RECEIVE;
        RECEIVE.inverse = SEND;
        WITHDRAW.inverse = DEPOSIT;
        DEPOSIT.inverse = WITHDRAW;
    }
}
