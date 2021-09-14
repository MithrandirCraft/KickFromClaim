package es.mithrandircraft.kickfromclaim.kickfromclaim;

import org.bukkit.entity.Player;

public class CommandKickFromClaimCooldownObject {
    CommandKickFromClaimCooldownObject(KickFromClaim mainClass, CommandKickFromClaim kfc, Player player){
        mainClass.getServer().getScheduler().scheduleSyncDelayedTask(mainClass, new Runnable() {
            public void run() {
                kfc.RemoveCooldown(player);
            }
        }, mainClass.getConfig().getInt("CommandCooldownSeconds") * 20L);
    }
}
