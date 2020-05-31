package tv.rewinside.home;

import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import tv.rewinside.home.command.HomeCommandRegistry;
import tv.rewinside.home.inventories.HomeInventory;
import tv.rewinside.home.log.HomeLogFactory;
import tv.rewinside.home.log.HomeLogRepository;
import tv.rewinside.home.player.PlayerHomeFactory;
import tv.rewinside.home.player.PlayerHomeRegistry;
import tv.rewinside.home.player.PlayerHomeRepository;

import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.Datastore;
import tv.rewinside.home.scoreboard.ScoreboardRegistry;
import tv.rewinside.home.utils.ThreadUtil;
import tv.rewinside.home.utils.Tuple;

import java.util.Map;
import java.util.UUID;

public class HomeBukkitPlugin extends JavaPlugin {

    private static HomeBukkitPlugin instance;

    @Override
    public void onEnable() {
        final long epoch = System.currentTimeMillis();
        Bukkit.getLogger().info("Initializing home plugin...");

        instance = this;

        this.initConfig();
        this.initThread();
        this.initVariables();
        this.initCommands();
        this.initMorphia();
        this.initHome();
        this.initLog();
        this.initInventory();
        this.initScoreboard();

        this.registerServices();

        final long initializeDuration = System.currentTimeMillis() - epoch;
        Bukkit.getLogger().info(String.format("Successfully initialized home plugin. (took %sms)", initializeDuration));
    }

    @Override
    public void onDisable() {

    }

    private PlayerHomeRepository playerHomeRepository;
    private PlayerHomeFactory playerHomeFactory;
    private PlayerHomeRegistry playerHomeRegistry;

    private void initHome() {
        playerHomeFactory = new PlayerHomeFactory();
        playerHomeRepository = new PlayerHomeRepository(datastore);
        playerHomeRegistry = new PlayerHomeRegistry(this);
        playerHomeRegistry.init();
    }

    private HomeInventory homeInventory;

    private void initInventory() {
        homeInventory = new HomeInventory(this);
        Bukkit.getPluginManager().registerEvents(homeInventory, this);
    }

    private ScoreboardRegistry scoreboardRegistry;

    private void initScoreboard() {
        scoreboardRegistry = new ScoreboardRegistry(this);
        scoreboardRegistry.init();
        Bukkit.getPluginManager().registerEvents(scoreboardRegistry, this);
    }

    private HomeLogFactory homeLogFactory;
    private HomeLogRepository homeLogRepository;

    private void initLog() {
        homeLogFactory = new HomeLogFactory();
        homeLogRepository = new HomeLogRepository(datastore);
    }

    private ThreadUtil threadUtil;

    private void initThread() {
        threadUtil = new ThreadUtil();
        threadUtil.init();
    }

    public void initConfig() {
        this.getConfig().addDefault("mongoURI", "mongodb://localhost:27017");
        this.getConfig().addDefault("prefix", "&7[&eHOME&7]");
        this.getConfig().addDefault("teleportCooldown", 5);
        this.getConfig().addDefault("titleRadius", 5);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    private Map<Player, Tuple<Player, UUID>> transferMap;
    private Map<Player, Tuple<Player, UUID>> inviteMap;
    private String mongoURI;
    private String prefix;
    private int teleportCooldown;
    private int titleRadius;

    public void initVariables() {
        transferMap = Maps.newConcurrentMap();
        inviteMap = Maps.newConcurrentMap();
        mongoURI = this.getConfig().getString("mongoURI");
        prefix = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("prefix"))+" ";
        teleportCooldown = this.getConfig().getInt("teleportCooldown");
        titleRadius = this.getConfig().getInt("titleRadius");
    }

    private Morphia morphia;
    private Datastore datastore;

    public void initMorphia() {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        this.morphia = new Morphia();
        this.morphia.getMapper().getOptions().setStoreEmpties(true);
        this.datastore = morphia.createDatastore(mongoClient, "home-plugin");
    }

    private HomeCommandRegistry homeCommandRegistry;

    public void initCommands() {
        homeCommandRegistry = new HomeCommandRegistry(this);
        homeCommandRegistry.init();
        getCommand("home").setExecutor(homeCommandRegistry);
    }

    private void registerServices() {
        ServicesManager servicesManager = Bukkit.getServicesManager();
        servicesManager.register(PlayerHomeRepository.class, playerHomeRepository, this, ServicePriority.Highest);
        servicesManager.register(HomeLogRepository.class, homeLogRepository, this, ServicePriority.Highest);
    }

    public String getPrefix() {
        return prefix;
    }

    public int getTeleportCooldown() {
        return teleportCooldown;
    }

    public int getTitleRadius() {
        return titleRadius;
    }

    public PlayerHomeFactory getPlayerHomeFactory() {
        return playerHomeFactory;
    }

    public PlayerHomeRepository getPlayerHomeRepository() {
        return playerHomeRepository;
    }

    public HomeLogFactory getHomeLogFactory() {
        return homeLogFactory;
    }

    public HomeLogRepository getHomeLogRepository() {
        return homeLogRepository;
    }

    public PlayerHomeRegistry getPlayerHomeRegistry() {
        return playerHomeRegistry;
    }

    public HomeInventory getHomeInventory() {
        return homeInventory;
    }

    public ScoreboardRegistry getScoreboardRegistry() {
        return scoreboardRegistry;
    }

    public Map<Player, Tuple<Player, UUID>> getTransferMap() {
        return transferMap;
    }

    public Map<Player, Tuple<Player, UUID>> getInviteMap() {
        return inviteMap;
    }

    public ThreadUtil getThreadUtil() {
        return threadUtil;
    }

    public static HomeBukkitPlugin getInstance() {
        return instance;
    }
}
