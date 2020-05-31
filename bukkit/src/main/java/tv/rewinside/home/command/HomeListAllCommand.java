package tv.rewinside.home.command;

import org.bukkit.entity.Player;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.log.HomeLog;
import tv.rewinside.home.player.PlayerHome;
import tv.rewinside.home.player.location.PlayerLocation;
import tv.rewinside.home.utils.UUIDFetcher;

import java.util.List;

public class HomeListAllCommand implements HomeCommand {

    private HomeBukkitPlugin plugin;

    public HomeListAllCommand(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "listall";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getPermission() {
        return "home.listall";
    }

    @Override
    public String getUsage() {
        return "(<Seite>)";
    }

    @Override
    public boolean onExecute(Player player, String[] args) {
        plugin.getThreadUtil().getExecutorService().submit(() -> {
            int page = 1;
            if(args.length > 0) {
                try {
                    page = Integer.valueOf(args[0]);
                } catch(Exception e) {
                    player.sendMessage(plugin.getPrefix()+"§cBitte gebe eine gültige Zahl an");
                    return;
                }
            }
            List<HomeLog> logs = plugin.getHomeLogRepository().findAll();
            if(logs.size() == 0) {
                player.sendMessage(plugin.getPrefix()+"§cEs gibt keine Logs bezüglich des Transfers");
                return;
            }
            if(logs.size() < ((page-1)*5)+1) {
                player.sendMessage(plugin.getPrefix()+"§cEs gibt nicht so viele Logs");
                return;
            }
            player.sendMessage(plugin.getPrefix()+"Auflistung §6"+page+"§8/§6"+((logs.size()/5)+1));
            for(int i = (page-1)*5; i < page*5; i++) {
                if(logs.size() > i) {
                    HomeLog log = logs.get(i);
                    String name = "§c(gelöscht)";
                    PlayerHome playerHome = plugin.getPlayerHomeRepository().find(log.getHomeUUID());
                    if(playerHome != null) {
                        name = playerHome.getName();
                    }
                    player.sendMessage("§7- §e"+name+" §8/ §e"+UUIDFetcher.getName(log.getOldOwner())+" §6-> §e"+UUIDFetcher.getName(log.getNewOwner()));
                    if(i == (page*5)-1) {
                        player.sendMessage("§6/home listall "+(page+1)+" §7für die nächste Seite");
                    }
                    return;
                }
            }
            return;
        });
        return true;
    }
}
