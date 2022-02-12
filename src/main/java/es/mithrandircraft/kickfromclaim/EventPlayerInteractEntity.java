package es.mithrandircraft.kickfromclaim;

import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class EventPlayerInteractEntity implements Listener {

    private final KickFromClaim mainClassAccess;

    public EventPlayerInteractEntity(KickFromClaim main) { this.mainClassAccess = main; }

    @EventHandler
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent e)
    {
        if(!e.getHand().equals(EquipmentSlot.HAND)) return;

        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String playerUUIDString = playerUUID.toString();

        if(mainClassAccess.CheckPlayerInInteractKickMode(playerUUIDString))
        {
            if(!mainClassAccess.HasCooldown(playerUUIDString))
            {
                mainClassAccess.AddCooldown(playerUUIDString);
                Entity entity = e.getRightClicked();

                if(entity instanceof LivingEntity)
                {
                    if(entity instanceof Player) //Attempting to kick a player
                    {
                        Player expelledPlayer = (Player)entity; //Target player to be expelled
                        if(expelledPlayer.hasPermission("ClaimKick.Exempt")) return;

                        final Location playerToExpelLocation = expelledPlayer.getLocation();
                        final Core griefDefenderCore = GriefDefender.getCore();
                        final Claim claim = griefDefenderCore.getClaimAt(playerToExpelLocation);

                        if(claim != null) //Target is in a claim
                        {
                            boolean solicitorIsClaimOwnerOrManager = false;

                            if(claim.getOwnerName().equals(player.getName())) //Is kick attempter the claim owner?
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
                                return;
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
                    else //Attempting to kick a mob or animal
                    {
                        Location animalOrMobToExpellLocation = entity.getLocation();
                        final Core griefDefenderCore = GriefDefender.getCore();
                        final Claim claim = griefDefenderCore.getClaimAt(animalOrMobToExpellLocation);

                        if(claim != null && claim.getOwnerName().equals(player.getName())) //Target is in a claim + claim is solicitor's
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
                                        entity.teleportAsync(randomCircumferenceRadiusLoc);
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("SuccessfulKickAnimalOrMob")));
                                    }
                                }
                            }));
                        }
                        else
                        {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("AnimalOrMobNotInYourClaim")));
                        }
                    }
                }
                else
                {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("InvalidInteractionExpulsionEntity")));
                }
            }
            else
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', LocaleManager.get().getString("MessagesPrefix") + LocaleManager.get().getString("KickCooldown")));
            }
        }
    }
}
