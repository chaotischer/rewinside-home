package tv.rewinside.home.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.player.PlayerHome;

public class HomeAcceptTransferCommand implements HomeCommand {

    private HomeBukkitPlugin plugin;

    public HomeAcceptTransferCommand(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "accepttransfer";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getPermission() {
        return "home.accepttransfer";
    }

    @Override
    public String getUsage() {
        return "<Spielername>";
    }

    @Override
    public boolean onExecute(Player player, String[] args) {
        if(args.length != 1) return false;
        plugin.getThreadUtil().getExecutorService().submit(() -> {
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null && plugin.getTransferMap().containsKey(target) && plugin.getTransferMap().get(target).x.equals(player)) {
                PlayerHome playerHome = plugin.getPlayerHomeRepository().find(plugin.getTransferMap().get(target).y);
                for(PlayerHome home : plugin.getPlayerHomeRepository().findAllMember(player.getUniqueId())) {
                    if(home.getName().equals(playerHome.getName())) {
                        player.sendMessage(plugin.getPrefix()+"§cDu besitzt bereits/ bist Mitglied eines Homes mit diesem Namen");
                        return;
                    }
                }
                playerHome.transfer(player.getUniqueId());
                player.sendMessage(plugin.getPrefix()+"§aDu besitzt nun den Home §e"+playerHome.getName());
                target.sendMessage(plugin.getPrefix()+"§aDer Spieler §e"+player.getName()+" §abesitzt nun deinen Home §e"+playerHome.getName());
                plugin.getTransferMap().remove(target);
                return;
            }
            player.sendMessage(plugin.getPrefix()+"§cDu hast keine Anfrage von diesem Spieler");
            return;
        });
        return true;
    }
}
