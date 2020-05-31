package tv.rewinside.home.log;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.UUID;

@Entity(value = "homeLogs", noClassnameStored = true)
public class HomeLog {

    @Id
    private UUID identifier;

    private UUID homeUUID;
    private UUID oldOwner;
    private UUID newOwner;
    private long timestamp;

    public UUID getHomeUUID() {
        return homeUUID;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public UUID getOldOwner() {
        return oldOwner;
    }

    public UUID getNewOwner() {
        return newOwner;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static class Builder {

        private final HomeLog homeLog;

        public Builder() {
            this.homeLog = new HomeLog();
        }

        public Builder withIdentifier(UUID identifier) {
            this.homeLog.identifier = identifier;
            return this;
        }

        public Builder withHomeUUID(UUID homeUUID) {
            this.homeLog.homeUUID = homeUUID;
            return this;
        }

        public Builder withOldOwner(UUID oldOwner) {
            this.homeLog.oldOwner = oldOwner;
            return this;
        }

        public Builder withNewOwner(UUID newOwner) {
            this.homeLog.newOwner = newOwner;
            return this;
        }

        public Builder withTimestamp(long timestamp) {
            this.homeLog.timestamp = timestamp;
            return this;
        }

        public HomeLog build() {
            return this.homeLog;
        }
    }
}
