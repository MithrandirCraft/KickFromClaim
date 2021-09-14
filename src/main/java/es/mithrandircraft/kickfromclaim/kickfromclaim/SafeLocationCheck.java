package es.mithrandircraft.kickfromclaim.kickfromclaim;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class SafeLocationCheck {
    /**
     * Checks if a location is safe (solid ground with 2 breathable blocks)
     *
     * @param location Location to check
     * @return True if location is safe
     */
    public static boolean IsSafeLocation(Location location) {
        Block feet = location.getBlock();
        if (!feet.getType().isSolid()) {
            return false; // solid (will suffocate)
        }
        Block head = feet.getRelative(BlockFace.UP);
        if (head.getType().isSolid()) {
            return false; // solid (will suffocate)
        }
        Block ground = feet.getRelative(BlockFace.DOWN);
        return ground.getType().isSolid(); // not solid
    }
}