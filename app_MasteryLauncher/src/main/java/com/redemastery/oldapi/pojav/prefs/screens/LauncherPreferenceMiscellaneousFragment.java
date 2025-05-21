package com.redemastery.oldapi.pojav.prefs.screens;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.preference.Preference;

import com.redemastery.launcher.R;

import com.redemastery.oldapi.pojav.Tools;
import com.redemastery.oldapi.pojav.utils.GLInfoUtils;

public class LauncherPreferenceMiscellaneousFragment extends LauncherPreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_misc);
        Preference driverPreference = requirePreference("zinkPreferSystemDriver");
        PackageManager packageManager = driverPreference.getContext().getPackageManager();
        boolean supportsTurnip = Tools.checkVulkanSupport(packageManager) && GLInfoUtils.getGlInfo().isAdreno();
        driverPreference.setVisible(supportsTurnip);
    }
}
