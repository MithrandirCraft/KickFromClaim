//--------------------------------------------------------------------
// Copyright © Dylan Calaf Latham KickFromClaim
//--------------------------------------------------------------------

package es.mithrandircraft.kickfromclaim.kickfromclaim;

import org.bukkit.plugin.java.JavaPlugin;

public final class KickFromClaim extends JavaPlugin {

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

    }
}
