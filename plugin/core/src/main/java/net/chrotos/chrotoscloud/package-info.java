@AnyMetaDef(idType = "uuid-char", metaType = "string", name = "PermissionMetaDef",
        metaValues = {
                @MetaValue(targetEntity = CloudPlayer.class, value = "player"),
                @MetaValue(targetEntity = CloudRank.class, value = "rank")
        })

@FilterDef(name = "accountType", parameters = {
        @ParamDef(name = "accountType", type = "net.chrotos.chrotoscloud.economy.AccountType")},
        defaultCondition = "account_type = :accountType")
@FilterDef(name = "uniqueId", parameters = {@ParamDef(name = "uniqueId", type = "java.util.UUID")},
        defaultCondition = "unique_id = :uniqueId")
@FilterDef(name = "gameMode", parameters = {@ParamDef(name = "gameMode", type = "java.lang.String")},
        defaultCondition = "game_mode = :gameMode")

package net.chrotos.chrotoscloud;

import net.chrotos.chrotoscloud.permissions.CloudRank;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.MetaValue;
import org.hibernate.annotations.ParamDef;