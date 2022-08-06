package jp.simplespace.simplespeedrunner.game;

import jp.simplespace.simplespeedrunner.SimpleSpeedRunner;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.UUID;

public class Game {
    private Plugin plugin;
    private Status status;
    private GameScheduler scheduler;
    private FileConfiguration config;
    private UUID runner;
    private Listener listener;
    private Location nowCompassLocation;
    private int taskId;
    private int deathCount;
    private boolean isPause;

    public Game(Plugin plugin){
        this.plugin=plugin;
        this.status=Status.STOP;
        this.scheduler=new GameScheduler(this);
        this.config=SimpleSpeedRunner.getConfiguration();
        this.listener=new GameListener(this);
        this.deathCount=0;
    }
    public void setRunner(Player player){
        runner=player.getUniqueId();
    }
    public UUID getRunner(){
        return runner;
    }
    public Plugin getPlugin(){
        return this.plugin;
    }
    public boolean ready(){
        if(status.equals(Status.RUNNING)||status.equals(Status.READY)){
            return false;
        }
        this.status=Status.READY;
        Bukkit.getPluginManager().registerEvents(listener,plugin);
        for(Player player : Bukkit.getOnlinePlayers()){
            player.setHealth(player.getHealthScale());
            player.setFoodLevel(32);
            for(PotionEffect effect : player.getActivePotionEffects()){
                player.removePotionEffect(effect.getType());
            }
            player.getInventory().clear();
            if(!player.getUniqueId().equals(runner)) player.getInventory().setItem(8,new ItemStack(Material.COMPASS));
        }
        Bukkit.getPlayer(runner).getWorld().setTime(0);
        Bukkit.broadcastMessage(ChatColor.GOLD+"まもなくゲームが開始されます。\nランナーはハンターを攻撃してスタートしてください。");
        return true;
    }
    public boolean start(){
        if(status.equals(Status.RUNNING)){
            return false;
        }
        this.status=Status.RUNNING;
        Bukkit.broadcastMessage(ChatColor.GOLD+"ゲームが開始されました。");
        setCompassTargets();
        scheduler.setCount(0);
        scheduler.setCompassCount(config.getInt("compassInterval",10));
        setDeathCount(0);
        taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,scheduler,0L,20L);
        return true;
    }
    public void setCompassTargets(){
        nowCompassLocation=Bukkit.getPlayer(runner).getLocation();
        for(Player player : Bukkit.getOnlinePlayers()){
            player.setCompassTarget(nowCompassLocation);
            PlayerInventory inventory = player.getInventory();
            for(int i=0;i<36;i++){
                ItemStack item = inventory.getItem(i);
                if(item==null){
                    continue;
                }
                if(item.getType().equals(Material.COMPASS)){
                    CompassMeta meta = (CompassMeta) item.getItemMeta();
                    meta.setLodestone(nowCompassLocation);
                    meta.setLodestoneTracked(false);
                    item.setItemMeta(meta);
                }
            }
        }
    }
    public void resetCompassTargets(){
        for(Player player : Bukkit.getOnlinePlayers()){
            player.setCompassTarget(player.getWorld().getSpawnLocation());
        }
    }
    public boolean stop(){
        if(status.equals(Status.STOP)){
            return false;
        }
        this.status=Status.STOP;
        HandlerList.unregisterAll(listener);
        resetCompassTargets();
        plugin.getServer().getScheduler().cancelTask(taskId);
        Bukkit.broadcastMessage(ChatColor.GOLD+"ゲームが終了しました。");
        return true;
    }
    public Status getStatus(){
        return status;
    }
    public Location getNowCompassLocation(){
        return nowCompassLocation;
    }
    public enum Status{
        READY,RUNNING,STOP
    }
    public GameScheduler getScheduler(){
        return scheduler;
    }
    public void setDeathCount(int count){
        this.deathCount=count;
    }
    public int getDeathCount(){
        return this.deathCount;
    }
    public boolean isPause(){
        return this.isPause;
    }
    public void isPause(boolean boo){
        this.isPause=boo;
    }
}
