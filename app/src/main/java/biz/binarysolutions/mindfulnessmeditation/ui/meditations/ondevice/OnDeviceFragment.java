package biz.binarysolutions.mindfulnessmeditation.ui.meditations.ondevice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import java.util.List;

import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.data.Meditation;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDatabase;

/**
 *
 */
public class OnDeviceFragment extends Fragment
        implements
        Observer<List<Meditation>>,
        OnDeviceAdapterListener {

    private View            root;
    private OnDeviceAdapter adapter;

    /**
     *
     */
    private void hideList() {

        View view;

        view = root.findViewById(R.id.linearLayoutYesOnDevice);
        if (view != null) {
            view.setVisibility(View.GONE);
        }

        view = root.findViewById(R.id.linearLayoutNoOnDevice);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     */
    private void showList() {

        View view;

        view = root.findViewById(R.id.linearLayoutNoOnDevice);
        if (view != null) {
            view.setVisibility(View.GONE);
        }

        view = root.findViewById(R.id.linearLayoutYesOnDevice);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     * @param context
     */
    private void setListAdapter(Context context) {

        adapter = new OnDeviceAdapter(context, R.layout.list_item_ondevice, this);

        ListView listView = root.findViewById(R.id.listViewOnDevice);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);

        MeditationDatabase
            .getDatabase(context)
                .meditationDao()
                    .getAllOnDevice()
                        .observe(getViewLifecycleOwner(), this);
    }

    /**
     *
     */
    public OnDeviceFragment() {
    }

    @Override
    public View onCreateView
        (
            LayoutInflater inflater,
            ViewGroup      container,
            Bundle         savedInstanceState
        ) {

        root = inflater.inflate(R.layout.fragment_meditations_ondevice, container, false);

        Context context = getContext();
        if (context != null) {
            setListAdapter(context);
        }

        return root;
    }

    @Override
    public void onChanged(List<Meditation> meditations) {

        if (meditations == null || meditations.size() == 0) {
            hideList();
        } else {
            showList();

            adapter.clear();
            adapter.addAll(meditations);
        }
    }

    @Override
    public void onDeleteMeditation(Meditation meditation) {

        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }

        DeleteMeditationDialogFragment fragment =
            new DeleteMeditationDialogFragment(meditation);
        fragment.show(activity.getSupportFragmentManager(), "");
    }

    @Override
    public void onPlayMeditation(Meditation meditation) {

        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        String key    = getString(R.string.extra_key_meditation);

        intent.putExtra(key, meditation);
        startActivity(intent);
    }
}
