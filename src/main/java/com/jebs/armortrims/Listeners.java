package com.jebs.armortrims;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Listeners implements Listener {
    @EventHandler
    public void onPlayerCrouch(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            Player player = event.getPlayer();
            ArmorTrims.activateAbility(player);
        }
    }
}
