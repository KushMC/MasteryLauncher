package com.redemastery.oldapi.pojav.utils;

import android.util.Log;

import java.text.ParseException;
import java.util.Date;

import com.redemastery.oldapi.pojav.JMinecraftVersionList;
import com.redemastery.oldapi.pojav.Tools;
import com.redemastery.oldapi.pojav.extra.ExtraConstants;
import com.redemastery.oldapi.pojav.extra.ExtraCore;

/** Class here to help with various stuff to help run lower versions smoothly */
public class OldVersionsUtils {
    /** Lower minecraft versions fare better with opengl 1
     * @param version The version about to be launched
     */
    public static void selectOpenGlVersion(JMinecraftVersionList.Version version){
        // 1309989600 is 2011-07-07  2011-07-07T22:00:00+00:00
        String creationTime = version.time;
        if(!Tools.isValidString(creationTime)){
            ExtraCore.setValue(ExtraConstants.OPEN_GL_VERSION, "2");
            return;
        }

        try {
           Date creationDate = DateUtils.parseReleaseDate(creationTime);
            if(creationDate == null) {
                Log.e("GL_SELECT", "Failed to parse version date");
                ExtraCore.setValue(ExtraConstants.OPEN_GL_VERSION, "2");
                return;
            }
            String openGlVersion =  DateUtils.dateBefore(creationDate, 2011, 6, 8) ? "1" : "2";
            Log.i("GL_SELECT", openGlVersion);
            ExtraCore.setValue(ExtraConstants.OPEN_GL_VERSION, openGlVersion);
        }catch (ParseException exception){
            Log.e("GL_SELECT", exception.toString());
            ExtraCore.setValue(ExtraConstants.OPEN_GL_VERSION, "2");
        }
    }
}
