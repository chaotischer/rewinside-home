package tv.rewinside.home.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.player.PlayerHome;
import tv.rewinside.home.utils.Tuple;

public class HomeInviteCommand implements HomeCommand {

    private HomeBukkitPlugin plugin;

    public HomeInviteCommand(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getPermission() {
        return "home.invite";
    }

    @Override
    public String getUsage() {
        return "<Name> <Spielername>";
    }

    @Override
    public boolean onExecute(Player player, String[] args) {
        if(args.length != 2) return false;
        plugin.getThreadUtil().getExecutorService().submit(() -> {
            String name = args[0].toLowerCase();
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null || !target.isOnline()) {
                player.sendMessage(plugin.getPrefix()+"§cDer Spieler muss dafür online sein");
                return;
            }
            if(target.equals(player)) {
                player.sendMessage(plugin.getPrefix()+"§cDu kannst dir nicht selber eine Einladung schicken");
                return;
            }
            for(PlayerHome playerHome : plugin.getPlayerHomeRepository().findAllMember(player.getUniqueId())) {
                if(playerHome.getName().equals(name)) {
                    player.sendMessage(plugin.getPrefix()+"§aDu hast dem Spieler §e"+target.getName()+" §aeine Einladung geschickt");
                    target.sendMessage(plugin.getPrefix()+"§6Du hast eine Einladung von §e"+player.getName()+" §6für den Home §e"+playerHome.getName()+" §6erhalten");
                    target.sendMessage(plugin.getPrefix()+"§6Nehme sie mit §e/home acceptinvite "+player.getName()+" §6an");
                    plugin.getInviteMap().put(player, new Tuple<>(target, playerHome.getIdentifier()));
                    return;
                }
            }
            player.sendMessage(plugin.getPrefix()+"§cDu besitzt kein Home mit dem Namen "+name);
            return;
        });
        return true;
    }
}
