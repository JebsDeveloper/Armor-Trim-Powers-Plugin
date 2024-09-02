package com.jebs.armortrims;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public final class ArmorTrims extends JavaPlugin implements Listener {
    private static final ArrayList<PlayerObject> playerObjects = new ArrayList<>();
    private static final ArrayList<Entity> summonedEntities = new ArrayList<>();
    private static final ArrayList<Player> entitySummoners = new ArrayList<>();

    public static PluginLogger logger;

    @Override
    public void onEnable() {
        getLogger().info("Initializing Armor Trim Powers Plugin");

        logger = (PluginLogger) getLogger();

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new Listeners(), this);

        Iterator playersIterator = getServer().getOnlinePlayers().iterator();

        while(playersIterator.hasNext()) {
            Player player = (Player) playersIterator.next();
            playerObjects.add(new PlayerObject(player));
        }

        getLogger().info("Finished initializing Armor Trim Powers Plugin");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling Armor Trim Powers Plugin");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerObjects.add(new PlayerObject(event.getPlayer()));
        getLogger().info("New Player Object Create For " + event.getPlayer().getName());
    }

    private static boolean hasConsistentTrims(Player player) {
        if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null && player.getInventory().getBoots() != null) {
            ArmorMeta helmetMeta = (ArmorMeta) (Objects.requireNonNull(player.getInventory().getHelmet())).getItemMeta();
            ArmorMeta chestplateMeta = (ArmorMeta) (Objects.requireNonNull(player.getInventory().getChestplate())).getItemMeta();
            ArmorMeta leggingsMeta = (ArmorMeta) (Objects.requireNonNull(player.getInventory().getLeggings())).getItemMeta();
            ArmorMeta bootsMeta = (ArmorMeta) (Objects.requireNonNull(player.getInventory().getBoots())).getItemMeta();

            if (helmetMeta != null && chestplateMeta != null && leggingsMeta != null && bootsMeta != null && helmetMeta.hasTrim() && chestplateMeta.hasTrim() && leggingsMeta.hasTrim() && bootsMeta.hasTrim()) {
                return Objects.requireNonNull(helmetMeta.getTrim()).getPattern().getKey().equals((Objects.requireNonNull(chestplateMeta.getTrim())).getPattern().getKey()) && helmetMeta.getTrim().getPattern().getKey().equals((Objects.requireNonNull(leggingsMeta.getTrim())).getPattern().getKey()) && helmetMeta.getTrim().getPattern().getKey().equals((Objects.requireNonNull(bootsMeta.getTrim())).getPattern().getKey());
            }
        }

        return false;
    }

    private static PlayerObject getPlayerObject(Player player) {
        Iterator playerObjectIterator = playerObjects.iterator();
        PlayerObject playerObject;

        do {
            if (!playerObjectIterator.hasNext()) {
                return null;
            }

            playerObject = (PlayerObject) playerObjectIterator.next();
        } while (!playerObject.getPlayer().equals(player));

        return playerObject;
    }

    private static double getTrimLevel(Player player) {
        ArrayList<ArmorMeta> metas = new ArrayList<>();

        metas.add((ArmorMeta) Objects.requireNonNull(player.getInventory().getHelmet()).getItemMeta());
        metas.add((ArmorMeta) Objects.requireNonNull(player.getInventory().getChestplate()).getItemMeta());
        metas.add((ArmorMeta) Objects.requireNonNull(player.getInventory().getLeggings()).getItemMeta());
        metas.add((ArmorMeta) Objects.requireNonNull(player.getInventory().getBoots()).getItemMeta());

        double total = 0.0D;
        Iterator metaIterator = metas.iterator();

        while(metaIterator.hasNext()) {
            ArmorMeta meta = (ArmorMeta) metaIterator.next();

            if (meta != null && meta.hasTrim()) {
                String trimMaterial = (Objects.requireNonNull(meta.getTrim())).getMaterial().getKey().toString().substring(10).toLowerCase();

                byte level = -1;

                switch(trimMaterial) {
                    case "copper": level = 1;
                    case "amethyst": level = 1;

                    case "gold": level = 2;
                    case "emerald": level = 2;

                    case "diamond": level = 3;

                    case "netherite": level = 4;

                    default: level = 1;
                }

                switch(level) {
                    case 1: total = 1.0D; break;
                    case 2: total += 2.0D; break;
                    case 3: total += 3.0D; break;
                    case 4: total += 4.0D; break;

                    default: ++total;
                }
            }
        }

        return total / 4;
    }

    private static void silence(PlayerObject player) {
        if (player.getCoolDown() < System.currentTimeMillis() - 60000L) {
            player.setCoolDown(System.currentTimeMillis());

            double level = getTrimLevel(player.getPlayer());
            int duration = (int) ((level - 1.0D) * 100.0D);
            Iterator playerObjectsIterator = playerObjects.iterator();

            while (playerObjectsIterator.hasNext()) {
                PlayerObject object = (PlayerObject) playerObjectsIterator.next();

                Location prevLoc = object.getPlayer().getLocation();

                if (!object.equals(player) && object.getPlayer().getLocation().distance(player.getPlayer().getLocation()) < 20.0D) {
                    object.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, duration, 2));
                    object.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 255));
                    object.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 255));

                    object.getPlayer().sendTitle("English... or Spanish?", "Whoever moves first is gay");

                    while (object.getPlayer().hasPotionEffect(PotionEffectType.DARKNESS)) {
                        object.getPlayer().teleport(prevLoc);
                    }
                }
            }

            player.getPlayer().sendMessage(ChatColor.GREEN + "Successfully used Silence Ability");
            logger.info(player.getPlayer().getName() + " used silence trim ability");
        } else {
            long timeLeft = player.getCoolDown() + 60000 - System.currentTimeMillis();
            player.getPlayer().sendMessage(ChatColor.RED + "You must wait " + timeLeft / 1000L + " seconds before using your trim ability again.");
        }
    }

    private static void vex(PlayerObject player) {
        if (player.getCoolDown() >= System.currentTimeMillis() - 80000L) {
            long timeLeft = player.getCoolDown() + 80000L - System.currentTimeMillis();
            player.getPlayer().sendMessage(ChatColor.RED + "You must wait " + timeLeft / 1000L + " seconds before using your trim ability again.");
        } else {
            player.setCoolDown(System.currentTimeMillis());
            Iterator summonedEntitiesIterator = summonedEntities.iterator();

            while (summonedEntitiesIterator.hasNext()) {
                Entity entity = (Entity) summonedEntitiesIterator.next();

                if (entity.getType().equals(EntityType.VEX) && (Objects.requireNonNull(entity.getCustomName())).contains(player.getPlayer().getName())) {
                    entity.remove();
                }
            }

            double level = getTrimLevel(player.getPlayer()) * 4.0D;
            int vexes = (int) level;

            for (int i = 0; i < vexes; ++i) {
                Entity entity = player.getPlayer().getWorld().spawnEntity(player.getPlayer().getLocation(), EntityType.VEX);

                entity.setCustomName(player.getPlayer().getName() + "'s Vex");
                entity.setCustomNameVisible(true);

                summonedEntities.add(entity);
            }

            if (!entitySummoners.contains(player.getPlayer())) {
                entitySummoners.add(player.getPlayer());
            }

            player.getPlayer().sendMessage(ChatColor.GREEN + "Successfully used Vex Ability");
        }
    }

    public static void activateAbility(Player player) {
        if (hasConsistentTrims(player)) {
            ArmorMeta meta = (ArmorMeta) (Objects.requireNonNull(player.getInventory().getHelmet())).getItemMeta();

            assert meta != null;

            String trim = (Objects.requireNonNull(meta.getTrim())).getPattern().getKey().toString().substring(10);
            PlayerObject playerObject = getPlayerObject(player);

            assert playerObject != null;

            if (trim.equalsIgnoreCase("silence")) {
                silence(playerObject);
            } else if (trim.equalsIgnoreCase("vex")) {
                vex(playerObject);
            } else if (trim.equalsIgnoreCase("sentry")) {

            } else if (trim.equalsIgnoreCase("wild")) {

            } else if (trim.equalsIgnoreCase("coast")) {

            } else if (trim.equalsIgnoreCase("tide")) {

            } else if (trim.equalsIgnoreCase("wayfinder")) {

            } else if (trim.equalsIgnoreCase("shaper")) {

            } else if (trim.equalsIgnoreCase("raiser")) {

            } else if (trim.equalsIgnoreCase("host")) {

            } else if (trim.equalsIgnoreCase("snout")) {

            } else if (trim.equalsIgnoreCase("rib")) {

            } else if (trim.equalsIgnoreCase("spire")) {

            } else if (trim.equalsIgnoreCase("eye")) {

            }
        }
    }
}
