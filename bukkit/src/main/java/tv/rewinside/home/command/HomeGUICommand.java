package tv.rewinside.home.command;

import org.bukkit.entity.Player;
import tv.rewinside.home.HomeBukkitPlugin;

public class HomeGUICommand implements HomeCommand {

    private HomeBukkitPlugin plugin;

    public HomeGUICommand(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "gui";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getPermission() {
        return "home.gui";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean onExecute(Player player, String[] args) {
        plugin.getThreadUtil().getExecutorService().submit(() -> {
            player.openInventory(plugin.getHomeInventory().get(player, 1));
        });
        return true;
    }
}
