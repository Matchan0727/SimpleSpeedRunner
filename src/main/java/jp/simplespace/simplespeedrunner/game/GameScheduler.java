package jp.simplespace.simplespeedrunner.game;

import jp.simplespace.simplespeedrunner.SimpleSpeedRunner;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameScheduler implements Runnable {
    private Game game;
    private int count;
    private int compassCount;
    private FileConfiguration config;

    public GameScheduler(Game game){
        this.game=game;
        count=0;
        config=SimpleSpeedRunner.getConfiguration();
        compassCount=config.getInt("compassInterval",10);
    }

    @Override
    public void run() {
        if(game.isPause()){
            for(Player player : Bukkit.getOnlinePlayers()){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.GOLD+"一時停止中..."));
            }
            return;
        }
        boolean hasJoin = false;
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getUniqueId().equals(game.getRunner())){
                hasJoin=true;
                break;
            }
        }
        if(!hasJoin){
            game.isPause(true);
            Bukkit.broadcastMessage(ChatColor.GOLD+"ランナーが退出したためゲームを一時停止させました。");
            return;
        }
        count++;
        int min = count / 60;
        int sec = count % 60;
        String timer = min+"分"+sec+"秒";
        for(Player player : Bukkit.getOnlinePlayers()){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.GOLD+"経過時間: "+timer+"     コンパス更新まで: "+compassCount+"秒     ランナー死亡回数: "+game.getDeathCount()+"/"+config.getInt("maxDeath",5)+"回"));
        }
        compassCount--;
        if(compassCount<=0){
            game.setCompassTargets();
            if(config.getBoolean("compassAnnounce",false)) Bukkit.broadcastMessage(ChatColor.GOLD+"コンパスの座標が更新されました！");
            compassCount=config.getInt("compassInterval",10);
        }
    }
    public void setCount(int count){
        this.count=count;
    }
    public int getCount(){
        return this.count;
    }
    public void setCompassCount(int compassCount){
        this.compassCount=compassCount;
    }
    public int getCompassCount(){
        return this.compassCount;
    }
    public Game getGame(){
        return this.game;
    }
}
