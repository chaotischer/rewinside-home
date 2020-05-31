package tv.rewinside.home.command;

import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.rewinside.home.HomeBukkitPlugin;

import java.util.Arrays;
import java.util.List;

public class HomeCommandRegistry implements CommandExecutor {

    private final HomeBukkitPlugin plugin;
    private final List<HomeCommand> commands;

    public HomeCommandRegistry(final HomeBukkitPlugin plugin) {
        this.plugin = plugin;
        this.commands = Lists.newArrayList();
    }

    public void register(final HomeCommand homeCommand) {
        this.commands.add(homeCommand);
    }

    public void init() {
        this.register(new HomeAcceptInviteCommand(this.plugin));
        this.register(new HomeAcceptTransferCommand(this.plugin));
        this.register(new HomeCreateCommand(this.plugin));
        this.register(new HomeDeleteCommand(this.plugin));
        this.register(new HomeGUICommand(this.plugin));
        this.register(new HomeInviteCommand(this.plugin));
        this.register(new HomeListAllCommand(this.plugin));
        this.register(new HomeListCommand(this.plugin));
        this.register(new HomeMemberCommand(this.plugin));
        this.register(new HomeTeleportCommand(this.plugin));
        this.register(new HomeTransferCommand(this.plugin));
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) return false;
        final Player player = (Player) sender;
        if (args.length == 0) {
            this.sendHelp(player);
            return false;
        }
        for (final HomeCommand homeCommand : this.commands) {
            if (args[0].equalsIgnoreCase(homeCommand.getName()) || Arrays.asList(homeCommand.getAliases()).contains(args[0])) {
                if (!player.hasPermission(homeCommand.getPermission())) {
                    this.sendNoPerms(player);
                    return false;
                }
                if (!homeCommand.onExecute(player, Arrays.copyOfRange(args, 1, args.length))) {
                    player.sendMessage(this.plugin.getPrefix() + "§cBenutzung: §7/home " + homeCommand.getName() + " §f" + homeCommand.getUsage());
                }
                return true;
            }
        }
        this.sendHelp(player);
        return false;
    }

    public void sendHelp(final Player player) {
        boolean permissions = false;
        for (final HomeCommand homeCommand : this.commands) {
            if (player.hasPermission(homeCommand.getPermission())) {
                permissions = true;
            }
        }
        if (permissions) {
            player.sendMessage(this.plugin.getPrefix() + "Übersicht der Befehle");
            for (final HomeCommand homeCommand : this.commands) {
                if (player.hasPermission(homeCommand.getPermission())) {
                    player.sendMessage("§7- /home " + homeCommand.getName() + " §e" + homeCommand.getUsage());
                }
            }
        } else {
            this.sendNoPerms(player);
        }
    }

    public void sendNoPerms(final Player player) {
        player.sendMessage(this.plugin.getPrefix() + "§cDu hast keine Berechtigung für diesen Befehl");
    }
}
