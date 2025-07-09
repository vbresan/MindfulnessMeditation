package biz.binarysolutions.mindfulnessmeditation.ui.support;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import biz.binarysolutions.mindfulnessmeditation.R;

public class ParentFragment extends Fragment {

    public View onCreateView
        (
            @NonNull LayoutInflater inflater,
            ViewGroup               container,
            Bundle                  savedInstanceState
        ) {

        View root = inflater.inflate(R.layout.fragment_support, container, false);

        TextView textView = root.findViewById(R.id.textViewDonate);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        return root;
    }
}