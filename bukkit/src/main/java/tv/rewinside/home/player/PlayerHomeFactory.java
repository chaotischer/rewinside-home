package tv.rewinside.home.player;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import tv.rewinside.home.player.location.PlayerLocation;

import java.util.UUID;

public class PlayerHomeFactory {

    public PlayerHome create(UUID ownerUUID, String name, Location location) {
        return new PlayerHome.Builder()
                .withIdentifier(UUID.randomUUID())
                .withOwnerUUID(ownerUUID)
                .withMembers(Lists.newArrayList())
                .withName(name)
                .withPlayerLocation(new PlayerLocation.Builder()
                        .withLocation(location)
                        .build())
                .build();
    }
}
