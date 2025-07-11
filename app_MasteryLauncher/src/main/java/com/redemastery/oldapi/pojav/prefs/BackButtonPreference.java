package com.redemastery.oldapi.pojav.prefs;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

import com.redemastery.launcher.R;

import com.redemastery.oldapi.pojav.extra.ExtraConstants;
import com.redemastery.oldapi.pojav.extra.ExtraCore;

public class BackButtonPreference extends Preference {
    public BackButtonPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressWarnings("unused") public BackButtonPreference(Context context) {
        this(context, null);
    }

    private void init(){
        if(getTitle() == null){
            setTitle(R.string.preference_back_title);
        }
        if(getIcon() == null){
            setIcon(R.drawable.ic_arrow_back_white);
        }
    }


    @Override
    protected void onClick() {
        // It is caught by an ExtraListener in the LauncherActivity
        ExtraCore.setValue(ExtraConstants.BACK_PREFERENCE, "true");
    }
}
