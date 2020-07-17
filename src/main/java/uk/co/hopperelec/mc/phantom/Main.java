package uk.co.hopperelec.mc.phantom;

import net.citizensnpcs.trait.Poses;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.Gravity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import net.citizensnpcs.api.CitizensAPI;

import java.util.Objects;
import java.util.Random;

public final class Main extends JavaPlugin implements Listener {
    String pre = "§4MegaPvP§7: ";
    String myname = "HopperElecYT";
    World world;
    NPC mega;
    boolean locked = true;
    int stage = 0;
    int loops;
    Random random = new Random();

    @Override
    public void onEnable() {
        world = Bukkit.getWorlds().get(0);
        NPCRegistry registry = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());
        mega = registry.createNPC(EntityType.PLAYER,"MegaPvP");
        mega.getTrait(Gravity.class).gravitate(true);
        mega.spawn(new Location(world,0,world.getHighestBlockYAt(0,7)+3,7));
        getServer().getPluginManager().registerEvents(this,this);}

    @Override
    public void onDisable() {
        if (mega.getStoredLocation().getBlock().getType() == Material.OAK_STAIRS) {
            mega.getStoredLocation().getBlock().setType(Material.AIR);}
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().getBlock().getType() == Material.OAK_STAIRS) {
                player.getLocation().getBlock().setType(Material.AIR);}}}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.setLevel(0); p.setExp(0);

        if (!p.getDisplayName().equals(myname)) {
            p.setGameMode(GameMode.ADVENTURE); p.setFlying(false); p.setOp(false);
            Location loc = new Location(world,0,world.getHighestBlockYAt(0,0),0);
            loc.setDirection(new Vector(0,world.getHighestBlockYAt(0,7)+2,7).subtract(new Vector(0,world.getHighestBlockYAt(0,0)+1,0)));
            p.teleport(loc);

            loops = 0;
            new BukkitRunnable() {
                @Override
                public void run() {
                    loops += 1;
                    if (stage == 0) {
                        if (loops <= 45) {
                            mega.teleport(mega.getStoredLocation().add(0.0,-0.0625,0.0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            mega.faceLocation(new Location(world,0,world.getHighestBlockYAt(0,0)+1,0));}
                        else if (loops == 46) {
                            p.sendMessage(pre+"Hello, Phantom. I've been expecting you for a while.");
                            mega.getTrait(Gravity.class).gravitate(false);}
                        else if (loops == 60) {
                            p.sendMessage(pre+"And... I have some explaining to do.");
                            mega.faceLocation(new Location(world,0,world.getHighestBlockYAt(0,0)-1,0));}
                        else if (loops == 74) {
                            stage = 1;
                            p.sendMessage(pre+"Come closer.");
                            mega.faceLocation(mega.getStoredLocation().add(0,0,-1));}}
                    else if (stage == 2) {
                        if (loops == 1) {
                            mega.faceLocation(p.getLocation());}
//                        else if (loops == 5) {
//                            mega.getStoredLocation().getBlock().setType(Material.OAK_STAIRS);
//                            p.getLocation().getBlock().setType(Material.OAK_STAIRS);}
                        else if (loops == 15) {
                            p.sendMessage(pre+"I am...");}
                        else if (loops == 30) {
                            p.sendMessage(pre+"not you.");}
                        else if (loops == 50) {
                            p.sendMessage(pre+"Congrats lol that's your prize hope it was worth it");}
                        else if (loops >= 50 && loops <= 99) {
                            mega.faceLocation(new Location(world,random.nextInt(128)-64,random.nextInt(256),random.nextInt(128)-64));}
                        else if (loops == 100) {
                            p.kickPlayer("Ok I'll stop calling you Mega now haha");}}}
            }.runTaskTimer(this,40,4);}}

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (!event.getPlayer().getDisplayName().equals(myname)) {
            if (mega.getStoredLocation().getBlock().getType() == Material.OAK_STAIRS) {
                mega.getStoredLocation().getBlock().setType(Material.AIR);}
            if (event.getPlayer().getLocation().getBlock().getType() == Material.OAK_STAIRS) {
                event.getPlayer().getLocation().getBlock().setType(Material.AIR);}
            stage = 0;
            mega.getTrait(Gravity.class).gravitate(true);
            mega.teleport(new Location(world,0,world.getHighestBlockYAt(0,7)+3,7), PlayerTeleportEvent.TeleportCause.PLUGIN);}}

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.getPlayer().getDisplayName().equals(myname) && locked) {
            Location loc = event.getFrom();
            loc.setY(Objects.requireNonNull(event.getTo()).getY());
            if (event.getTo().getPitch() <= 45 && event.getTo().getPitch() >= -45) {
                loc.setPitch(Objects.requireNonNull(event.getTo()).getPitch());}
            if (event.getTo().getYaw() <= 45 && event.getTo().getYaw() >= -45) {
                loc.setYaw(Objects.requireNonNull(event.getTo()).getYaw());}
            if (stage == 1) {
                if (event.getTo().getZ() > event.getFrom().getZ()) {
                    loc.setZ(event.getTo().getZ());
                    if (event.getTo().getZ() >= 4) {
                        stage = 2; loops = 0;}}
                if (event.getTo().getX() <= 1 && event.getTo().getX() >= -1) {
                    loc.setX(event.getTo().getX());}}
            event.getPlayer().teleport(loc);}}

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!((Player) event.getEntity()).getDisplayName().equals(myname)) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);}}}}

    @EventHandler
    public void onPlayerTalk(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().getDisplayName().equals(myname)) {
            event.setMessage("lmao thanks so much for making me this plugin it's awesome I'm so happy you made this for me");}}
}
