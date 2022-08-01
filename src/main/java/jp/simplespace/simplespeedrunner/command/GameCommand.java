package jp.simplespace.simplespeedrunner.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import jp.simplespace.simplespeedrunner.SimpleSpeedRunner;
import jp.simplespace.simplespeedrunner.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GameCommand {
    private final Game game;
    public GameCommand(){
        this.game= SimpleSpeedRunner.getGame();
    }
    @Command(
            name = "speedrun",
            aliases = {"sr"},
            desc = "ゲームの基本コマンド"
    )
    @Require("ssr.command.game")
    public void handleCommand(@Sender CommandSender sender){
;    }
    @Command(
            name = "start",
            desc = "ゲームの開始コマンド"
    )
    public void handleStartCommand(@Sender CommandSender sender){
        if(game.getRunner()==null){
            sender.sendMessage(ChatColor.RED+"ランナーが設定されていません。");
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
    public void handleStopCommand(@Sender CommandSender sender){
        if(!game.stop()){
            sender.sendMessage(ChatColor.RED+"現在ゲームが実行されていません。");
        }
    }
    @Command(
            name = "setinterval",
            desc = "コンパスの座標更新タイミング設定"
    )
    public void handleSetIntervalCommand(@Sender CommandSender sender, int sec){
        FileConfiguration config = SimpleSpeedRunner.getConfiguration();
        config.set("compassInterval",sec);
        game.getPlugin().saveConfig();
        game.getScheduler().setCompassCount(sec);
        Bukkit.broadcastMessage(ChatColor.GOLD+"コンパスの座標公開間隔を"+sec+"秒に設定しました。");
    }
    @Command(
            name = "setrunner",
            desc = "ランナーを設定"
    )
    public void handleSetRunnerCommand(@Sender CommandSender sender, Player player){
        game.setRunner(player);
        Bukkit.broadcastMessage(ChatColor.GOLD+"ランナーを"+player.getName()+"に設定しました。");
    }
    @Command(
            name = "setcompassannounce",
            desc = "コンパス更新の通知設定"
    )
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
    public void handleSetMaxDeathCommand(@Sender CommandSender sender, int count){
        FileConfiguration config = SimpleSpeedRunner.getConfiguration();
        config.set("maxDeath",count);
        game.getPlugin().saveConfig();
        Bukkit.broadcastMessage(ChatColor.GOLD+"ランナーの最大死亡可能回数を"+count+"回に設定しました。");
    }
}
