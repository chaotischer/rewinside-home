package tv.rewinside.home.player.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PlayerLocation {

    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public static class Builder {

        private final PlayerLocation playerLocation;

        public Builder() {
            this.playerLocation = new PlayerLocation();
        }

        public Builder withLocation(Location location) {
            this.playerLocation.world = location.getWorld().getName();
            this.playerLocation.x = location.getX();
            this.playerLocation.y = location.getY();
            this.playerLocation.z = location.getZ();
            this.playerLocation.yaw = location.getYaw();
            this.playerLocation.pitch = location.getPitch();
            return this;
        }

        public PlayerLocation build() {
            return this.playerLocation;
        }
    }
}
