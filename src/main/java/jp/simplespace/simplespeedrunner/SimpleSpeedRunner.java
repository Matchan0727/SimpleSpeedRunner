package jp.simplespace.simplespeedrunner;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import jp.simplespace.simplespeedrunner.command.GameCommand;
import jp.simplespace.simplespeedrunner.game.Game;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleSpeedRunner extends JavaPlugin {
    private static Plugin plugin;
    private static Game game;
    private static FileConfiguration config;
    private static CommandService drink;
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        saveDefaultConfig();
        config = getConfig();
        game = new Game(this);
        drink= Drink.get(this);
        drink.register(new GameCommand(),"speedrun");
        drink.registerCommands();
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static void reloadConfiguration(){
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    public static FileConfiguration getConfiguration(){
        return config;
    }
    public static Game getGame(){
        return game;
    }
}
