package com.redemastery.oldapi.pojav.modloaders.modpacks.api;


import android.content.Context;

import com.kdt.mcgui.ProgressLayout;
import com.redemastery.launcher.R;
import com.redemastery.launcher.application.MasteryLauncherApplication;

import java.io.IOException;

import com.redemastery.oldapi.pojav.Tools;
import com.redemastery.oldapi.pojav.modloaders.modpacks.models.ModDetail;
import com.redemastery.oldapi.pojav.modloaders.modpacks.models.ModItem;
import com.redemastery.oldapi.pojav.modloaders.modpacks.models.SearchFilters;
import com.redemastery.oldapi.pojav.modloaders.modpacks.models.SearchResult;

;

/**
 *
 */
public interface ModpackApi {

    /**
     * @param searchFilters Filters
     * @param previousPageResult The result from the previous page
     * @return the list of mod items from specified offset
     */
    SearchResult searchMod(SearchFilters searchFilters, SearchResult previousPageResult);

    /**
     * @param searchFilters Filters
     * @return A list of mod items
     */
    default SearchResult searchMod(SearchFilters searchFilters) {
        return searchMod(searchFilters, null);
    }

    /**
     * Fetch the mod details
     * @param item The moditem that was selected
     * @return Detailed data about a mod(pack)
     */
    ModDetail getModDetails(ModItem item);

    /**
     * Download and install the mod(pack)
     * @param modDetail The mod detail data
     * @param selectedVersion The selected version
     */
    default void handleInstallation(Context context, ModDetail modDetail, int selectedVersion) {
        // Doing this here since when starting installation, the progress does not start immediately
        // which may lead to two concurrent installations (very bad)
        ProgressLayout.setProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.global_waiting);
        MasteryLauncherApplication.Companion.getSExecutorService().execute(() -> {
            try {
                ModLoader loaderInfo = installMod(modDetail, selectedVersion);
                if (loaderInfo == null) return;
                loaderInfo.getDownloadTask(new NotificationDownloadListener(context, loaderInfo)).run();
            }catch (IOException e) {
                Tools.showErrorRemote(context, R.string.modpack_install_download_failed, e);
            }
        });
    }

    /**
     * Install the mod(pack).
     * May require the download of additional files.
     * May requires launching the installation of a modloader
     * @param modDetail The mod detail data
     * @param selectedVersion The selected version
     */
    ModLoader installMod(ModDetail modDetail, int selectedVersion) throws IOException;
}
