//--------------------------------------------------------------------
// Copyright © Dylan Calaf Latham KickFromClaim
//--------------------------------------------------------------------

package es.mithrandircraft.kickfromclaim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.entity.*;

import java.util.List;

public class CommandKickFromClaim implements CommandExecutor {

    private final KickFromClaim mainClassAccess;

    public CommandKickFromClaim(KickFromClaim main) { this.mainClassAccess = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) //Is player
        {
            Player player = (Player) sender; //Soliciting player
            String playerUUID = player.getUniqueId().toString();

            if(!mainClassAccess.HasCooldown(playerUUID))
            {
                if(args.length == 1) //Kick specified player
                {
                    mainClassAccess.AddCooldown(playerUUID);

                    Player expelledPlayer = Bukkit.getPlayer(args[0]); //Target player to be expelled
                    if(expelledPlayer != null && !expelledPlayer.hasPermission("ClaimKick.Exempt"))
                    {
                        final DataStore dataStore = GriefPrevention.instance.dataStore;
                        Location playerToExpelLocation = expelledPlayer.getLocation();
                        Claim claim = dataStore.getClaimAt(playerToExpelLocation, true, null);
                        if(claim != null) //Target is in a claim
                        {
                            boolean solicitorIsClaimOwnerOrManager = false;

                            if(claim.getOwnerName().equals(player.getName())) //Is claim owner?
                            {
                                solicitorIsClaimOwnerOrManager = true;
                            }
                            else
                            {
                                for (String manager : claim.managers)
                                {
                                    if(manager.equals(playerUUID)) //Is claim manager?
                                    {
                                        solicitorIsClaimOwnerOrManager = true;
                                        break;
                                    }
                                }
                            }

                            if(!solicitorIsClaimOwnerOrManager)
                            {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("NotClaimOwnerOrManager")));
                                return false;
                            }

                            if(!mainClassAccess.getConfig().getBoolean("SendToSpawnInstead"))
                            {
                                //Expel through "expanding iterating circumferences" method
                                Bukkit.getScheduler().runTaskAsynchronously(mainClassAccess, () -> LocationFinder.IterateCircumferences(mainClassAccess, dataStore, player.getLocation(), claim.getGreaterBoundaryCorner().getWorld(), new CallbackReturnLocation()
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
                else if(args.length == 0) //Toggle interact kick mode
                {
                    if(mainClassAccess.CheckPlayerInInteractKickMode(playerUUID))
                    {
                        mainClassAccess.RemovePlayerFromInteractKickMode(playerUUID);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("ExitedInteractKickMode")));
                    }
                    else
                    {
                        mainClassAccess.SetPlayerInInteractKickMode(playerUUID);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("EnteredInteractKickMode")));
                    }
                }
            }
            else
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("KickCooldown")));
            }
        }

        return false;
    }
}
