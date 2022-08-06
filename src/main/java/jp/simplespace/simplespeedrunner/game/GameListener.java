package jp.simplespace.simplespeedrunner.game;

import jp.simplespace.simplespeedrunner.SimpleSpeedRunner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class GameListener implements Listener {
    private final Game game;
    public GameListener(Game game){
        this.game=game;
    }
    public Game getGame(){
        return game;
    }
    @EventHandler
    public void onReadyDamage(EntityDamageByEntityEvent event){
        if(!game.getStatus().equals(Game.Status.READY)){
            return;
        }
        if(!((event.getDamager() instanceof Player)&&(event.getEntity() instanceof Player))){
            return;
        }
        Player attacker = (Player) event.getDamager();
        Player target = (Player) event.getEntity();
        if(attacker.getUniqueId().equals(game.getRunner())){
            game.start();
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if(game.getStatus().equals(Game.Status.READY)){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (game.getStatus().equals(Game.Status.READY)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onDropItem(PlayerDropItemEvent event){
        if (game.getStatus().equals(Game.Status.READY)) {
            event.setCancelled(true);
        }
        if(game.getStatus().equals(Game.Status.RUNNING)){
            if(event.getItemDrop().getItemStack().getType().equals(Material.COMPASS) || !event.getPlayer().getUniqueId().equals(game.getRunner())){
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(game.getStatus().equals(Game.Status.RUNNING)){
            player.setCompassTarget(game.getNowCompassLocation());
            if(!player.getInventory().contains(Material.COMPASS) || !player.getUniqueId().equals(game.getRunner())){
                player.getInventory().setItem(8,new ItemStack(Material.COMPASS));
            }
        }
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        if(game.getStatus().equals(Game.Status.RUNNING)){
            if(player.getInventory().contains(Material.COMPASS)){
                player.getInventory().remove(Material.COMPASS);
            }
        }
        if(player.getUniqueId().equals(game.getRunner())){
            game.setDeathCount(game.getDeathCount()+1);
            if(game.getDeathCount()>=SimpleSpeedRunner.getConfiguration().getInt("maxDeath",5)){
                game.stop();
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if(p.getUniqueId().equals(game.getRunner())) p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                    else p.playSound(p.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
                }
                Bukkit.broadcastMessage(ChatColor.GOLD+"ランナーの死亡回数が上限に達したためゲームが終了しました。");
            }
        }
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        if(!game.getStatus().equals(Game.Status.RUNNING)){
            return;
        }
        Player player = event.getPlayer();
        if(!player.getUniqueId().equals(game.getRunner())){
            player.getInventory().setItem(8,new ItemStack(Material.COMPASS));
        }
    }
    @EventHandler
    public void onDeathEntity(EntityDeathEvent event){
        if(!game.getStatus().equals(Game.Status.RUNNING)){
            return;
        }
        if(event.getEntity().getType().equals(EntityType.ENDER_DRAGON)){
            game.stop();
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(!p.getUniqueId().equals(game.getRunner())) p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                else p.playSound(p.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
            }
            Bukkit.broadcastMessage(ChatColor.GOLD+"エンダードラゴンが倒されたためゲームが終了しました。");
        }
    }
}
