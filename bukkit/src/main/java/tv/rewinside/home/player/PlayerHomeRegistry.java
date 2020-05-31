package tv.rewinside.home.player;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.utils.UUIDFetcher;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerHomeRegistry {

    private HomeBukkitPlugin plugin;
    private Map<Player, UUID> currentHome;

    public PlayerHomeRegistry(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
        this.currentHome = Maps.newConcurrentMap();
    }

    public void init() {
        this.startCheck();
    }

    public void startCheck() {
        plugin.getThreadUtil().getScheduledService().scheduleAtFixedRate(() -> {
            for(Player all : Bukkit.getOnlinePlayers()) {
                int i = 0;
                for(PlayerHome playerHome : plugin.getPlayerHomeRepository().findAll()) {
                    if(all.getLocation().distance(playerHome.getPlayerLocation().getLocation()) <= plugin.getTitleRadius()) {
                        if(!currentHome.containsKey(all) || (currentHome.containsKey(all) && !currentHome.get(all).equals(playerHome.getIdentifier()))) {
                            all.sendTitle("§6"+playerHome.getName(), "§7von "+ UUIDFetcher.getName(playerHome.getOwnerUUID()));
                            currentHome.put(all, playerHome.getIdentifier());
                        }
                        i++;
                    }
                }
                if(i == 0) {
                    currentHome.remove(all);
                }
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    public Map<Player, UUID> getCurrentHome() {
        return currentHome;
    }
}
