package tv.rewinside.home.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.player.PlayerHome;

public class HomeMemberCommand implements HomeCommand {

    private HomeBukkitPlugin plugin;

    public HomeMemberCommand(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "member";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getPermission() {
        return "home.member";
    }

    @Override
    public String getUsage() {
        return "<Name> <add/remove> <Spielername>";
    }

    @Override
    public boolean onExecute(Player player, String[] args) {
        if(args.length != 3 || (!args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) return false;
        plugin.getThreadUtil().getExecutorService().submit(() -> {
            String name = args[0].toLowerCase();
            Player target = Bukkit.getPlayer(args[2]);
            if(target == null || !target.isOnline()) {
                player.sendMessage(plugin.getPrefix()+"§cDer Spieler muss dafür online sein");
                return;
            }
            if(target.equals(player)) {
                player.sendMessage(plugin.getPrefix()+"§cDu kannst dich nicht selber als Mitglied hinzufügen");
                return;
            }
            for(PlayerHome playerHome : plugin.getPlayerHomeRepository().findAllMember(target.getUniqueId())) {
                if(playerHome.getName().equals(name)) {
                    player.sendMessage(plugin.getPrefix()+"§cDer Spieler besitzt bereits/ ist Mitglied eines Homes mit diesem Namen");
                    return;
                }
            }
            for(PlayerHome playerHome : plugin.getPlayerHomeRepository().findAllOwned(player.getUniqueId())) {
                if(playerHome.getName().equals(name)) {
                    if(args[1].equalsIgnoreCase("add")) {
                        if(playerHome.getMembers().contains(target.getUniqueId())) {
                            player.sendMessage(plugin.getPrefix()+"§cDer Spieler ist bereits Mitglied");
                            return;
                        }
                        playerHome.addMember(target.getUniqueId());
                        player.sendMessage(plugin.getPrefix()+"§aDer Spieler §e"+target.getName()+" §aist nun Mitglied des Homes");
                        return;
                    } else if(args[1].equalsIgnoreCase("remove")) {
                        if(!playerHome.getMembers().contains(target.getUniqueId())) {
                            player.sendMessage(plugin.getPrefix()+"§cDer Spieler ist kein Mitglied");
                            return;
                        }
                        playerHome.removeMember(target.getUniqueId());
                        player.sendMessage(plugin.getPrefix()+"§cDer Spieler §e"+target.getName()+" §cist nun kein Mitglied des Homes");
                        return;
                    }
                }
            }
            player.sendMessage(plugin.getPrefix()+"§cDu besitzt kein Home mit dem Namen "+name);
            return;
        });
        return true;
    }
}
