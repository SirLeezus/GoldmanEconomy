package lee.code.economy.database;

import lee.code.economy.Economy;
import lee.code.economy.database.cache.CachePlayers;
import lombok.Getter;

public class CacheManager {

    private final Economy economy;

    @Getter private final CachePlayers cachePlayers;

    public CacheManager(Economy economy, DatabaseManager databaseManager) {
        this.economy = economy;
        this.cachePlayers = new CachePlayers(databaseManager);
    }
}
