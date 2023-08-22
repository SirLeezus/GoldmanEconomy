package lee.code.economy.listeners;

import lee.code.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final Economy economy;

    public JoinListener(Economy economy) {
        this.economy = economy;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!economy.getCacheManager().getCachePlayers().hasPlayerData(e.getPlayer().getUniqueId())) {
            economy.getCacheManager().getCachePlayers().createPlayerData(e.getPlayer().getUniqueId());
        }
    }
}
