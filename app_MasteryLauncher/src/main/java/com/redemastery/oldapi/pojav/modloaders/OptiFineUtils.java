package com.redemastery.oldapi.pojav.modloaders;

import android.content.Intent;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.redemastery.oldapi.pojav.Tools;
import com.redemastery.oldapi.pojav.utils.DownloadUtils;

public class OptiFineUtils {

    public static OptiFineVersions downloadOptiFineVersions() throws IOException {
        try {
            return DownloadUtils.downloadStringCached("https://optifine.net/downloads",
                    "of_downloads_page", new OptiFineScraper());
        }catch (DownloadUtils.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addAutoInstallArgs(Intent intent, File modInstallerJar) {
        intent.putExtra("javaArgs", "-javaagent:"+ Tools.DIR_DATA+"/forge_installer/forge_installers.jar"
                + "=OFNPS" +// No Profile Suppression
                " -jar "+modInstallerJar.getAbsolutePath());
    }

    public static class OptiFineVersions {
        public List<String> minecraftVersions;
        public List<List<OptiFineVersion>> optifineVersions;
    }
    public static class OptiFineVersion {
        public String minecraftVersion;
        public String versionName;
        public String downloadUrl;
    }
}
