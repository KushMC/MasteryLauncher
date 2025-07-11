package com.redemastery.oldapi.pojav.value.launcherprofiles;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.redemastery.oldapi.pojav.Tools;
import com.redemastery.oldapi.pojav.prefs.LauncherPreferences;

public class LauncherProfiles {
    public static MinecraftLauncherProfiles mainProfileJson;
    private static final File launcherProfilesFile = new File(Tools.GAME_PROFILES_FILE);

    /** Reload the profile from the file, creating a default one if necessary */
    public static void load(){
        if (launcherProfilesFile.exists()) {
            try {
                mainProfileJson = Tools.GLOBAL_GSON.fromJson(Tools.read(launcherProfilesFile.getAbsolutePath()), MinecraftLauncherProfiles.class);
            } catch (IOException e) {
                Log.e(LauncherProfiles.class.toString(), "Failed to load file: ", e);
                throw new RuntimeException(e);
            }
        }

        // Fill with default
        if (mainProfileJson == null) mainProfileJson = new MinecraftLauncherProfiles();
        if (mainProfileJson.profiles == null) mainProfileJson.profiles = new HashMap<>();
        if (mainProfileJson.profiles.isEmpty())
            mainProfileJson.profiles.put(UUID.randomUUID().toString(), MinecraftProfile.getDefaultProfile());

        // Normalize profile names from mod installers
        if(normalizeProfileIds(mainProfileJson)){
            write();
            load();
        }
    }

    /** Apply the current configuration into a file */
    public static void write() {
        try {
            Tools.write(launcherProfilesFile.getAbsolutePath(), mainProfileJson.toJson());
        } catch (IOException e) {
            Log.e(LauncherProfiles.class.toString(), "Failed to write profile file", e);
            throw new RuntimeException(e);
        }
    }

    public static @NonNull MinecraftProfile getCurrentProfile() {
        if(mainProfileJson == null) LauncherProfiles.load();
        String defaultProfileName = LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, "");
        MinecraftProfile profile = mainProfileJson.profiles.get(defaultProfileName);
        if(profile == null) throw new RuntimeException("The current profile stopped existing :(");
        return profile;
    }

    /**
     * Insert a new profile into the profile map
     * @param minecraftProfile the profile to insert
     */
    public static void insertMinecraftProfile(MinecraftProfile minecraftProfile) {
        mainProfileJson.profiles.put(getFreeProfileKey(), minecraftProfile);
    }

    /**
     * Pick an unused normalized key to store a new profile with
     * @return an unused key
     */
    public static String getFreeProfileKey() {
        Map<String, MinecraftProfile> profileMap = mainProfileJson.profiles;
        String freeKey = UUID.randomUUID().toString();
        while(profileMap.get(freeKey) != null) freeKey = UUID.randomUUID().toString();
        return freeKey;
    }

    /**
     * For all keys to be UUIDs, effectively isolating profile created by installers
     * This avoids certain profiles to be erased by the installer
     * @return Whether some profiles have been normalized
     */
    private static boolean normalizeProfileIds(MinecraftLauncherProfiles launcherProfiles){
        boolean hasNormalized = false;
        ArrayList<String> keys = new ArrayList<>();

        // Detect denormalized keys
        for(String profileKey : launcherProfiles.profiles.keySet()){
            try{
                if(!UUID.fromString(profileKey).toString().equals(profileKey)) keys.add(profileKey);
            }catch (IllegalArgumentException exception){
                keys.add(profileKey);
                Log.w(LauncherProfiles.class.toString(), "Illegal profile uuid: " + profileKey);
            }
        }

        // Swap the new keys
        for(String profileKey : keys){
            MinecraftProfile currentProfile = launcherProfiles.profiles.get(profileKey);
            insertMinecraftProfile(currentProfile);
            launcherProfiles.profiles.remove(profileKey);
            hasNormalized = true;
        }

        return hasNormalized;
    }
}
