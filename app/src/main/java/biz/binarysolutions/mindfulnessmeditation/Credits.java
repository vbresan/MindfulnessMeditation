package biz.binarysolutions.mindfulnessmeditation;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 *
 */
public class Credits extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        TextView view = findViewById(R.id.textViewCredits1);
        if (view != null) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
