package lee.code.economy;

import lee.code.economy.database.CacheManager;
import lee.code.economy.database.DatabaseManager;
import lee.code.economy.listeners.JoinListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Economy extends JavaPlugin {
    @Getter private static Economy instance;
    @Getter private CacheManager cacheManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        this.databaseManager = new DatabaseManager(this);
        this.cacheManager = new CacheManager(this, databaseManager);
        databaseManager.initialize(false);

        registerListeners();
    }

    @Override
    public void onDisable() {
        databaseManager.closeConnection();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
    }
}
