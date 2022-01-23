package es.mithrandircraft.kickfromclaim;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class EventPlayerInteractEntity implements Listener {

    private final KickFromClaim mainClassAccess;

    public EventPlayerInteractEntity(KickFromClaim main) { this.mainClassAccess = main; }

    @EventHandler
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent e)
    {
        if(!e.getHand().equals(EquipmentSlot.HAND)) return;

        Player player = e.getPlayer();
        String playerUUID = player.getUniqueId().toString();

        if(mainClassAccess.CheckPlayerInInteractKickMode(playerUUID))
        {
            if(!mainClassAccess.HasCooldown(playerUUID))
            {
                mainClassAccess.AddCooldown(playerUUID);
                Entity entity = e.getRightClicked();

                if(entity instanceof LivingEntity)
                {
                    final DataStore dataStore = GriefPrevention.instance.dataStore;
                    if(entity instanceof Player) //Attempting to kick a player
                    {
                        Player expelledPlayer = (Player)entity; //Target player to be expelled
                        if(expelledPlayer.hasPermission("ClaimKick.Exempt")) return;

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
                                return;
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
                    else //Attempting to kick a mob or animal
                    {
                        Location animalOrMobToExpellLocation = entity.getLocation();
                        Claim claim = dataStore.getClaimAt(animalOrMobToExpellLocation, true, null);
                        if(claim != null && claim.ownerID.equals(player.getUniqueId())) //Target is in a claim + claim is solicitor's
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
