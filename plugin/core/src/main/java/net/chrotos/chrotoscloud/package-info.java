@AnyMetaDef(idType = "uuid-char", metaType = "string", name = "PermissionMetaDef",
        metaValues = {
                @MetaValue(targetEntity = CloudPlayer.class, value = "player")
        })

package net.chrotos.chrotoscloud;

import net.chrotos.chrotoscloud.player.CloudPlayer;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;