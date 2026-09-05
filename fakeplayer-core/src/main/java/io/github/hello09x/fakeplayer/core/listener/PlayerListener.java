package io.github.hello09x.fakeplayer.core.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerListener implements Listener {

    private final FakeplayerManager manager;

    @Inject
    public PlayerListener(FakeplayerManager manager) {
        this.manager = manager;
    }

    /**
     * 玩家蹲伏时取消假人骑乘
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSneak(@NotNull PlayerToggleSneakEvent event) {
        var player = event.getPlayer();
        var passengers = player.getPassengers();
        if (passengers.isEmpty()) {
            return;
        }

        for (var passenger : passengers) {
            if (!(passenger instanceof Player target)) {
                continue;
            }
            if (manager.isFake(target)) {
                player.removePassenger(target);
            }
        }
    }

    /**
     * Prevent players from opening fake player inventories
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEntity(@NotNull org.bukkit.event.player.PlayerInteractEntityEvent event) {
        var clicked = event.getRightClicked();
        if (!(clicked instanceof Player target)) {
            return;
        }
        if (manager.isFake(target)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot open a fake player's inventory.");
        }
    }

}