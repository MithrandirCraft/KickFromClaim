//--------------------------------------------------------------------
// Copyright © Dylan Calaf Latham KickFromClaim
//--------------------------------------------------------------------

package es.mithrandircraft.kickfromclaim;

import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.*;

import java.util.UUID;

public class CommandKickFromClaim implements CommandExecutor {

    private final KickFromClaim mainClassAccess;

    public CommandKickFromClaim(KickFromClaim main) { this.mainClassAccess = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) //Is player
        {
            Player player = (Player) sender; //Soliciting player
            UUID playerUUID = player.getUniqueId();
            String playerUUIDString = playerUUID.toString();

            if(!mainClassAccess.HasCooldown(playerUUIDString))
            {
                if(args.length == 1) //Kick specified player
                {
                    mainClassAccess.AddCooldown(playerUUIDString);

                    Player expelledPlayer = Bukkit.getPlayer(args[0]); //Target player to be expelled
                    if(expelledPlayer != null && !expelledPlayer.hasPermission("ClaimKick.Exempt"))
                    {
                        final Location playerToExpelLocation = expelledPlayer.getLocation();
                        final Core griefDefenderCore = GriefDefender.getCore();
                        final Claim claim = griefDefenderCore.getClaimAt(playerToExpelLocation);

                        if(claim != null) //Target is in a claim
                        {
                            boolean solicitorIsClaimOwnerOrManager = false;

                            if(claim.getOwnerName().equals(player.getName())) //Is claim owner?
                            {
                                solicitorIsClaimOwnerOrManager = true;
                            }
                            else
                            {
                                for (UUID trustee : claim.getUserTrusts())
                                {
                                    if(trustee.equals(playerUUID)) //Is kick attempter trusted in claim?
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
                                Bukkit.getScheduler().runTaskAsynchronously(mainClassAccess, () -> LocationFinder.IterateCircumferences(mainClassAccess, griefDefenderCore, player.getLocation(), claim.getWorldUniqueId(), new CallbackReturnLocation()
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
                    if(mainClassAccess.CheckPlayerInInteractKickMode(playerUUIDString))
                    {
                        mainClassAccess.RemovePlayerFromInteractKickMode(playerUUIDString);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("ExitedInteractKickMode")));
                    }
                    else
                    {
                        mainClassAccess.SetPlayerInInteractKickMode(playerUUIDString);
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
