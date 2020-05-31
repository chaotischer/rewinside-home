package tv.rewinside.home.player;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.player.location.PlayerLocation;
import tv.rewinside.home.utils.Tuple;

import java.util.List;
import java.util.UUID;

@Entity(value = "playerHomes", noClassnameStored = true)
public class PlayerHome {

    @Id
    private UUID identifier;

    private UUID ownerUUID;
    private List<UUID> members;
    private String name;
    private PlayerLocation playerLocation;

    public UUID getIdentifier() {
        return identifier;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public PlayerLocation getPlayerLocation() {
        return playerLocation;
    }

    public void addMember(UUID member) {
        members.add(member);
        HomeBukkitPlugin.getInstance().getThreadUtil().getExecutorService().submit(() ->
                HomeBukkitPlugin.getInstance().getPlayerHomeRepository().update(this.identifier, new Tuple<>("members", this.members)));
    }

    public void removeMember(UUID member) {
        members.remove(member);
        HomeBukkitPlugin.getInstance().getThreadUtil().getExecutorService().submit(() ->
                HomeBukkitPlugin.getInstance().getPlayerHomeRepository().update(this.identifier, new Tuple<>("members", this.members)));
    }

    public void transfer(UUID newOwner) {
        HomeBukkitPlugin.getInstance().getThreadUtil().getExecutorService().submit(() ->
                HomeBukkitPlugin.getInstance().getHomeLogRepository().create(HomeBukkitPlugin.getInstance().getHomeLogFactory().create(this.identifier, this.ownerUUID, newOwner)));
        HomeBukkitPlugin.getInstance().getThreadUtil().getExecutorService().submit(() -> {
            this.ownerUUID = newOwner;
            HomeBukkitPlugin.getInstance().getPlayerHomeRepository().update(this.identifier, new Tuple<>("ownerUUID", this.ownerUUID));
        });
    }

    public static class Builder {

        private final PlayerHome playerHome;

        public Builder() {
            this.playerHome = new PlayerHome();
        }

        public Builder withIdentifier(UUID identifier) {
            this.playerHome.identifier = identifier;
            return this;
        }

        public Builder withOwnerUUID(UUID ownerUUID) {
            this.playerHome.ownerUUID = ownerUUID;
            return this;
        }

        public Builder withMembers(List<UUID> members) {
            this.playerHome.members = members;
            return this;
        }

        public Builder withName(String name) {
            this.playerHome.name = name;
            return this;
        }

        public Builder withPlayerLocation(PlayerLocation playerLocation) {
            this.playerHome.playerLocation = playerLocation;
            return this;
        }

        public PlayerHome build() {
            return this.playerHome;
        }
    }
}
