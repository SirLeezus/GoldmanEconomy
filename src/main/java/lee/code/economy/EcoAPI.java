package lee.code.economy;

import java.util.UUID;

public class EcoAPI {

    public static double getBalance(UUID uuid) {
        if (!Economy.getInstance().getCacheManager().getCachePlayers().hasPlayerData(uuid)) return 0;
        else return Economy.getInstance().getCacheManager().getCachePlayers().getBalance(uuid);
    }

    public static void addBalance(UUID uuid, double amount) {
        if (!Economy.getInstance().getCacheManager().getCachePlayers().hasPlayerData(uuid)) return;
        Economy.getInstance().getCacheManager().getCachePlayers().addBalance(uuid, amount);
    }

    public static void setBalance(UUID uuid, double amount) {
        if (!Economy.getInstance().getCacheManager().getCachePlayers().hasPlayerData(uuid)) return;
        Economy.getInstance().getCacheManager().getCachePlayers().setBalance(uuid, amount);
    }

    public static void removeBalance(UUID uuid, double amount) {
        if (!Economy.getInstance().getCacheManager().getCachePlayers().hasPlayerData(uuid)) return;
        Economy.getInstance().getCacheManager().getCachePlayers().removeBalance(uuid, amount);
    }
}

