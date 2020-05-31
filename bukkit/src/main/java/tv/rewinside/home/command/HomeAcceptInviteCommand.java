package tv.rewinside.home.command;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.player.PlayerHome;
import tv.rewinside.home.player.location.PlayerLocation;

import java.util.Map;

public class HomeAcceptInviteCommand implements HomeCommand {

    private HomeBukkitPlugin plugin;

    public HomeAcceptInviteCommand(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "acceptinvite";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getPermission() {
        return "home.acceptinvite";
    }

    @Override
    public String getUsage() {
        return "<Spielername>";
    }

    private Map<Player, Integer> cooldownMap = Maps.newConcurrentMap();
    private Map<Player, BukkitTask> taskMap = Maps.newConcurrentMap();

    @Override
    public boolean onExecute(Player player, String[] args) {
        if(args.length != 1) return false;
        plugin.getThreadUtil().getExecutorService().submit(() -> {
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null && plugin.getInviteMap().containsKey(target) && plugin.getInviteMap().get(target).x.equals(player)) {
                PlayerHome playerHome = plugin.getPlayerHomeRepository().find(plugin.getInviteMap().get(target).y);
                cooldownMap.put(player, 0);
                PlayerLocation playerLocation = new PlayerLocation.Builder().withLocation(player.getLocation()).build();
                player.sendMessage(plugin.getPrefix()+"§aDu hast die Anfrage erfolgreich angenommen");
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
                        player.sendMessage(plugin.getPrefix()+"§aDu wurdest zum Home §e"+playerHome.getName()+" §avon §e"+target.getName()+" §ateleportiert");
                        cooldownMap.remove(player);
                        taskMap.get(player).cancel();
                    }
                }, 20, 20));
                plugin.getInviteMap().remove(target);
                return;
            }
            player.sendMessage(plugin.getPrefix()+"§cDu hast keine Einladung von diesem Spieler");
            return;
        });
        return true;
    }
}
