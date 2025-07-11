package com.redemastery.oldapi.pojav.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.ExpandableListAdapter;

import com.redemastery.launcher.R;

import java.io.File;
import java.io.IOException;

import com.redemastery.oldapi.pojav.JavaGUILauncherActivity;
import com.redemastery.oldapi.pojav.modloaders.ModloaderListenerProxy;
import com.redemastery.oldapi.pojav.modloaders.OptiFineDownloadTask;
import com.redemastery.oldapi.pojav.modloaders.OptiFineUtils;
import com.redemastery.oldapi.pojav.modloaders.OptiFineVersionListAdapter;

public class OptiFineInstallFragment extends ModVersionListFragment<OptiFineUtils.OptiFineVersions> {
    public static final String TAG = "OptiFineInstallFragment";
    public OptiFineInstallFragment() {
        super(TAG);
    }
    @Override
    public int getTitleText() {
        return R.string.of_dl_select_version;
    }

    @Override
    public int getNoDataMsg() {
        return R.string.of_dl_failed_to_scrape;
    }
    @Override
    public OptiFineUtils.OptiFineVersions loadVersionList() throws IOException {
        return OptiFineUtils.downloadOptiFineVersions();
    }

    @Override
    public ExpandableListAdapter createAdapter(OptiFineUtils.OptiFineVersions versionList, LayoutInflater layoutInflater) {
        return new OptiFineVersionListAdapter(versionList, layoutInflater);
    }

    @Override
    public Runnable createDownloadTask(Object selectedVersion, ModloaderListenerProxy listenerProxy) {
        return new OptiFineDownloadTask((OptiFineUtils.OptiFineVersion) selectedVersion, listenerProxy, requireActivity());
    }

    @Override
    public void onDownloadFinished(Context context, File downloadedFile) {
        Intent modInstallerStartIntent = new Intent(context, JavaGUILauncherActivity.class);
        OptiFineUtils.addAutoInstallArgs(modInstallerStartIntent, downloadedFile);
        context.startActivity(modInstallerStartIntent);
    }
}
