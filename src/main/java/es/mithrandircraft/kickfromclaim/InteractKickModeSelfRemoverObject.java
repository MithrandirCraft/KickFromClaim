package es.mithrandircraft.kickfromclaim;

public class InteractKickModeSelfRemoverObject {
    InteractKickModeSelfRemoverObject(KickFromClaim mainClass, String playerUUID)
    {
        mainClass.getServer().getScheduler().scheduleSyncDelayedTask(mainClass, new Runnable() {
            public void run() {
                mainClass.RemovePlayerFromInteractKickMode(playerUUID);
            }
        }, mainClass.getConfig().getInt("AutoRemoveKickModeSeconds") * 20L);
    }
}
