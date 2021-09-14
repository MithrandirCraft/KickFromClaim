package es.mithrandircraft.kickfromclaim;

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
        Block feet = block.getRelative(BlockFace.UP);
        if (feet.isSolid()) return false; //Solid (will suffocate)
        Block head = feet.getRelative(BlockFace.UP);
        if (head.isSolid()) return false; //Solid (will suffocate)
        //Final check, base block is solid?
        return block.isSolid();
    }
}
