package net.chrotos.chrotoscloud.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionOrigin {
    PURCHASE(TransactionType.CREDIT),
    SALE(TransactionType.DEBIT),
    SEND(TransactionType.CREDIT),
    RECEIVE(TransactionType.DEBIT),
    WITHDRAW(TransactionType.CREDIT),
    DEPOSIT(TransactionType.DEBIT),
    SERVER_DEDUCT(TransactionType.CREDIT),
    SERVER_ADD(TransactionType.DEBIT);

    private final TransactionType transactionType;
}
