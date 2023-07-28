package biz.binarysolutions.mindfulnessmeditation.ui.audioguides;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDao;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDatabase;
import biz.binarysolutions.mindfulnessmeditation.ui.audioguides.downloadable.DownloadableFragment;
import biz.binarysolutions.mindfulnessmeditation.ui.audioguides.ondevice.OnDeviceFragment;

/**
 *
 */
public class ParentFragment extends Fragment
    implements TabLayout.OnTabSelectedListener {

    private OnDeviceFragment     onDeviceFragment;
    private DownloadableFragment downloadableFragment;

    /**
     *
     */
    private void addFragmentsToContainer() {

        FragmentManager     fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (!onDeviceFragment.isAdded()) {
            ft.add(R.id.fragmentContainer, onDeviceFragment);
        }
        if (!downloadableFragment.isAdded()) {
            ft.add(R.id.fragmentContainer, downloadableFragment);
        }

        ft.commit();
    }

    /**
     *
     * @return
     */
    private void selectDefaultTab(final TabLayout tabLayout) {

        Context context = getContext();
        if (context == null) {
            tabLayout.selectTab(tabLayout.getTabAt(1));
            return;
        }

        final MeditationDao dao =
            MeditationDatabase.getDatabase(context).meditationDao();

        MeditationDatabase.databaseWriteExecutor.execute(() -> {

            int count = dao.getAllOnDeviceCount();
            new Handler(context.getMainLooper()).post(() ->
                tabLayout.selectTab(tabLayout.getTabAt(count > 0? 0 : 1))
            );
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onDeviceFragment     = new OnDeviceFragment();
        downloadableFragment = new DownloadableFragment();
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView
        (
            @NonNull LayoutInflater inflater,
            ViewGroup               container,
            Bundle                  savedInstanceState
        ) {

        View root = inflater.inflate(R.layout.fragment_audio_guides, container, false);

        addFragmentsToContainer();

        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(this);
        selectDefaultTab(tabLayout);

        return root;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int position = tab.getPosition();

        FragmentManager     fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(position == 0 ? downloadableFragment : onDeviceFragment);
        ft.show(position == 0 ? onDeviceFragment     : downloadableFragment);
        ft.commit();
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // do nothing
    }
}