package com.redemastery.oldapi.pojav.modloaders;

import android.app.Activity;

import com.kdt.mcgui.ProgressLayout;
import com.redemastery.launcher.R;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.redemastery.oldapi.pojav.JMinecraftVersionList;
import com.redemastery.oldapi.pojav.Tools;
import com.redemastery.oldapi.pojav.progresskeeper.ProgressKeeper;
import com.redemastery.oldapi.pojav.tasks.AsyncMinecraftDownloader;
import com.redemastery.oldapi.pojav.tasks.MinecraftDownloader;
import com.redemastery.oldapi.pojav.utils.DownloadUtils;

public class OptiFineDownloadTask implements Runnable, Tools.DownloaderFeedback, AsyncMinecraftDownloader.DoneListener {
    private static final Pattern sMcVersionPattern = Pattern.compile("([0-9]+)\\.([0-9]+)\\.?([0-9]+)?");
    private final OptiFineUtils.OptiFineVersion mOptiFineVersion;
    private final File mDestinationFile;
    private final ModloaderDownloadListener mListener;
    private final Object mMinecraftDownloadLock = new Object();
    private Throwable mDownloaderThrowable;
    private final Activity activity;

    public OptiFineDownloadTask(OptiFineUtils.OptiFineVersion mOptiFineVersion, ModloaderDownloadListener mListener, Activity activity) {
        this.mOptiFineVersion = mOptiFineVersion;
        this.mDestinationFile = new File(Tools.DIR_CACHE, "optifine-installer.jar");
        this.mListener = mListener;
        this.activity = activity;
    }

    @Override
    public void run() {
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.of_dl_progress, mOptiFineVersion.versionName);
        try {
            if(runCatching()) mListener.onDownloadFinished(mDestinationFile);
        }catch (IOException e) {
            mListener.onDownloadError(e);
        }
        ProgressLayout.clearProgress(ProgressLayout.INSTALL_MODPACK);
    }

    public boolean runCatching() throws IOException {
        String downloadUrl = scrapeDownloadsPage();
        if(downloadUrl == null) return false;
        String minecraftVersion = determineMinecraftVersion();
        if(minecraftVersion == null) return false;
        if(!downloadMinecraft(minecraftVersion)) {
            if(mDownloaderThrowable instanceof Exception) {
                mListener.onDownloadError((Exception) mDownloaderThrowable);
            }else {
                Exception exception = new Exception(mDownloaderThrowable);
                mListener.onDownloadError(exception);
            }
            return false;
        }
        DownloadUtils.downloadFileMonitored(downloadUrl, mDestinationFile, new byte[8192], this);
        return true;
    }

    public String scrapeDownloadsPage() throws IOException{
        String scrapeResult = OFDownloadPageScraper.run(mOptiFineVersion.downloadUrl);
        if(scrapeResult == null) mListener.onDataNotAvailable();
        return scrapeResult;
    }

    public String determineMinecraftVersion() {
        Matcher matcher = sMcVersionPattern.matcher(mOptiFineVersion.minecraftVersion);
        if(matcher.find()) {
            StringBuilder mcVersionBuilder = new StringBuilder();
            mcVersionBuilder.append(matcher.group(1));
            mcVersionBuilder.append('.');
            mcVersionBuilder.append(matcher.group(2));
            String thirdGroup = matcher.group(3);
            if(thirdGroup != null && !thirdGroup.isEmpty() && !"0".equals(thirdGroup)) {
                mcVersionBuilder.append('.');
                mcVersionBuilder.append(thirdGroup);
            }
            return mcVersionBuilder.toString();
        }else{
            mListener.onDataNotAvailable();
            return null;
        }
    }

    public boolean downloadMinecraft(String minecraftVersion) {
        // the string is always normalized
        JMinecraftVersionList.Version minecraftJsonVersion = AsyncMinecraftDownloader.getListedVersion(minecraftVersion);
        if(minecraftJsonVersion == null) return false;
        try {
            synchronized (mMinecraftDownloadLock) {
                new MinecraftDownloader().start(activity, minecraftJsonVersion, minecraftVersion, this);
                mMinecraftDownloadLock.wait();
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mDownloaderThrowable == null;
    }

    @Override
    public void updateProgress(int curr, int max) {
        int progress100 = (int)(((float)curr / (float)max)*100f);
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, progress100, R.string.of_dl_progress, mOptiFineVersion.versionName);
    }

    @Override
    public void onDownloadDone() {
        synchronized (mMinecraftDownloadLock) {
            mDownloaderThrowable = null;
            mMinecraftDownloadLock.notifyAll();
        }
    }

    @Override
    public void onDownloadFailed(Throwable throwable) {
        synchronized (mMinecraftDownloadLock) {
            mDownloaderThrowable = throwable;
            mMinecraftDownloadLock.notifyAll();
        }
    }

    @Override
    public void onStartDownload() {

    }
}
