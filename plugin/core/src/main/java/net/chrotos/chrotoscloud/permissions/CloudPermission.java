package net.chrotos.chrotoscloud.permissions;

import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.UUID;

@Entity(name = "permissions")
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class CloudPermission implements Permission {
    @Id
    @Column(updatable = false, nullable = false, name = "unique_id")
    @NonNull
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @Column(updatable = false, nullable = false)
    @NonNull
    String name;

    boolean value = false;

    @Any(metaColumn = @Column(name = "permissible_type"),
            metaDef = "PermissionMetaDef", optional = false)
    @JoinColumn(name = "permissible", updatable = false)
    @NonNull
    private Permissible permissible;

    @Override
    public boolean getValue() {
        return this.value;
    }
}
