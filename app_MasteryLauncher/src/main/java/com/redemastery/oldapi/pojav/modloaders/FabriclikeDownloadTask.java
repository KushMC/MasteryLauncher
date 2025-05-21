package com.redemastery.oldapi.pojav.modloaders;

import com.kdt.mcgui.ProgressLayout;
import com.redemastery.launcher.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import com.redemastery.oldapi.pojav.Tools;
import com.redemastery.oldapi.pojav.progresskeeper.ProgressKeeper;
import com.redemastery.oldapi.pojav.utils.DownloadUtils;
import com.redemastery.oldapi.pojav.utils.FileUtils;
import com.redemastery.oldapi.pojav.value.launcherprofiles.LauncherProfiles;
import com.redemastery.oldapi.pojav.value.launcherprofiles.MinecraftProfile;

public class FabriclikeDownloadTask implements Runnable, Tools.DownloaderFeedback{
    private final ModloaderDownloadListener mModloaderDownloadListener;
    private final FabriclikeUtils mUtils;
    private final String mGameVersion;
    private final String mLoaderVersion;
    private final boolean mCreateProfile;
    public FabriclikeDownloadTask(ModloaderDownloadListener modloaderDownloadListener, FabriclikeUtils utils, String mGameVersion, String mLoaderVersion, boolean mCreateProfile) {
        this.mModloaderDownloadListener = modloaderDownloadListener;
        this.mUtils = utils;
        this.mGameVersion = mGameVersion;
        this.mLoaderVersion = mLoaderVersion;
        this.mCreateProfile = mCreateProfile;
    }

    @Override
    public void run() {
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.fabric_dl_progress);
        try {
            if(runCatching()) mModloaderDownloadListener.onDownloadFinished(null);
            else mModloaderDownloadListener.onDataNotAvailable();
        }catch (IOException e) {
            mModloaderDownloadListener.onDownloadError(e);
        }
        ProgressLayout.clearProgress(ProgressLayout.INSTALL_MODPACK);
    }

    private boolean runCatching() throws IOException{
        String fabricJson = DownloadUtils.downloadString(mUtils.createJsonDownloadUrl(mGameVersion, mLoaderVersion));
        String versionId;
        try {
            JSONObject fabricJsonObject = new JSONObject(fabricJson);
            versionId = fabricJsonObject.getString("id");
        }catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        File versionJsonDir = new File(Tools.DIR_HOME_VERSION, versionId);
        File versionJsonFile = new File(versionJsonDir, versionId+".json");
        FileUtils.ensureDirectory(versionJsonDir);
        Tools.write(versionJsonFile.getAbsolutePath(), fabricJson);
        if(mCreateProfile) {
            LauncherProfiles.load();
            MinecraftProfile fabricProfile = new MinecraftProfile();
            fabricProfile.lastVersionId = versionId;
            fabricProfile.name = mUtils.getName();
            fabricProfile.icon = mUtils.getIconName();
            LauncherProfiles.insertMinecraftProfile(fabricProfile);
            LauncherProfiles.write();
        }
        return true;
    }

    @Override
    public void updateProgress(int curr, int max) {
        int progress100 = (int)(((float)curr / (float)max)*100f);
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, progress100, R.string.fabric_dl_progress, mUtils.getName());
    }
}
