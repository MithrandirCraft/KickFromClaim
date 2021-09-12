//--------------------------------------------------------------------
// Copyright © Dylan Calaf Latham KickFromClaim
//--------------------------------------------------------------------

package es.mithrandircraft.kickfromclaim.kickfromclaim;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

class LocaleManager {

    private static File localeFile;
    private static FileConfiguration localeConfiguration;

    private static void SetDefaultFileEntries() //Sets the default language entries in english
    {
        //Messages prefix:
        localeConfiguration.addDefault("MessagesPrefix","&b[&aClaimKick&b] ");

        localeConfiguration.addDefault("LocationNotFound","&aNo fue posible encontrar una localización de expulsión fuera de claims, o las existentes no eran seguras.");
        localeConfiguration.addDefault("PlayerNotInYourClaim","&aEl/la jugador/a especificado no se encuentra en ninguno de tus claims.");
        localeConfiguration.addDefault("PlayerOffline","&aEl/la jugador/a especificado no está conectad@.");

        localeConfiguration.addDefault("SuccessfulKick","&aEl/la jugador/a especificado ha sido expulsad@ de tu claim.");


    }

    public static void setup(String pluginName) //Finds or generates custom config file
    {
        localeFile = new File(Bukkit.getServer().getPluginManager().getPlugin(pluginName).getDataFolder(), "locale.yml");

        if(!localeFile.exists())
        {
            try {
                localeFile.createNewFile(); //Creates the file
            } catch(IOException e)
            {
                System.out.print("[KickFromClaim] Could not create locale file.");
            }
        }
        localeConfiguration = YamlConfiguration.loadConfiguration(localeFile);
        SetDefaultFileEntries(); //Sets default entries
    }

    public static FileConfiguration get()
    {
        return localeConfiguration;
    }

    public static void save()
    {
        try {
            localeConfiguration.save(localeFile);
        } catch(IOException e)
        {
            System.out.print("[KickFromClaim] Could not save locale file.");
        }
    }
}






























