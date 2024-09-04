package com.jebs.armortrims.util.commands;

import com.jebs.armortrims.ArmorTrims;
import com.jebs.armortrims.util.PlayerObject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EndCooldownCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (s.equals("ec")) {
             for (PlayerObject i: ArmorTrims.getPlayerObjects()) {
                i.setCoolDown(0);
            }
        }

        return true;
    }
}
