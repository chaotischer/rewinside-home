package tv.rewinside.home.inventories;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import tv.rewinside.home.HomeBukkitPlugin;
import tv.rewinside.home.player.PlayerHome;
import tv.rewinside.home.player.location.PlayerLocation;
import tv.rewinside.home.utils.UUIDFetcher;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomeInventory implements Listener {

    private HomeBukkitPlugin plugin;
    private Map<Player, Integer> currentPage;
    private Map<Player, PlayerHome> currentHome;

    public HomeInventory(HomeBukkitPlugin plugin) {
        this.plugin = plugin;
        currentPage = Maps.newConcurrentMap();
        currentHome = Maps.newConcurrentMap();
    }

    public Inventory get(Player player, int page) {
        currentPage.put(player, page);
        Inventory inventory = Bukkit.createInventory(null, 27, "§6Homes §8> §7Übersicht");
        List<PlayerHome> homes = plugin.getPlayerHomeRepository().findAll();
        for(int i = 18; i < 27; i++) {
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (byte)7).withName(" ").build());
        }
        if(page != 1) inventory.setItem(18, new ItemBuilder(Material.SKULL_ITEM, (byte)3).withSkull("MHF_ArrowLeft").withName("§eVorherige Seite").build());
        for(int i = (page-1)*18; i < page*18; i++) {
            if(homes.size() > i) {
                PlayerHome home = homes.get(i);
                PlayerLocation location = home.getPlayerLocation();
                inventory.setItem(i-((page-1)*18), new ItemBuilder(Material.CHEST).withName("§6"+home.getName()).withLore(Arrays.asList("§7Besitzer§8: §e"+UUIDFetcher.getName(home.getOwnerUUID()), "§7Ort§8: §e"+location.getWorld()+"§8, §e"+(int)location.getX()+"§8, §e"+(int)location.getY()+"§8, §e"+(int)location.getZ(), "§8"+home.getIdentifier())).build());
                if(i == (page*18)-1) {
                    inventory.setItem(26, new ItemBuilder(Material.SKULL_ITEM, (byte)3).withSkull("MHF_ArrowRight").withName("§eNächste Seite").build());
                }
            }
        }
        return inventory;
    }

    public Inventory getHome(Player player, PlayerHome home) {
        currentHome.put(player, home);
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, "§6Homes §8> §7Verwaltung");
        inventory.setItem(0, new ItemBuilder(Material.SKULL_ITEM, (byte)3).withSkull("MHF_ArrowLeft").withName("§eZurück").build());
        inventory.setItem(2, new ItemBuilder(Material.LAVA_BUCKET).withName("§c§lLöschen").build());
        List<String> members = Lists.newArrayList();
        home.getMembers().stream().forEach(memberUUID -> members.add("§8- §7"+UUIDFetcher.getName(memberUUID)));
        if(members.size() == 0) members.add("§ckeine");
        inventory.setItem(3, new ItemBuilder(Material.PAPER).withName("§eMitbewohner").withLore(members).build());
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if(event.getInventory().getTitle().equals("§6Homes §8> §7Übersicht")) {
            event.setCancelled(true);
            if(event.getSlot() == 18) {
                player.openInventory(get(player, currentPage.get(player)-1));
            } else if(event.getSlot() == 26) {
                player.openInventory(get(player, currentPage.get(player)+1));
            } else {
                plugin.getThreadUtil().getExecutorService().submit(() -> {
                    if(event.getCurrentItem().getItemMeta().getLore() == null) return;
                    UUID homeUUID = UUID.fromString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(2)));
                    PlayerHome playerHome = plugin.getPlayerHomeRepository().find(homeUUID);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.openInventory(getHome(player, playerHome)), 0);
                });
            }
        } else if(event.getInventory().getTitle().equals("§6Homes §8> §7Verwaltung")) {
            event.setCancelled(true);
            if(event.getSlot() == 0) {
                player.openInventory(get(player, currentPage.get(player)));
            } else if(event.getSlot() == 2) {
                player.closeInventory();
                plugin.getThreadUtil().getExecutorService().submit(() -> {
                    PlayerHome playerHome = currentHome.get(player);
                    plugin.getPlayerHomeRepository().delete(playerHome);
                    player.sendMessage(plugin.getPrefix()+"§cDer Home wurde erfolgreich gelöscht");
                });
            }
        }
    }

    public class ItemBuilder {

        private final ItemStack itemStack;

        public ItemBuilder(Material material) {
            itemStack = new ItemStack(material);
        }

        public ItemBuilder(Material material, byte data) {
            itemStack = new ItemStack(material, 1, data);
        }

        public ItemBuilder withName(String name) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(name);
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder withLore(List<String> lore) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder withSkull(String owner) {
            SkullMeta itemMeta = (SkullMeta)itemStack.getItemMeta();
            itemMeta.setOwner(owner);
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemStack build() {
            return this.itemStack;
        }
    }
}
