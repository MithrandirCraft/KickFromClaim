//--------------------------------------------------------------------
// Copyright © Dylan Calaf Latham KickFromClaim
//--------------------------------------------------------------------

package es.mithrandircraft.kickfromclaim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandKickFromClaim implements CommandExecutor {

    private final KickFromClaim mainClassAccess;

    Map<Player, CommandKickFromClaimCooldownObject> kfcCommandPerPlayerCooldowns;

    public CommandKickFromClaim(KickFromClaim main)
    {
        this.mainClassAccess = main;
        kfcCommandPerPlayerCooldowns = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) //Is player
        {
            Player player = (Player) sender; //Kick soliciting player

            if(!HasCooldown(player))
            {
                AddCooldown(player);
                final DataStore dataStore = GriefPrevention.instance.dataStore;

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
                                //Expel through "iterative circumferences" method
                                Bukkit.getScheduler().runTaskAsynchronously(mainClassAccess, () -> IterateCircumferences(dataStore, player.getLocation(), claim.getGreaterBoundaryCorner().getWorld(), new CallbackReturnLocation()
                                {
                                    @Override
                                    public void onDone(Location randomCircumferenceRadiusLoc){
                                        if(randomCircumferenceRadiusLoc == null)
                                        {
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("LocationNotFound")));
                                        }
                                        else
                                        {
                                            expelledPlayer.teleportAsync(randomCircumferenceRadiusLoc);
                                            expelledPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + PlaceholderManager.SubstituteExpulsor(LocaleManager.get().getString("Kicked"), player.getName())));
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("SuccessfulKick")));
                                        }
                                    }
                                }));
                            }
                            else
                            {
                                //Expel through "send to spawn" method
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("SuccessfulKick")));
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("PlayerNotInYourClaim")));
                        }
                    }
                }
                /*
                else if(Check mascot is being looked at) //Mascot being looked at
                {

                }
                */
            }
            else
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("KickCommandCooldown")));
            }
        }

        return false;
    }

    /**[Run asynchronously] Callback returns "safe" location outside a claim if found, if not found returns null (Uses expanding iterating circumferences method)*/
    private void IterateCircumferences(DataStore dataStore, Location circumferenceCenter, World circumferenceWorld, CallbackReturnLocation callback)
    {
        int circumferenceRadius = 10;
        Location randomCircumferenceRadiusLoc = null;
        int maxCircleIterations = mainClassAccess.getConfig().getInt("MaxCircleIterations");
        int checkLocationsPerCircumference = mainClassAccess.getConfig().getInt("CheckLocationsPerCircumference");
        int maxSafeLocationFailures = mainClassAccess.getConfig().getInt("MaxSafeLocationFailures");
        int safeLocationChecks = 0;
        outer: for(int i = 0; i < maxCircleIterations; i++) //Circle radius iteration
        {
            circumferenceRadius *= 2;

            for(int j = 0; j < checkLocationsPerCircumference; j++) //Circumference position + check within claim
            {
                randomCircumferenceRadiusLoc = GetRandomCircumferenceLoc(circumferenceCenter, circumferenceRadius, circumferenceWorld);
                System.out.println(dataStore.getClaimAt(randomCircumferenceRadiusLoc, true, null));
                if(dataStore.getClaimAt(randomCircumferenceRadiusLoc, true, null) == null)
                {
                    safeLocationChecks++;
                    Block highestBlock = circumferenceWorld.getHighestBlockAt(randomCircumferenceRadiusLoc);
                    if(SafeLocationCheck.BlockSafetyCheck(highestBlock))
                    {
                        randomCircumferenceRadiusLoc = new Location(circumferenceWorld, highestBlock.getX() + 0.5, highestBlock.getY() + 1, highestBlock.getZ() + 0.5);
                        break outer;
                    }
                    else if(!(safeLocationChecks >= maxSafeLocationFailures)) j = 0; //Reset circumference position search unless it's the last safe check
                }
            }

            if(i == maxCircleIterations - 1) randomCircumferenceRadiusLoc = null; //Last iteration and no appropriate position found
        }

        Location finalRandomCircumferenceRadiusLoc = randomCircumferenceRadiusLoc;
        Bukkit.getScheduler().runTask(mainClassAccess, new Runnable() { //Callback to main thread
            @Override
            public void run() {
                callback.onDone(finalRandomCircumferenceRadiusLoc);
            }
        });
    }

    /**Returns a random Location from a circumference of circumferenceRadius and circunferenceCenter*/
    private Location GetRandomCircumferenceLoc(Location circumferenceCenter, int circumferenceRadius, World circumferenceWorld)
    {
        double randomAngle = Math.random()*Math.PI*2;
        return new Location(circumferenceWorld,
        circumferenceCenter.getX() + (Math.cos(randomAngle) * (double)circumferenceRadius),
        0,
        circumferenceCenter.getZ() + (Math.sin(randomAngle) * (double)circumferenceRadius)
        );
    }

    private void AddCooldown(Player player)
    {
        kfcCommandPerPlayerCooldowns.put(player, new CommandKickFromClaimCooldownObject(mainClassAccess, this, player));
    }
    protected void RemoveCooldown(Player player)
    {
        kfcCommandPerPlayerCooldowns.remove(player);
    }
    /**Returns true if command is in cooldown for player, else returns false*/
    private boolean HasCooldown(Player player)
    {
        return kfcCommandPerPlayerCooldowns.get(player) != null;
    }
}
