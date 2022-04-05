package net.chrotos.chrotoscloud.economy;

import lombok.*;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.permissions.CloudPermission;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity(name = "accounts")
@Data
@NoArgsConstructor
@DynamicUpdate
@SQLDelete(sql = "UPDATE accounts SET deleted_at=now() WHERE unique_id = ?")
@Where(clause = "deleted_at IS NUlL")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CloudAccount implements Account, SoftDeletable {
    @EmbeddedId
    private AccountKey key;

    @ManyToOne(targetEntity = CloudPlayer.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_unique_id", updatable = false)
    @NonNull
    private AccountHolder owner;

    private float balance;
    private float balanceLimit;
    private float creditLimit;
    private boolean sharedAccount = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt = Calendar.getInstance();
    @Temporal(TemporalType.TIMESTAMP)
    @Version
    private Calendar updatedAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar deletedAt;

    @Setter
    private transient long lastRefreshed;

    @OneToMany(targetEntity = CloudTransaction.class, cascade = CascadeType.ALL, mappedBy = "account")
    private List<Transaction> transactions;

    public CloudAccount(AccountHolder owner, AccountType accountType) {
        super();
        key = new AccountKey(UUID.randomUUID(), accountType);
    }

    @Override
    public void addTransaction(TransactionOrigin origin, float amount) {
        addTransaction(origin, amount, LocalDateTime.now());
    }

    @Override
    public void addTransaction(TransactionOrigin origin, float amount, Account source) {
        addTransaction(origin, amount, LocalDateTime.now(), source);
    }

    @Override
    public void addTransaction(TransactionOrigin origin, float amount, LocalDateTime createdAt) {
        addTransaction(origin, amount, createdAt, UUID.randomUUID().toString());
    }

    @Override
    public void addTransaction(TransactionOrigin origin, float amount, LocalDateTime createdAt, Account source) {
        addTransaction(origin, amount, createdAt, UUID.randomUUID().toString(), source);
    }

    @Override
    public void addTransaction(TransactionOrigin origin, float amount, LocalDateTime createdAt, String transactionCode) {
        addTransaction(origin, amount, createdAt, transactionCode, null);
    }

    @Override
    public void addTransaction(TransactionOrigin origin, float amount, LocalDateTime createdAt, String transactionCode, Account source) {
        Cloud.getInstance().getPersistence().runInTransaction(databaseTransaction -> {
            if ((origin.getInverse() == null) != (source == null)) {
                if (source == null) {
                    throw new AssertionError("Transaction source must not be null for transaction origin " +
                                                origin.name());
                } else {
                    throw new AssertionError("Transaction source must be null for transaction origin " +
                                                origin.name());
                }
            }

            float signedAmount = amount * origin.getTransactionType().getSign();

            addTransaction(new CloudTransaction(
                    transactionCode,
                    getAccountType(),
                    getUniqueId(),
                    source != null ? source.getUniqueId() : null,
                    getUniqueId(),
                    origin.getTransactionType(),
                    origin,
                    signedAmount,
                    Math.abs(amount),
                    signedAmount >= 0,
                    createdAt
            ));

            if (source != null) {
                TransactionOrigin inverseOrigin = origin.getInverse();
                float inverseAmount = amount * inverseOrigin.getTransactionType().getSign();

                if ((inverseAmount * -1) != signedAmount) {
                    throw new AssertionError("Balanced Transaction must not have the same sign");
                }

                source.addTransaction(new CloudTransaction(
                        transactionCode,
                        source.getAccountType(),
                        source.getUniqueId(),
                        this.getUniqueId(),
                        source.getUniqueId(),
                        inverseOrigin.getTransactionType(),
                        inverseOrigin,
                        inverseAmount,
                        Math.abs(amount),
                        inverseAmount >= 0,
                        createdAt
                ));
            }
        });
    }

    @Override
    public void addTransaction(Transaction transaction) {
        Cloud.getInstance().getPersistence().runInTransaction(databaseTransaction -> {
            checkTransaction(transaction);

            Cloud.getInstance().getPersistence().save(transaction);
            balance += transaction.getAmount();
            Cloud.getInstance().getPersistence().merge(this);
        });
    }

    @Override
    public UUID getUniqueId() {
        return key.getUniqueId();
    }

    @Override
    public AccountType getAccountType() {
        return key.getAccountType();
    }

    private void checkTransaction(Transaction transaction) {
        if (transaction.getAccount() != this) {
            throw new AssertionError("Transaction account must be this");
        }

        if (transaction.getAccountId() != getUniqueId()) {
            throw new AssertionError("Transaction account id must be " + getUniqueId());
        }

        if (transaction.getAbsolute() != Math.abs(transaction.getAmount())) {
            throw new AssertionError("Transaction absolute must be " + Math.abs(transaction.getAmount()));
        }

        if (transaction.isPositive() != (transaction.getAmount() > 0)) {
            throw new AssertionError("Transaction positive must be " + (transaction.getAmount() > 0 ?
                                        "true" : "false")
            );
        }
    }
}
