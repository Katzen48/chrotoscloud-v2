package net.chrotos.chrotoscloud.economy;

import lombok.Data;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

@Entity(name = "account")
@Data
public class CloudAccount implements Account, SoftDeletable {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uniqueId;
    private UUID ownerId;

    @ManyToOne(targetEntity = CloudPlayer.class)
    @JoinColumn(name = "owner_id")
    private Player owner;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private float balance;
    private float limit;
    private float creditLimit;
    private boolean sharedAccount;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updatedAt;
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
