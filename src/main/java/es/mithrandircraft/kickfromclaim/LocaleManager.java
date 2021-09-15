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

        localeConfiguration.addDefault("KickCooldown","&4Solo puedes expulsar jugadores/as y mascotas una vez cada 5 segundos.");

        localeConfiguration.addDefault("LocationNotFound","&4No fue posible calcular una localización de expulsión fuera de claims, o las encontradas no eran seguras. Intentalo de nuevo!");
        localeConfiguration.addDefault("PlayerNotInYourClaim","&4El/la jugador/a especificado no se encuentra en ninguno de tus claims.");
        localeConfiguration.addDefault("PlayerOffline","&4El/la jugador/a especificado no está conectad@.");

        localeConfiguration.addDefault("SuccessfulKick","&aEl/la jugador/a especificado ha sido expulsad@ de tu/s claim/s. Utiliza &e/setclaimflag noenterplayer <nombreDeJugador/a> &asi no quieres que vuelva a entrar.");
        localeConfiguration.addDefault("Kicked","&4Has sido expulsad@ de un claim de {Expulsor}");

        localeConfiguration.addDefault("EnteredInteractKickMode","&aEntraste en modo expulsión. Haz click derecho sobre jugadores/as o mascotas que quieras expulsar de tu claim. Sal del modo expulsión utilizando &e/ck, kfc, claimkick &ade nuevo");
        localeConfiguration.addDefault("ExitedInteractKickMode","&4Saliste del modo expulsión.");

        localeConfiguration.addDefault("AnimalOrMobNotInYourClaim","&4Animal o mob no se encuentra en tu claim.");
        localeConfiguration.addDefault("InvalidInteractionExpulsionEntity","&4Intentaste expulsar a una entidad invalida, solo es posible expulsar Animales o Mobs. Sal del modo expulsión utilizando &e/ck, kfc, claimkick &ade nuevo");

        localeConfiguration.addDefault("SuccessfulKickAnimalOrMob","&aAnimal o mob expulsados de tu claim.");
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






























