package tv.rewinside.home.log;

import java.util.UUID;

public class HomeLogFactory {

    public HomeLog create(UUID homeUUID, UUID oldOwner, UUID newOwner) {
        return new HomeLog.Builder()
                .withIdentifier(UUID.randomUUID())
                .withHomeUUID(homeUUID)
                .withOldOwner(oldOwner)
                .withNewOwner(newOwner)
                .withTimestamp(System.currentTimeMillis())
                .build();
    }
}
