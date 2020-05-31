package tv.rewinside.home.command;

import com.google.gson.Gson;
import org.bukkit.entity.Player;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.player.PlayerHome;

public class HomeCreateCommand implements HomeCommand {

    private HomeBukkitPlugin plugin;

    public HomeCreateCommand(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getPermission() {
        return "home.create";
    }

    @Override
    public String getUsage() {
        return "<Name>";
    }

    @Override
    public boolean onExecute(Player player, String[] args) {
        if(args.length != 1) return false;
        plugin.getThreadUtil().getExecutorService().submit(()  -> {
            String name = args[0].toLowerCase();
            for(PlayerHome playerHome : plugin.getPlayerHomeRepository().findAllMember(player.getUniqueId())) {
                if(playerHome.getName().toLowerCase().equals(name)) {
                    player.sendMessage(plugin.getPrefix()+"§cDu besitzt bereits/ bist Mitglied eines Homes mit diesem Namen");
                    return;
                }
            }
            plugin.getPlayerHomeRepository().create(plugin.getPlayerHomeFactory().create(player.getUniqueId(), name, player.getLocation()));
            player.sendMessage(plugin.getPrefix()+"§aDer Home §e"+name+" §awurde erfolgreich erstellt");
            return;
        });
        return true;
    }
}
