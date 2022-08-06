package jp.simplespace.simplespeedrunner.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import jp.simplespace.simplespeedrunner.SimpleSpeedRunner;
import jp.simplespace.simplespeedrunner.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GameCommand {
    private final Game game;
    public GameCommand(){
        this.game= SimpleSpeedRunner.getGame();
    }
    @Command(
            name = "start",
            desc = "ゲームの開始コマンド"
    )
    @Require("ssr.command.game")
    public void handleStartCommand(@Sender CommandSender sender){
        if(game.getRunner()==null){
            sender.sendMessage(ChatColor.RED+"ランナーが設定されていません。");
            return;
        }
        if(game.getStatus().equals(Game.Status.READY)){
            game.start();
            Bukkit.broadcastMessage(ChatColor.GOLD+"ゲームを強制スタートさせました。");
            return;
        }
        if(!game.ready()){
            sender.sendMessage(ChatColor.RED+"ゲームを開始できませんでした。");
        }
    }
    @Command(
            name = "stop",
            desc = "ゲームの終了コマンド"
    )
    @Require("ssr.command.game")
    public void handleStopCommand(@Sender CommandSender sender){
        if(!game.stop()){
            sender.sendMessage(ChatColor.RED+"現在ゲームが実行されていません。");
            return;
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            player.getInventory().clear();
        }
    }
    @Command(
            name = "setinterval",
            desc = "コンパスの座標更新タイミング設定"
    )
    @Require("ssr.command.game")
    public void handleSetIntervalCommand(@Sender CommandSender sender, int sec){
        FileConfiguration config = SimpleSpeedRunner.getConfiguration();
        config.set("compassInterval",sec);
        game.getPlugin().saveConfig();
        game.getScheduler().setCompassCount(sec);
        Bukkit.broadcastMessage(ChatColor.GOLD+"コンパスの更新間隔を"+sec+"秒に設定しました。");
    }
    @Command(
            name = "setrunner",
            desc = "ランナーを設定"
    )
    @Require("ssr.command.game")
    public void handleSetRunnerCommand(@Sender CommandSender sender, Player player){
        game.setRunner(player);
        Bukkit.broadcastMessage(ChatColor.GOLD+"ランナーを"+player.getName()+"に設定しました。");
    }
    @Command(
            name = "setcompassannounce",
            desc = "コンパス更新の通知設定"
    )
    @Require("ssr.command.game")
    public void handleSetCompassAnnounceCommand(@Sender CommandSender sender, boolean boo){
        FileConfiguration config = SimpleSpeedRunner.getConfiguration();
        config.set("compassAnnounce",boo);
        game.getPlugin().saveConfig();
        Bukkit.broadcastMessage(ChatColor.GOLD+"コンパス更新の通知を"+boo+"に設定しました。");
    }
    @Command(
            name = "setmaxdeath",
            desc = "ランナーの最大死亡可能回数の設定"
    )
    @Require("ssr.command.game")
    public void handleSetMaxDeathCommand(@Sender CommandSender sender, int count){
        FileConfiguration config = SimpleSpeedRunner.getConfiguration();
        config.set("maxDeath",count);
        game.getPlugin().saveConfig();
        Bukkit.broadcastMessage(ChatColor.GOLD+"ランナーの最大死亡可能回数を"+count+"回に設定しました。");
    }
    @Command(
            name = "pause",
            desc = "ゲームを一時停止させる"
    )
    @Require("ssr.command.game")
    public void handlePauseCommand(@Sender CommandSender sender){
        if(game.isPause()){
            game.isPause(false);
            Bukkit.broadcastMessage(ChatColor.GOLD+"ゲームを再開させました。");
        }
        else {
            game.isPause(true);
            Bukkit.broadcastMessage(ChatColor.GOLD+"ゲームを一時停止させました。");
        }
    }
    @Command(
            name = "pvp",
            desc = "PvP装備一式を全員に付与する"
    )
    @Require("ssr.command.game")
    public void handlePvpCommand(@Sender CommandSender sender){
        if(game.getStatus().equals(Game.Status.READY)){
            for(Player player : Bukkit.getOnlinePlayers()){
                PlayerInventory inv = player.getInventory();
                inv.clear();
                inv.setHelmet(new ItemStack(Material.IRON_HELMET));
                inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                inv.setBoots(new ItemStack(Material.IRON_BOOTS));
                inv.setItem(8,new ItemStack(Material.COMPASS));
                inv.setItem(0,new ItemStack(Material.IRON_SWORD));
                inv.setItem(1,new ItemStack(Material.COOKED_BEEF,64));
                inv.setItem(2,new ItemStack(Material.GOLDEN_APPLE,16));
                inv.setHeldItemSlot(0);
            }
        }
        else {
            sender.sendMessage(ChatColor.RED+"ゲームが準備状態のときのみ使用できます。");
        }
    }
}
