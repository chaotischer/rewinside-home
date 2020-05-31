package tv.rewinside.home.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.player.PlayerHome;
import tv.rewinside.home.utils.Tuple;

public class HomeTransferCommand implements HomeCommand {

    private HomeBukkitPlugin plugin;

    public HomeTransferCommand(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "transfer";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getPermission() {
        return "home.transfer";
    }

    @Override
    public String getUsage() {
        return "<Name> <Spielername>";
    }

    @Override
    public boolean onExecute(Player player, String[] args) {
        if(args.length != 2) return false;
        plugin.getThreadUtil().getExecutorService().submit(() -> {
            if(plugin.getTransferMap().containsKey(player)) {
                player.sendMessage(plugin.getPrefix()+"§cDu hast bereits eine offene Anfrage");
                return;
            }
            String name = args[0].toLowerCase();
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null || !target.isOnline()) {
                player.sendMessage(plugin.getPrefix()+"§cDer Spieler muss dafür online sein");
                return;
            }
            if(target.equals(player)) {
                player.sendMessage(plugin.getPrefix()+"§cDu kannst dir nicht selber eine Anfrage schicken");
                return;
            }
            for(PlayerHome playerHome : plugin.getPlayerHomeRepository().findAllOwned(player.getUniqueId())) {
                if(playerHome.getName().equals(name)) {
                    player.sendMessage(plugin.getPrefix()+"§aDu hast dem Spieler §e"+target.getName()+" §aeine Transferanfrage gestellt");
                    target.sendMessage(plugin.getPrefix()+"§6Du hast eine Transferanfrage von §6für den Home §e"+playerHome.getName()+" erhalten");
                    target.sendMessage(plugin.getPrefix()+"§6Nehme sie mit §e/home accepttransfer "+player.getName()+" §6an");
                    plugin.getTransferMap().put(player, new Tuple<>(target, playerHome.getIdentifier()));
                    return;
                }
            }
            player.sendMessage(plugin.getPrefix()+"§cDu besitzt kein Home mit dem Namen "+name);
            return;
        });
        return true;
    }
}
