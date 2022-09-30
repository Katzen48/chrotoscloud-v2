package net.chrotos.chrotoscloud.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import lombok.*;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.economy.Account;
import net.chrotos.chrotoscloud.economy.AccountType;
import net.chrotos.chrotoscloud.economy.CloudAccount;
import net.chrotos.chrotoscloud.games.events.PlayerKickedEvent;
import net.chrotos.chrotoscloud.games.states.CloudGameState;
import net.chrotos.chrotoscloud.games.states.GameState;
import net.chrotos.chrotoscloud.games.stats.CloudGameStatistic;
import net.chrotos.chrotoscloud.games.stats.GameStatistic;
import net.chrotos.chrotoscloud.permissions.*;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.*;

@Entity(name = "players")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@DynamicUpdate
@SQLDelete(sql = "UPDATE players SET deleted_at=now() WHERE unique_id = ? AND updated_at = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
@SelectBeforeUpdate
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CloudPlayer extends CloudPermissible implements Player, SoftDeletable {
    @Id
    @Column(updatable = false, nullable = false)
    @NonNull
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @NonNull
    private String name;

    @Setter
    @JsonIgnore
    private transient SidedPlayer sidedPlayer;

    @JsonIgnore
    private transient CityResponse cityResponse;

    @OneToMany(mappedBy = "owner", targetEntity = CloudAccount.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @Filter(name = "accountType")
    @Filter(name = "uniqueId")
    @NonNull
    @JsonIgnore
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "player", targetEntity = CloudBan.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    @Filter(name = "uniqueId")
    @Filter(name = "active")
    @NonNull
    @JsonIgnore
    private Set<Ban> bans = new HashSet<>();

    @OneToMany(targetEntity = CloudPermission.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "permissible_unique_id")
    @Where(clause = "permissible_type='player'")
    @NonNull
    @JsonIgnore
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Permission> permissions = new HashSet<>();

    @Setter
    @ManyToOne(targetEntity = CloudRank.class)
    @JoinColumn(name = "rank_unique_id", updatable = false)
    private Rank rank;

    @OneToMany(mappedBy = "player", targetEntity = CloudGameStatistic.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @Filter(name = "gameMode")
    @NonNull
    @JsonIgnore
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<GameStatistic> stats = new HashSet<>();

    @OneToMany(mappedBy = "player", targetEntity = CloudGameState.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @Filter(name = "gameMode")
    @Filter(name = "name")
    @NonNull
    @JsonIgnore
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<GameState> states = new HashSet<>();

    @OneToMany(mappedBy = "player", targetEntity = CloudPlayerInventory.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @Filter(name = "gameMode")
    @NonNull
    @JsonIgnore
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<PlayerInventory> inventories = new HashSet<>();

    @CreationTimestamp
    private Calendar createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Version
    private Calendar updatedAt;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Calendar deletedAt;

    @Override
    public Collection<? extends Account> getAccounts(AccountType type) {
        return Cloud.getInstance().getPersistence()
                .executeFiltered("accountType", Collections.singletonMap("type", type),
                        () -> getAccounts().stream().toList());
    }

    @Override
    public Account getAccount(UUID uniqueId) {
        return Cloud.getInstance().getPersistence()
                .executeFiltered("uniqueId", Collections.singletonMap("uniqueid", uniqueId),
                        () -> getAccounts().stream().findFirst().orElse(null));
    }

    @Override
    @NonNull
    public Optional<Permission> getPermissionExact(@NonNull String permission) {
        Optional<Permission> optionalPermission = super.getPermissionExact(permission);

        return optionalPermission.isPresent() || getRank() == null ? optionalPermission : getRank().getPermissionExact(permission);
    }

    @Override
    @NonNull
    @JsonIgnore
    public Component getPrefixes() {
        return Cloud.getInstance().getChatManager().getPrefixes(this);
    }

    @Override
    @NonNull
    public Collection<? extends GameStatistic> getStats(@NonNull String gameMode) {
        return Cloud.getInstance().getPersistence()
                .executeFiltered("gameMode",
                        Collections.singletonMap("gameMode", gameMode), () -> getStats().stream().toList());
    }

    @Override
    @NonNull
    public Collection<? extends GameState> getStates(@NonNull String gameMode) {
        return Cloud.getInstance().getPersistence()
                .executeFiltered("gameMode",
                        Collections.singletonMap("gameMode", gameMode), () -> getStates().stream().toList());
    }

    @Override
    @NonNull
    public Collection<? extends GameState> getStatesByName(@NonNull String name) {
        return Cloud.getInstance().getPersistence()
                .executeFiltered("name",
                        Collections.singletonMap("name", name), () -> getStates().stream().toList());
    }

    @Override
    public PlayerInventory getInventory(@NonNull String gameMode) {
        return Cloud.getInstance().getPersistence()
                .executeFiltered("gameMode",
                        Collections.singletonMap("gameMode", gameMode),
                        () -> getInventories().stream().findFirst().orElse(null));
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash) {
        sidedPlayer.setResourcePack(url, hash);
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash, boolean required) {
        sidedPlayer.setResourcePack(url, hash, required);
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash, boolean required, @NonNull TextComponent prompt) {
        sidedPlayer.setResourcePack(url, hash, required, prompt);
    }

    @Override
    @JsonIgnore
    public TimeZone getTimeZone() {
        return getTimeZone(null);
    }

    @Override
    public TimeZone getTimeZone(Locale locale) {
        CityResponse city = getCityResponse();

        String timeZoneString = city != null ? city.getLocation().getTimeZone() : getTimeZoneString(locale);
        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(timeZoneString);
        } catch (ZoneRulesException e) {
            e.printStackTrace();
            zoneId = ZoneId.of("UTC");
        }

        return TimeZone.getTimeZone(zoneId);
    }

    private String getTimeZoneString(Locale locale) {
        if (Locale.GERMAN.equals(locale) || Locale.GERMANY.equals(locale)) {
            return "Europe/Berlin";
        } else if (Locale.UK.equals(locale)) {
            return "Europe/London";
        } else if (Locale.US.equals(locale)) {
            return "America/New_York";
        } else if (Locale.CANADA.equals(locale)) {
            return "America/Toronto";
        } else if (Locale.CHINA.equals(locale) || Locale.CHINESE.equals(locale) || Locale.SIMPLIFIED_CHINESE.equals(locale) || Locale.TRADITIONAL_CHINESE.equals(locale)) {
            return "Asia/Shanghai";
        } else {
            return TimeZone.getDefault().getID();
        }
    }

    @Override
    public Locale getLocale() {
        if (getSidedPlayer() == null) {
            return Locale.US;
        }

        if (getSidedPlayer().getLocale() != null) {
            return getSidedPlayer().getLocale();
        }

        Locale foundLocale = null;
        CityResponse city = getCity();
        if (city != null && !city.getCountry().getIsoCode().isBlank()) {
            String isoCode = city.getCountry().getIsoCode();

            if ((foundLocale = getValidLocale(isoCode, isoCode)) == null
                    && (foundLocale = getValidLocale(isoCode.toLowerCase() + "_" + isoCode.toUpperCase(), isoCode)) == null) {
                Optional<Locale> optional = Arrays.stream(Locale.getAvailableLocales())
                        .filter(locale -> {
                            try {
                                return locale.getCountry().equalsIgnoreCase(isoCode);
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .findFirst();

                if (optional.isPresent()) {
                    foundLocale = optional.get();
                }
            }
        }

        if (foundLocale == null) {
            foundLocale = Locale.US;
        }

        return foundLocale;
    }

    @JsonIgnore
    private Locale getValidLocale(@NonNull String key, @NonNull String countryIsoCode) {
        Locale locale = Locale.forLanguageTag(key);
        if (locale.getCountry().isBlank() || locale.getCountry().equalsIgnoreCase(countryIsoCode)) {
            return locale;
        }

        return null;
    }

    @Override
    @JsonProperty("ban")
    public Ban getActiveBan() {
        return Cloud.getInstance().getPersistence().executeFiltered("active", Collections.emptyMap(),
                () -> getBans().stream().findFirst().orElse(null));
    }

    @Override
    @JsonIgnore
    public boolean isBanned() {
        return getActiveBan() != null;
    }

    @Override
    public Ban ban(@NonNull String reason) {
        return ban(reason, null);
    }

    @Override
    public Ban ban(@NonNull String reason, Calendar expiresAt) {
        Ban ban = new CloudBan(UUID.randomUUID(), this, reason, Calendar.getInstance(), expiresAt);
        Cloud.getInstance().getPersistence().runInTransaction((transaction) -> {
            bans.add(ban);
        });

        if (getSidedPlayer() != null) {
            kick(ban.getBanMessage(getLocale()));
        } else {
            Cloud.getInstance().getQueue().publish("games.server.kick", new PlayerKickedEvent(getUniqueId(),
                    LegacyComponentSerializer.builder().build().serialize(ban.getBanMessage(Locale.US))));
        }

        return ban;
    }

    @Override
    public boolean unban() {
        if (!isBanned()) {
            return false;
        }
        getBans().clear();

        return true;
    }

    @Override
    public void kick(Component message) {
        if (getSidedPlayer() == null) {
            Cloud.getInstance().getQueue().publish("games.server.kick", new PlayerKickedEvent(getUniqueId(),
                    message != null ? LegacyComponentSerializer.builder().build().serialize(message) : null));

            return;
        }

        if (message != null) {
            getSidedPlayer().kick(GlobalTranslator.render(message, getLocale()));
        } else {
            getSidedPlayer().kick(null);
        }
    }

    private CityResponse getCity() {
        if (cityResponse != null) {
            return cityResponse;
        }

        if (getSidedPlayer() == null || getSidedPlayer().getIPAddress() == null) {
            return null;
        }

        DatabaseReader geoIp = ((CoreCloud)Cloud.getInstance()).getGeoIp();
        if (geoIp == null) {
            return null;
        }

        try {
            return (cityResponse = geoIp.city(getSidedPlayer().getIPAddress()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
