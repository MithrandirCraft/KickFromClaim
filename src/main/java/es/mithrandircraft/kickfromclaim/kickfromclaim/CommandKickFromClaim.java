//--------------------------------------------------------------------
// Copyright © Dylan Calaf Latham KickFromClaim
//--------------------------------------------------------------------

package es.mithrandircraft.kickfromclaim.kickfromclaim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Player;

public class CommandKickFromClaim implements CommandExecutor {

    private final KickFromClaim mainClassAccess;

    public CommandKickFromClaim(KickFromClaim main) { this.mainClassAccess = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) //Is player
        {
            final DataStore dataStore = GriefPrevention.instance.dataStore;

            Player player = (Player) sender; //Kick soliciting player

            if(args.length == 1) //Specifies player
            {
                Player expelledPlayer = Bukkit.getPlayer(args[0]); //Target player to be expelled
                if(expelledPlayer != null)
                {
                    Location playerToExpellLocation = expelledPlayer.getLocation();
                    Claim claim = dataStore.getClaimAt(playerToExpellLocation, true, null);
                    if(claim != null && claim.ownerID.equals(player.getUniqueId())) //Target is in a claim + claim is solicitor's
                    {
                        if(!mainClassAccess.getConfig().getBoolean("SendToSpawnInstead"))
                        {
                            //Expell through "iterative circumferences" method
                            Location randomCircumferenceRadiusLoc = null;
                            randomCircumferenceRadiusLoc = IterateCircumferences(dataStore, player.getLocation(), claim.getGreaterBoundaryCorner().getWorld());

                            if(randomCircumferenceRadiusLoc == null)
                            {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("LocationNotFound")));
                            }
                            else
                            {
                                expelledPlayer.teleport(randomCircumferenceRadiusLoc);
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("SuccessfulKick")));
                            }
                        }
                        else
                        {
                            //Expel through "send to spawn" method
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("SuccessfulKick")));
                        }
                    }
                }
            }
            /*
            else if(Check mascot is being looked at) //Mascot being looked at
            {

            }
            */
        }

        return false;
    }

    /**Returns "safe" location outside a claim if found, if not found returns null (Uses expanding iterating circumferences method)*/
    private Location IterateCircumferences(DataStore dataStore, Location circumferenceCenter, World circumferenceWorld)
    {
        int circumferenceRadius = 10;
        Location randomCircumferenceRadiusLoc;
        int maxCircleIterations = mainClassAccess.getConfig().getInt("MaxCircleIterations");
        int checkLocationsPerCircumference = mainClassAccess.getConfig().getInt("CheckLocationsPerCircumference");
        int maxSafeLocationFailures = mainClassAccess.getConfig().getInt("MaxSafeLocationFailures");

        int safeLocationChecks = 0;
        for(int i = 0; i < maxCircleIterations; i++) //Circle radius iteration
        {
            circumferenceRadius *= 2;

            for(int j = 0; j < checkLocationsPerCircumference; j++) //Circunference position + check within claim
            {
                randomCircumferenceRadiusLoc = GetRandomCircumferenceLoc(circumferenceCenter, circumferenceRadius, circumferenceWorld);
                if(dataStore.getClaimAt(randomCircumferenceRadiusLoc, true, null) == null)
                    safeLocationChecks++;
                    if(SafeLocationCheck.IsSafeLocation(randomCircumferenceRadiusLoc)) return randomCircumferenceRadiusLoc;
                    else if(!(safeLocationChecks >= maxSafeLocationFailures)) j = 0;
            }
        }

        return null;
    }

    private Location GetRandomCircumferenceLoc(Location circumferenceCenter, int circumferenceRadius, World circumferenceWorld)
    {
        double randomAngle = Math.random()*Math.PI*2;
        return new Location(circumferenceWorld,
        circumferenceCenter.getX() + (Math.cos(randomAngle) * circumferenceRadius),
        70,
        circumferenceCenter.getZ() + (Math.sin(randomAngle) * circumferenceRadius));
    }
}
