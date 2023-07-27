package biz.binarysolutions.mindfulnessmeditation.ui.audioguides.ondevice;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.data.Meditation;
import biz.binarysolutions.mindfulnessmeditation.ui.audioguides.downloadable.Util;

/**
 *
 */
public class OnDeviceAdapter extends ArrayAdapter<Meditation>
    implements AdapterView.OnItemClickListener {

    private final Context         context;
    private final int             resource;
    private final OnDeviceAdapterListener listener;

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
     * @param view
     * @param meditation
     */
    private void setButtonListeners(View view, final Meditation meditation) {

        ImageButton button;

        button = view.findViewById(R.id.imageButtonDelete);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteMeditation(meditation);
                }
            });
        }

        button = view.findViewById(R.id.imageButtonPlay);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPlayMeditation(meditation);
                }
            });
        }
    }

    /**
     * @param context
     * @param resource
     * @param listener
     */
    public OnDeviceAdapter
        (
            @NonNull Context context,
            int              resource,
            OnDeviceAdapterListener listener
        ) {
        super(context, resource);

        this.context  = context;
        this.resource = resource;
        this.listener = listener;
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
        if (view == null) {
            view = LayoutInflater.from(context).inflate(resource, null);
        }

        final Meditation meditation = getItem(position);
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

        setButtonListeners(view, meditation);

        return view;
    }

    @Override
    public void onItemClick
        (
            AdapterView<?> adapterView,
            View           view,
            int            position,
            long           l
        ) {

        Meditation meditation = getItem(position);
        if (meditation != null) {
            listener.onPlayMeditation(meditation);
        }
    }
}
