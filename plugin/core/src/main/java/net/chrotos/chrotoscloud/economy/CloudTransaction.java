package net.chrotos.chrotoscloud.economy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "transactions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@SelectBeforeUpdate
public class CloudTransaction implements Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(updatable = false, nullable = false)
    private Long entryNo;

    private String transactionCode;

    @Enumerated(EnumType.STRING)
    @NonNull
    @Column(insertable = false, updatable = false, nullable = false)
    @JsonIgnore
    private AccountType accountType;

    @ManyToOne(targetEntity = CloudAccount.class)
    @JoinColumn(name = "accountId", referencedColumnName = "uniqueId", nullable = false)
    @JoinColumn(name = "accountType", referencedColumnName = "accountType", nullable = false)
    @JsonIgnore
    private Account account = null;

    @NonNull
    @Column(insertable = false, updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private UUID accountId;

    @Type(type = "uuid-char")
    private UUID fromUniqueId;
    @Type(type = "uuid-char")
    private UUID toUniqueId;

    @Enumerated(EnumType.STRING)
    @NonNull
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @NonNull
    @Column(nullable = false)
    private TransactionOrigin origin;

    @Column(nullable = false)
    private float amount;
    @Column(nullable = false)
    private float absolute;
    @Column(nullable = false)
    private boolean positive;

    @NonNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public CloudTransaction(@NonNull String transactionCode, @NonNull AccountType accountType,@NonNull UUID accountId,
                            UUID fromUniqueId, UUID toUniqueId, @NonNull TransactionType type,
                            @NonNull TransactionOrigin origin, float amount, float absolute,
                            boolean positive, @NonNull LocalDateTime createdAt) {
        this.transactionCode = transactionCode;
        this.accountType = accountType;
        this.accountId = accountId;
        this.fromUniqueId = fromUniqueId;
        this.toUniqueId = toUniqueId;
        this.type = type;
        this.origin = origin;
        this.amount = amount;
        this.absolute = absolute;
        this.positive = positive;
        this.createdAt = createdAt;
    }
}
