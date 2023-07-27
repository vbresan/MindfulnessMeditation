package biz.binarysolutions.mindfulnessmeditation.ui.practicejournal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.data.CompletedMeditation;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDatabase;

/**
 *
 */
public class ParentFragment
    extends     Fragment
    implements  Observer<List<CompletedMeditation>> {

    private ScrollView   scrollView;
    private LinearLayout linearLayout;


    /**
     *
     */
    private void registerDatabaseObserver() {

        Context context = getContext();
        if (context == null) {
            return;
        }

        MeditationDatabase
            .getDatabase(context)
                .completedMeditationDao()
                    .getAll()
                        .observe(getViewLifecycleOwner(), this);
    }

    /**
     *
     * @param context
     * @param date
     * @param title
     * @return
     */
    private View getNewItem(@NonNull Context context, String date, String title) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_practice_journal, null);

        TextView textView;

        textView = view.findViewById(R.id.textViewTitle);
        if (textView != null) {
            textView.setText(date);
        }

        textView = view.findViewById(R.id.textViewSubtitle);
        if (textView != null) {
            textView.setText(title);
        }

        return view;
    }

    /**
     *
     * @param timeStamp
     * @return
     */
    private String getMeditationDate(long timeStamp) {

        DateFormat format = SimpleDateFormat.getDateInstance();
        return format.format(new Date(timeStamp));
    }


    /**
     *
     * @return
     * @param meditations
     */
    private Map<String, List<String>> getPracticeJournalMap(List<CompletedMeditation> meditations) {

        Map<String, List<String>> map = new LinkedHashMap<>();

        for (CompletedMeditation meditation: meditations) {

            String date  = getMeditationDate(meditation.getTimeStamp());
            String title = meditation.getMeditationId();

            List<String> titles = map.get(date);
            if (titles == null) {
                titles = new ArrayList<>();
                map.put(date, titles);
            }

            titles.add(title);
        }

        return map;
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

        View root = inflater.inflate(R.layout.fragment_practice_journal, container, false);

        scrollView   = root.findViewById(R.id.scrollView);
        linearLayout = root.findViewById(R.id.linearLayout);

        registerDatabaseObserver();

        return root;
    }

    @Override
    public void onChanged(List<CompletedMeditation> meditations) {

        if (linearLayout == null) {
            return;
        }

        Context context = getContext();
        if (context == null) {
            return;
        }

        Map<String, List<String>> map = getPracticeJournalMap(meditations);
        for (String date: map.keySet()) {

            StringBuilder titles = new StringBuilder();
            for (String title: map.get(date)) {
                titles.append(title).append("\n");
            }

            linearLayout.addView(getNewItem(context, date, titles.toString()));
        }

        if (scrollView != null) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }
}