package net.chrotos.chrotoscloud.economy;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

@Entity(name = "accounts")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CloudAccount implements Account, SoftDeletable {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @ManyToOne(targetEntity = CloudPlayer.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @NonNull
    private Player owner;

    @Enumerated(EnumType.STRING)
    @NonNull
    private AccountType accountType;
    private float balance;
    private float balanceLimit;
    private float creditLimit;
    private boolean sharedAccount;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt = Calendar.getInstance();
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updatedAt = Calendar.getInstance();
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar deletedAt;

    @Override
    public Collection<Transaction> getTransactions() {
        return null;
    }

    @Override
    public Collection<UUID> getSharedWith() {
        return null;
    }
}
