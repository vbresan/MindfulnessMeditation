package biz.binarysolutions.mindfulnessmeditation.ui.meditations.downloadable;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.data.Meditation;
import biz.binarysolutions.mindfulnessmeditation.ui.meditations.downloadable.data.DownloadableMeditation;

/**
 *
 */
public class DownloadableAdapter extends ArrayAdapter<DownloadableMeditation>
    implements AdapterView.OnItemClickListener {

    private final Context context;
    private final int     resource;

    /**
     *
     * @param meditation
     * @return
     */
    private String getSubtitleText(Meditation meditation) {

        String author   = meditation.getAuthor();
        String duration = meditation.getDuration();
        long   size     = Util.convertBtoMB(meditation.getSize());

        if (TextUtils.isEmpty(author)) {
            return getContext()
                .getString(R.string.SubtitleNoAuthor, duration, size);
        } else {
            return getContext()
                .getString(R.string.Subtitle, author, duration, size);
        }
    }

    /**
     *
     * @param context
     * @param resource
     */
    public DownloadableAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        this.context  = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView
        (
            int                 position,
            @Nullable View      convertView,
            @NonNull  ViewGroup parent
        ) {
        
        View view = convertView;
//        if (view == null) {
            view = LayoutInflater.from(context).inflate(resource, null);
//        }

        final DownloadableMeditation meditation = getItem(position);
        if (meditation == null) {
            return view;
        }

        TextView textView;

        textView = view.findViewById(R.id.textViewTitle);
        if (textView != null) {
            textView.setText(meditation.getTitle());
        }

        textView = view.findViewById(R.id.textViewSubtitle);
        if (textView != null) {
            textView.setText(getSubtitleText(meditation));
        }

        CheckBox checkbox = view.findViewById(R.id.checkboxSelected);
        if (checkbox != null) {
            checkbox.setChecked(meditation.isSelected());
            checkbox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                    meditation.setSelected(isChecked);
                }
            });
        }

        return view;
    }

    @Override
    public void onItemClick
        (
                AdapterView<?> adapterView,
                View           view,
                int            position,
                long           id
        ) {

        CheckBox checkBox = view.findViewById(R.id.checkboxSelected);
        if (checkBox == null) {
            return;
        }

        boolean isChecked = checkBox.isChecked();
        checkBox.setChecked(! isChecked);
    }

    /**
     *
     * @param isSelected
     */
    public void setAllSelected(boolean isSelected) {

        for (int i = 0; i < getCount(); i++) {

            DownloadableMeditation meditation = getItem(i);
            if (meditation != null) {
                meditation.setSelected(isSelected);
            }
        }

        notifyDataSetChanged();
    }

    /**
     *
     * @return
     */
    public List<Meditation> getSelectedMeditations() {

        ArrayList<Meditation> meditations = new ArrayList<>();

        for (int i = 0; i < getCount(); i++) {

            DownloadableMeditation meditation = getItem(i);
            if (meditation != null && meditation.isSelected()) {
                meditations.add(meditation);
            }
        }

        return meditations;
    }
}
