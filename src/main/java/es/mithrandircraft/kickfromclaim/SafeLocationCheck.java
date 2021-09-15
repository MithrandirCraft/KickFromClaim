package es.mithrandircraft.kickfromclaim;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class SafeLocationCheck {
    /**
     * Checks if a block is safe to be on (solid with 2 breathable blocks above)
     *
     * @param block Location to check
     * @return True if block is safe
     */
    public static boolean BlockSafetyCheck(Block block) {
        if (!block.isSolid()) return false; //Base block isn't solid
        Block feet = block.getRelative(BlockFace.UP);
        if (feet.isSolid()) return false; //Solid feet (may suffocate)
        Block head = feet.getRelative(BlockFace.UP);
        if (head.isSolid()) return false; //Solid head (may suffocate)
        if (!block.getRelative(BlockFace.DOWN).isSolid()) return false; //Base block is floating or maybe even tree branch

        //Final check, inside world border? + return
        WorldBorder worldBorder = block.getWorld().getWorldBorder();
        return worldBorder.isInside(block.getLocation());
    }
}
