package com.redemastery.oldapi.pojav.tasks;

import static com.redemastery.oldapi.pojav.utils.DownloadUtils.downloadString;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.redemastery.launcher.application.MasteryLauncherApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import com.redemastery.oldapi.pojav.JMinecraftVersionList;
import com.redemastery.oldapi.pojav.Tools;
import com.redemastery.oldapi.pojav.prefs.LauncherPreferences;

;

/** Class getting the version list, and that's all really */
public class AsyncVersionList {

    public void getVersionList(@Nullable VersionDoneListener listener, boolean secondPass){
        MasteryLauncherApplication.Companion.getSExecutorService().execute(() -> {
            File versionFile = new File(Tools.DIR_DATA + "/version_list.json");
            JMinecraftVersionList versionList = null;
            try{
                if(!versionFile.exists() || (System.currentTimeMillis() > versionFile.lastModified() + 86400000 )){
                    versionList = downloadVersionList(LauncherPreferences.PREF_VERSION_REPOS);
                }
            }catch (Exception e){
                Log.e("AsyncVersionList", "Refreshing version list failed :" + e);
                e.printStackTrace();
            }

            // Fallback when no network or not needed
            if (versionList == null) {
                try {
                    versionList = Tools.GLOBAL_GSON.fromJson(new JsonReader(new FileReader(versionFile)), JMinecraftVersionList.class);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (JsonIOException | JsonSyntaxException e) {
                    e.printStackTrace();
                    versionFile.delete();
                    if(!secondPass)
                        getVersionList(listener, true);
                }
            }

            if(listener != null)
                listener.onVersionDone(versionList);
        });
    }


    @SuppressWarnings("SameParameterValue")
    private JMinecraftVersionList downloadVersionList(String mirror){
        JMinecraftVersionList list = null;
        try{
            Log.i("ExtVL", "Syncing to external: " + mirror);
            String jsonString = downloadString(mirror);
            list = Tools.GLOBAL_GSON.fromJson(jsonString, JMinecraftVersionList.class);
            Log.i("ExtVL","Downloaded the version list, len=" + list.versions.length);

            // Then save the version list
            //TODO make it not save at times ?
            FileOutputStream fos = new FileOutputStream(Tools.DIR_DATA + "/version_list.json");
            fos.write(jsonString.getBytes());
            fos.close();



        }catch (IOException e){
            Log.e("AsyncVersionList", e.toString());
        }
        return list;
    }

    /** Basic listener, acting as a callback */
    public interface VersionDoneListener{
        void onVersionDone(JMinecraftVersionList versions);
    }

}
