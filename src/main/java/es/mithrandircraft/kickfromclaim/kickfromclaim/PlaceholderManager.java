//--------------------------------------------------------------------
// Copyright © Dylan Calaf Latham KickFromClaim
//--------------------------------------------------------------------

package es.mithrandircraft.kickfromclaim.kickfromclaim;

import org.bukkit.ChatColor;

class PlaceholderManager {

    public static String SubstituteExpulsor(String toReplace, String player)
    {
        toReplace = toReplace.replaceAll("\\{Expulsor}", player);

        return toReplace;
    }
}
