package tv.rewinside.home.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.log.HomeLog;
import tv.rewinside.home.player.PlayerHome;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ScoreboardRegistry implements Listener {

    private HomeBukkitPlugin plugin;

    public ScoreboardRegistry(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        start();
    }

    public void start() {
        plugin.getThreadUtil().getScheduledService().scheduleAtFixedRate(() -> {
            for(Player all : Bukkit.getOnlinePlayers()) {
                reload(all);
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    public Scoreboard get(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("homes", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§6§lHOMES");
        objective.getScore("§e").setScore(3);
        return scoreboard;
    }

    public boolean reload(Player player) {
        if (player.getScoreboard() == null) return false;
        List<PlayerHome> home = plugin.getPlayerHomeRepository().findAllMember(player.getUniqueId());
        List<HomeLog> logs = plugin.getHomeLogRepository().findAllByOldOwner(player.getUniqueId());
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("homes");
        scoreboard.getPlayers().stream().filter(score -> score.getName().startsWith("§7")).forEach(score -> scoreboard.resetScores(score.getName()));
        objective.getScore("§7Homes im Besitz§8: §e"+home.size()).setScore(2);
        objective.getScore("§7Transferierte Homes§8: §e"+logs.size()).setScore(1);
        if(plugin.getPlayerHomeRegistry().getCurrentHome().containsKey(player)) {
            UUID homeUUID = plugin.getPlayerHomeRegistry().getCurrentHome().get(player);
            objective.getScore("§7Derzeitiges Home§8: §e"+plugin.getPlayerHomeRepository().find(homeUUID).getName()).setScore(0);
        }
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setScoreboard(get(player));
    }
}
