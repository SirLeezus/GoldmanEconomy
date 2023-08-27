package lee.code.economy.database.cache;

import com.google.common.util.concurrent.AtomicDouble;
import lee.code.economy.database.DatabaseManager;
import lee.code.economy.database.handlers.DatabaseHandler;
import lee.code.economy.database.tables.PlayerTable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CachePlayers extends DatabaseHandler {
  private final ConcurrentHashMap<UUID, PlayerTable> playersCache = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<UUID, AtomicDouble> balanceCache = new ConcurrentHashMap<>();

  public CachePlayers(DatabaseManager databaseManager) {
    super(databaseManager);
  }

  private void cachePlayerTable(PlayerTable playerTable) {
    playersCache.put(playerTable.getUniqueId(), playerTable);
  }

  private void cacheAtomicBalance(PlayerTable playerTable) {
    balanceCache.put(playerTable.getUniqueId(), new AtomicDouble(playerTable.getBalance()));
  }

  public void createPlayerData(UUID uuid) {
    final PlayerTable playerTable = new PlayerTable(uuid);
    cachePlayerTable(playerTable);
    cacheAtomicBalance(playerTable);
    createPlayerDatabase(playerTable);
  }

  private PlayerTable getPlayerTable(UUID uuid) {
    return playersCache.get(uuid);
  }

  private AtomicDouble getAtomicBalance(UUID uuid) {
    return balanceCache.get(uuid);
  }

  public void setPlayerTable(PlayerTable playerTable) {
    cachePlayerTable(playerTable);
    cacheAtomicBalance(playerTable);
  }

  public boolean hasPlayerData(UUID uuid) {
    return playersCache.containsKey(uuid);
  }

  public void addBalance(UUID uuid, double amount) {
    final PlayerTable playerTable = getPlayerTable(uuid);
    final AtomicDouble atomicBalance = getAtomicBalance(uuid);
    atomicBalance.addAndGet(amount);
    playerTable.setBalance(atomicBalance.get());
    updatePlayerDatabase(playerTable);
  }

  public void setBalance(UUID uuid, double amount) {
    final PlayerTable playerTable = getPlayerTable(uuid);
    final AtomicDouble atomicBalance = getAtomicBalance(uuid);
    atomicBalance.set(amount);
    playerTable.setBalance(atomicBalance.get());
    updatePlayerDatabase(playerTable);
  }

  public void removeBalance(UUID uuid, double amount) {
    final PlayerTable playerTable = getPlayerTable(uuid);
    final AtomicDouble atomicBalance = getAtomicBalance(uuid);
    atomicBalance.updateAndGet(currentValue -> Math.max(currentValue - amount, 0));
    playerTable.setBalance(atomicBalance.get());
    updatePlayerDatabase(playerTable);
  }

  public double getBalance(UUID uuid) {
    if (!balanceCache.containsKey(uuid)) return 0;
    return balanceCache.get(uuid).get();
  }

  public Map<UUID, Double> getBalances() {
    return balanceCache.entrySet()
      .stream()
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        entry -> entry.getValue().get()
      ));
  }
}
