package es.mithrandircraft.kickfromclaim;

public class CommandKickFromClaimCooldownObject {
    CommandKickFromClaimCooldownObject(KickFromClaim mainClass, String playerUUID){
        mainClass.getServer().getScheduler().scheduleSyncDelayedTask(mainClass, new Runnable() {
            public void run() {
                mainClass.RemoveCooldown(playerUUID);
            }
        }, mainClass.getConfig().getInt("CommandCooldownSeconds") * 20L);
    }
}