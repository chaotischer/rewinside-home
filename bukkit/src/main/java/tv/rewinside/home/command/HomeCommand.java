package tv.rewinside.home.command;

import org.bukkit.entity.Player;

public interface HomeCommand {
    String getName();
    String[] getAliases();
    String getPermission();
    String getUsage();
    boolean onExecute(Player player, String[] args);
}
