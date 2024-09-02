package com.jebs.armortrims;

import org.bukkit.entity.Player;

public class PlayerObject {
    private final Player player;
    private long coolDown = 0L;

    PlayerObject(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public long getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(long coolDown) {
        this.coolDown = coolDown;
    }
}
