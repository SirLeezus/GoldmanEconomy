package lee.code.economy.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.logger.LogBackendType;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lee.code.economy.Economy;
import lee.code.economy.database.tables.PlayerTable;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager {
  private final Economy economy;
  private final Object synchronizedThreadLock = new Object();
  private Dao<PlayerTable, UUID> playerDao;
  private ConnectionSource connectionSource;

  public DatabaseManager(Economy economy) {
    this.economy = economy;
  }

  private String getDatabaseURL() {
    if (!economy.getDataFolder().exists()) economy.getDataFolder().mkdir();
    return "jdbc:sqlite:" + new File(economy.getDataFolder(), "database.db");
  }

  public void initialize(boolean debug) {
    if (!debug) LoggerFactory.setLogBackendFactory(LogBackendType.NULL);
    try {
      final String databaseURL = getDatabaseURL();
      connectionSource = new JdbcConnectionSource(
        databaseURL,
        "test",
        "test",
        DatabaseTypeUtils.createDatabaseType(databaseURL));
      createOrCacheTables();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void closeConnection() {
    try {
      connectionSource.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void createOrCacheTables() throws SQLException {
    final CacheManager cacheManager = economy.getCacheManager();

    //Player data
    TableUtils.createTableIfNotExists(connectionSource, PlayerTable.class);
    playerDao = DaoManager.createDao(connectionSource, PlayerTable.class);

    for (PlayerTable playerTable : playerDao.queryForAll()) {
      cacheManager.getCachePlayers().setPlayerTable(playerTable);
    }
  }

  public void createPlayerTable(PlayerTable playerTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(economy, scheduledTask -> {
        try {
          playerDao.createIfNotExists(playerTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void updatePlayerTable(PlayerTable playerTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(economy, scheduledTask -> {
        try {
          playerDao.update(playerTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void deletePlayerTable(PlayerTable playerTable) {
    synchronized (synchronizedThreadLock) {
      Bukkit.getAsyncScheduler().runNow(economy, scheduledTask -> {
        try {
          playerDao.delete(playerTable);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      });
    }
  }
}
