package biz.binarysolutions.mindfulnessmeditation.ui.audioguides.downloadable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import java.util.List;

import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.data.Meditation;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDatabase;
import biz.binarysolutions.mindfulnessmeditation.ui.audioguides.downloadable.data.DownloadableMeditation;

/**
 *
 */
public class DownloadableFragment extends Fragment
        implements
        Observer<List<Meditation>>,
        TotalChangeListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private View                root;
    private DownloadableAdapter adapter;

    private long total = 0;

    /**
     *
     */
    private void hideList() {

        View view;

        view = root.findViewById(R.id.linearLayoutYesDownloadable);
        if (view != null) {
            view.setVisibility(View.GONE);
        }

        view = root.findViewById(R.id.linearLayoutNoDownloadable);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     */
    private void showList() {

        View view;

        view = root.findViewById(R.id.linearLayoutNoDownloadable);
        if (view != null) {
            view.setVisibility(View.GONE);
        }

        view = root.findViewById(R.id.linearLayoutYesDownloadable);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     */
    private void setButtonListeners() {

        CheckBox checkBox = root.findViewById(R.id.checkboxSelectAll);
        if (checkBox != null) {
            checkBox.setOnCheckedChangeListener(this);
        }

        Button button = root.findViewById(R.id.buttonStartDownload);
        if (button != null) {
            button.setOnClickListener(this);
        }
    }

    /**
     *
     */
    private void resetTotal() {

        total = 0;

        TextView view = root.findViewById(R.id.textViewTotalSelected);
        if (view != null) {
            view.setText(Long.toString(total));
        }
    }

    /**
     *
     */
    public DownloadableFragment() {
    }

    @Override
    public View onCreateView
        (
            LayoutInflater inflater,
            ViewGroup      container,
            Bundle         savedInstanceState
        ) {

        root = inflater.inflate(
            R.layout.fragment_audio_guides_downloadable, container, false);

        adapter = new DownloadableAdapter(
            getContext(), R.layout.list_item_downloadable);

        ListView listView = root.findViewById(R.id.listViewDownloadable);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);

        setButtonListeners();

        MeditationDatabase
            .getDatabase(getActivity())
                .meditationDao()
                    .getAllDownloadable()
                        .observe(getViewLifecycleOwner(), this);

        return root;
    }

    @Override
    public void onChanged(List<Meditation> meditations) {

        adapter.clear();
        resetTotal();

        if (meditations == null || meditations.size() == 0) {
            hideList();
        } else {
            showList();

            for (Meditation meditation : meditations) {
                adapter.add(new DownloadableMeditation(meditation, this));
            }
        }
    }

    @Override
    public void onTotalChanged(long size) {

        total += size;

        TextView view = root.findViewById(R.id.textViewTotalSelected);
        if (view != null) {
            view.setText(Long.toString(total));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isSelected) {
        adapter.setAllSelected(isSelected);
    }

    @Override
    public void onClick(View view) {

        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }

        List<Meditation> meditations = adapter.getSelectedMeditations();
        if (meditations == null || meditations.size() == 0) {
            return;
        }

        DownloadDialogFragment fragment = new DownloadDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), "");
        fragment.downloadMeditations(meditations);
    }
}
