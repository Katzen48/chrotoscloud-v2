package net.chrotos.chrotoscloud.jobs;

import lombok.*;
import lombok.experimental.Accessors;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.persistence.DatabaseTransaction;

import java.time.Duration;
import java.util.Calendar;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public abstract class AbstractJob {
    private final UUID uniqueId = UUID.randomUUID();
    @Setter(AccessLevel.PACKAGE)
    private JobState state;
    private boolean async = true;
    private Duration timeout = Duration.ofMinutes(10);
    @Accessors(fluent = true)
    private boolean requiresTransaction = false;
    private int retries = 3;
    @Accessors(fluent = true)
    private Calendar notBefore;
    @Accessors(fluent = true)
    private Calendar notAfter;

    public abstract void handle(DatabaseTransaction transaction);

    public final void queue() {
        Cloud.getInstance().getJobManager().queue(this);
    }
}
