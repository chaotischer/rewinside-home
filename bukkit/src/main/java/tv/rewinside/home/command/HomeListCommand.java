package tv.rewinside.home.command;

import org.bukkit.entity.Player;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.player.PlayerHome;
import tv.rewinside.home.player.location.PlayerLocation;

import java.util.Collections;
import java.util.List;

public class HomeListCommand implements HomeCommand {

    private HomeBukkitPlugin plugin;

    public HomeListCommand(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getPermission() {
        return "home.list";
    }

    @Override
    public String getUsage() {
        return "(<Seite>)";
    }

    @Override
    public boolean onExecute(Player player, String[] args) {
        plugin.getThreadUtil().getExecutorService().submit(() -> {
            List<PlayerHome> homes = plugin.getPlayerHomeRepository().findAllMember(player.getUniqueId());
            if(homes.size() == 0) {
                player.sendMessage(plugin.getPrefix()+"§cDu besitzt keine Homes");
                return;
            }
            int page = 1;
            if(args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]);
                } catch(Exception e) {
                    player.sendMessage(plugin.getPrefix()+"§cDu besitzt nicht so viele Homes");
                    return;
                }
            }
            if(homes.size() < ((page-1)*5)+1) {
                player.sendMessage(plugin.getPrefix()+"§cBitte gebe eine gültige Zahl an");
                return;
            }
            player.sendMessage(plugin.getPrefix()+"Auflistung §6"+page+"§8/§6"+((homes.size()/5)+1));
            for(int i = (page-1)*5; i < page*5; i++) {
                if(homes.size() > i) {
                    PlayerHome home = homes.get(i);
                    PlayerLocation location = home.getPlayerLocation();
                    player.sendMessage("§7- §e"+homes.get(i).getName()+" §8(§6"+location.getWorld()+"§8, §6"+(int)location.getX()+"§8, §6"+(int)location.getY()+"§8, §6"+(int)location.getZ()+"§8)");
                    if(i == (page*5)-1) {
                        player.sendMessage("§6/home list "+(page+1)+" §7für die nächste Seite");
                    }
                }
            }
            return;
        });
        return true;
    }
}
