package com.redemastery.oldapi.pojav.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.redemastery.launcher.R;

import java.io.File;
import java.io.IOException;

import com.redemastery.oldapi.pojav.Tools;
import com.redemastery.oldapi.pojav.extra.ExtraCore;
import com.redemastery.oldapi.pojav.modloaders.ModloaderDownloadListener;
import com.redemastery.oldapi.pojav.modloaders.ModloaderListenerProxy;
import com.redemastery.oldapi.pojav.progresskeeper.ProgressKeeper;

public abstract class ModVersionListFragment<T> extends Fragment implements Runnable, View.OnClickListener, ExpandableListView.OnChildClickListener, ModloaderDownloadListener {
    private final String mExtraTag;
    private ExpandableListView mExpandableListView;
    private ProgressBar mProgressBar;
    private LayoutInflater mInflater;
    private View mRetryView;

    public ModVersionListFragment(String mFragmentTag) {
        super(R.layout.fragment_mod_version_list);
        this.mExtraTag = mFragmentTag + "_proxy";
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView)view.findViewById(R.id.title_textview)).setText(getTitleText());
        mProgressBar = view.findViewById(R.id.mod_dl_list_progress);
        mExpandableListView = view.findViewById(R.id.mod_dl_expandable_version_list);
        mExpandableListView.setOnChildClickListener(this);
        mRetryView = view.findViewById(R.id.mod_dl_retry_layout);
        view.findViewById(R.id.forge_installer_retry_button).setOnClickListener(this);
        ModloaderListenerProxy taskProxy = getTaskProxy();
        if(taskProxy != null) {
            mExpandableListView.setEnabled(false);
            taskProxy.attachListener(this);
        }
        new Thread(this).start();
    }

    @Override
    public void onStop() {
        ModloaderListenerProxy taskProxy = getTaskProxy();
        if(taskProxy != null) taskProxy.detachListener();
        super.onStop();
    }

    @Override
    public void run() {
        try {
            T versions = loadVersionList();
            Tools.runOnUiThread(()->{
                if(versions != null) {
                    mExpandableListView.setAdapter(createAdapter(versions, mInflater));
                }else{
                    mRetryView.setVisibility(View.VISIBLE);
                }
                mProgressBar.setVisibility(View.GONE);
            });
        }catch (IOException e) {
            Tools.runOnUiThread(()-> {
                if (getContext() != null) {
                    Tools.showError(getContext(), e);
                    mRetryView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        mRetryView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        new Thread(this).start();
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        if(ProgressKeeper.hasOngoingTasks()) {
            Toast.makeText(expandableListView.getContext(), R.string.tasks_ongoing, Toast.LENGTH_LONG).show();
            return true;
        }
        Object forgeVersion = expandableListView.getExpandableListAdapter().getChild(i, i1);
        ModloaderListenerProxy taskProxy = new ModloaderListenerProxy();
        Runnable downloadTask = createDownloadTask(forgeVersion, taskProxy);
        setTaskProxy(taskProxy);
        taskProxy.attachListener(this);
        mExpandableListView.setEnabled(false);
        new Thread(downloadTask).start();
        return true;
    }

    @Override
    public void onDownloadFinished(File downloadedFile) {
        Tools.runOnUiThread(()->{
            Context context = requireContext();
            getTaskProxy().detachListener();
            setTaskProxy(null);
            mExpandableListView.setEnabled(true);
            // Read the comment in FabricInstallFragment.onDownloadFinished() to see how this works
            getParentFragmentManager().popBackStackImmediate();
            onDownloadFinished(context, downloadedFile);
        });
    }

    @Override
    public void onDataNotAvailable() {
        Tools.runOnUiThread(()->{
            Context context = requireContext();
            getTaskProxy().detachListener();
            setTaskProxy(null);
            mExpandableListView.setEnabled(true);
            Tools.dialog(context,
                    context.getString(R.string.global_error),
                    context.getString(getNoDataMsg()));
        });
    }

    @Override
    public void onDownloadError(Exception e) {
        Tools.runOnUiThread(()->{
            Context context = requireContext();
            getTaskProxy().detachListener();
            setTaskProxy(null);
            mExpandableListView.setEnabled(true);
            Tools.showError(context, e);
        });
    }

    private void setTaskProxy(ModloaderListenerProxy proxy) {
        ExtraCore.setValue(mExtraTag, proxy);
    }

    private ModloaderListenerProxy getTaskProxy() {
        return (ModloaderListenerProxy) ExtraCore.getValue(mExtraTag);
    }

    public abstract int getTitleText();
    public abstract int getNoDataMsg();

    public abstract T loadVersionList() throws IOException;

    public abstract ExpandableListAdapter createAdapter(T versionList, LayoutInflater layoutInflater);
    public abstract Runnable createDownloadTask(Object selectedVersion, ModloaderListenerProxy listenerProxy);
    public abstract void onDownloadFinished(Context context, File downloadedFile);
}
