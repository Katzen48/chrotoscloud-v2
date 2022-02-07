package net.chrotos.chrotoscloud.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountKey implements Serializable {
    @Column(nullable = false)
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NonNull
    private AccountType accountType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountKey that = (AccountKey) o;
        return Objects.equals(uniqueId, that.uniqueId) && accountType == that.accountType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId, accountType);
    }
}
