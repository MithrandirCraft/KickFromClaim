package es.mithrandircraft.kickfromclaim;

import me.ryanhamshire.GriefPrevention.DataStore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LocationFinder {
    /**[Run asynchronously] Callback returns "safe" location outside a claim if found, if not found returns null (Uses expanding iterating circumferences method)*/
    public static void IterateCircumferences(KickFromClaim mainClassAccess, DataStore dataStore, Location circumferenceCenter, World circumferenceWorld, CallbackReturnLocation callback)
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
    public static Location GetRandomCircumferenceLoc(Location circumferenceCenter, int circumferenceRadius, World circumferenceWorld)
    {
        double randomAngle = Math.random()*Math.PI*2;
        return new Location(circumferenceWorld,
                circumferenceCenter.getX() + (Math.cos(randomAngle) * (double)circumferenceRadius),
                120,
                circumferenceCenter.getZ() + (Math.sin(randomAngle) * (double)circumferenceRadius)
        );
    }
}
