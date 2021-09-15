//--------------------------------------------------------------------
// Copyright © Dylan Calaf Latham KickFromClaim
//--------------------------------------------------------------------

package es.mithrandircraft.kickfromclaim;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class KickFromClaim extends JavaPlugin {

    Map<String, CommandKickFromClaimCooldownObject> kickPerPlayerCooldowns = new HashMap<>();
    private final Map<String, InteractKickModeSelfRemoverObject> interactKickModePlayerUUIDs = new HashMap<>();

    @Override
    public void onEnable() {

        //Config load:
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //Locale load:
        LocaleManager.setup(getName());
        LocaleManager.get().options().copyDefaults(true);
        LocaleManager.save();

        //Commands:
        getCommand("ClaimKick").setExecutor(new CommandKickFromClaim(this));

        //Event registring:
        getServer().getPluginManager().registerEvents(new EventPlayerInteractEntity(this), this);
    }

    //Kick cooldown controls

    public void AddCooldown(String playerUUID)
    {
        kickPerPlayerCooldowns.put(playerUUID, new CommandKickFromClaimCooldownObject(this, playerUUID));
    }
    public void RemoveCooldown(String playerUUID)
    {
        kickPerPlayerCooldowns.remove(playerUUID);
    }
    /**Returns true if command is in cooldown for player, else returns false*/
    public boolean HasCooldown(String playerUUID)
    {
        return kickPerPlayerCooldowns.get(playerUUID) != null;
    }

    //Kick mode controls

    public void SetPlayerInInteractKickMode(String playerUUID)
    {
        interactKickModePlayerUUIDs.put(playerUUID, new InteractKickModeSelfRemoverObject(this, playerUUID));
    }
    public void RemovePlayerFromInteractKickMode(String playerUUID)
    {
        interactKickModePlayerUUIDs.remove(playerUUID);
    }
    public boolean CheckPlayerInInteractKickMode(String playerUUID)
    {
        return interactKickModePlayerUUIDs.containsKey(playerUUID);
    }
}
