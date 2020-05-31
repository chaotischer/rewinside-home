package tv.rewinside.home.command;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.player.PlayerHome;
import tv.rewinside.home.player.location.PlayerLocation;

import java.util.Map;

public class HomeTeleportCommand implements HomeCommand {

    private HomeBukkitPlugin plugin;

    public HomeTeleportCommand(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"tp"};
    }

    @Override
    public String getPermission() {
        return "home.teleport";
    }

    @Override
    public String getUsage() {
        return "<Name>";
    }

    private Map<Player, Integer> cooldownMap = Maps.newConcurrentMap();
    private Map<Player, BukkitTask> taskMap = Maps.newConcurrentMap();

    @Override
    public boolean onExecute(Player player, String[] args) {
        if(args.length != 1) return false;
        plugin.getThreadUtil().getExecutorService().submit(() -> {
            if(cooldownMap.containsKey(player)) {
                player.sendMessage(plugin.getPrefix()+"§cDu bist bereits in einem Teleportvorgang");
                return;
            }
            String name = args[0].toLowerCase();
            for(PlayerHome playerHome : plugin.getPlayerHomeRepository().findAllMember(player.getUniqueId())) {
                if(playerHome.getName().equals(name)) {
                    cooldownMap.put(player, 0);
                    PlayerLocation playerLocation = new PlayerLocation.Builder().withLocation(player.getLocation()).build();
                    player.sendMessage(plugin.getPrefix()+"§7Bitte bewege dich §6"+plugin.getTeleportCooldown()+" Sekunden §7lang nicht");
                    taskMap.put(player, Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                        if(((int)player.getLocation().getX() != (int)playerLocation.getX()) || ((int)player.getLocation().getZ() != (int)playerLocation.getZ())) {
                            player.sendMessage(plugin.getPrefix()+"§cDu hast dich bewegt, weshalb der Teleport abgebrochen wurde");
                            cooldownMap.remove(player);
                            taskMap.get(player).cancel();
                        }
                        cooldownMap.put(player, cooldownMap.get(player)+1);
                        if(cooldownMap.get(player) == plugin.getTeleportCooldown()) {
                            player.teleport(playerHome.getPlayerLocation().getLocation());
                            player.sendMessage(plugin.getPrefix()+"§aDu wurdest zum Home §e"+name+" §ateleportiert");
                            cooldownMap.remove(player);
                            taskMap.get(player).cancel();
                        }
                    }, 20, 20));
                    return;
                }
            }
            player.sendMessage(plugin.getPrefix()+"§cDu besitzt kein Home mit dem Namen "+name);
            return;
        });
        return true;
    }
}
