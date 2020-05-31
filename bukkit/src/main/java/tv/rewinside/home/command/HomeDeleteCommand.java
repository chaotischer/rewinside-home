package tv.rewinside.home.command;

import org.bukkit.entity.Player;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.player.PlayerHome;

public class HomeDeleteCommand implements HomeCommand {

    private HomeBukkitPlugin plugin;

    public HomeDeleteCommand(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getPermission() {
        return "home.delete";
    }

    @Override
    public String getUsage() {
        return "<Name>";
    }

    @Override
    public boolean onExecute(Player player, String[] args) {
        if(args.length != 1) return false;
        plugin.getThreadUtil().getExecutorService().submit(() -> {
            String name = args[0].toLowerCase();
            for(PlayerHome playerHome : plugin.getPlayerHomeRepository().findAllMember(player.getUniqueId())) {
                if(playerHome.getName().equals(name)) {
                    plugin.getPlayerHomeRepository().delete(playerHome);
                    player.sendMessage(plugin.getPrefix()+"§aDer Home §e"+name+" §awurde erfolgreich gelöscht");
                    return;
                }
            }
            player.sendMessage(plugin.getPrefix()+"§cDu besitzt kein Home mit dem Namen "+name);
            return;
        });
        return true;
    }
}
