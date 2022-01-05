@AnyMetaDef(idType = "uuid-char", metaType = "string", name = "PermissionMetaDef",
        metaValues = {
                @MetaValue(targetEntity = CloudPlayer.class, value = "player"),
                @MetaValue(targetEntity = CloudRank.class, value = "rank")
        })

package net.chrotos.chrotoscloud;

import net.chrotos.chrotoscloud.permissions.CloudRank;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;