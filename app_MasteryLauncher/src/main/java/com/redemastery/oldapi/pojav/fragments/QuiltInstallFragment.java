package com.redemastery.oldapi.pojav.fragments;

import com.redemastery.oldapi.pojav.modloaders.FabriclikeUtils;
import com.redemastery.oldapi.pojav.modloaders.ModloaderListenerProxy;

public class QuiltInstallFragment extends FabriclikeInstallFragment {

    public static final String TAG = "QuiltInstallFragment";
    private static ModloaderListenerProxy sTaskProxy;

    public QuiltInstallFragment() {
        super(FabriclikeUtils.QUILT_UTILS, TAG);
    }
}
