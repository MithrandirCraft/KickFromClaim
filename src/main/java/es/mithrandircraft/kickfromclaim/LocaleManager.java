//--------------------------------------------------------------------
// Copyright © Dylan Calaf Latham KickFromClaim
//--------------------------------------------------------------------

package es.mithrandircraft.kickfromclaim;

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

        localeConfiguration.addDefault("KickCommandCooldown","&aSolo puedes usar este comando una vez cada 5 segundos.");

        localeConfiguration.addDefault("LocationNotFound","&aNo fue posible calcular una localización de expulsión fuera de claims, o las encontradas no eran seguras. Intentalo de nuevo!");
        localeConfiguration.addDefault("PlayerNotInYourClaim","&aEl/la jugador/a especificado no se encuentra en ninguno de tus claims.");
        localeConfiguration.addDefault("PlayerOffline","&aEl/la jugador/a especificado no está conectad@.");

        localeConfiguration.addDefault("SuccessfulKick","&aEl/la jugador/a especificado ha sido expulsad@ de tu claim.");
        localeConfiguration.addDefault("Kicked","&aHas sido expulsad@ de un claim de {Expulsor}");



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






























