package biz.binarysolutions.mindfulnessmeditation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import biz.binarysolutions.mindfulnessmeditation.data.Karma;
import biz.binarysolutions.mindfulnessmeditation.data.Meditation;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDao;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDatabase;
import biz.binarysolutions.mindfulnessmeditation.data.Streak;
import biz.binarysolutions.mindfulnessmeditation.network.DownloadCallback;
import biz.binarysolutions.mindfulnessmeditation.network.TextDownloadFragment;

public class MainActivity extends AppCompatActivity
    implements DownloadCallback<String> {

    /**
     *
     */
    public void displayBalloonStreak() {
        Toast.makeText(this, R.string.BalloonStreak, Toast.LENGTH_LONG).show();
    }

    /**
     *
     */
    public void displayBalloonKarma() {
        Toast.makeText(this, R.string.BalloonKarma, Toast.LENGTH_LONG).show();
    }

    /**
     *
     */
    private void addOnClickListenerStreak() {

        LinearLayout view = findViewById(R.id.linearLayoutStreak);
        if (view == null) {
            return;
        }

        view.setOnClickListener(v -> displayBalloonStreak());
    }

    /**
     *
     */
    private void addOnClickListenerKarma() {

        LinearLayout view = findViewById(R.id.linearLayoutKarma);
        if (view == null) {
            return;
        }

        view.setOnClickListener(v -> displayBalloonKarma());
    }

    /**
     *
     */
    private void fetchMeditationsList() {

        TextDownloadFragment.getInstance(
            getSupportFragmentManager(),
            getString(R.string.url_meditations_list)
        );
    }

    /**
     *
     */
    private void displayStreak() {

        TextView textView = findViewById(R.id.textViewStreak);
        if (textView == null) {
            return;
        }

        ImageView imageView = findViewById(R.id.imageViewStreak);
        if (imageView == null) {
            return;
        }

        new Thread(() -> {

            Streak streak = Streak.get(MainActivity.this);

            runOnUiThread(() -> {

                if (streak.isUpdatedToday()) {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground);
                } else {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground_grayscale);
                }

                textView.setText(String.valueOf(streak.getCount()));
            });
        }).start();
    }

    /**
     *
     */
    private void displayKarma() {

        TextView textView = findViewById(R.id.textViewKarma);
        if (textView == null) {
            return;
        }

        new Thread(() -> {

            Karma  karma = Karma.get(MainActivity.this);
            String text  = String.valueOf(karma.getCount());

            runOnUiThread(() -> textView.setText(text));
        }).start();
    }

    /**
     *
     * @param string
     * @return
     */
    private ArrayList<Meditation> getMeditations(String string) {

        ArrayList<Meditation> meditations = new ArrayList<>();

        String[] lines = string.split("\n");
        for (int i = 1; i < lines.length; i++) {

            String[] items = lines[i].trim().split(",");
            if (items.length < 6) {
                continue;
            }

            long size = Long.decode(items[3]);
            Meditation meditation = new Meditation(
                items[0], items[1], items[2], size, items[4], items[5]);

            meditations.add(meditation);
        }

        return meditations;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration =
            new AppBarConfiguration.Builder(
                R.id.navigation_audio_guides,
                R.id.navigation_practice_journal,
                R.id.navigation_notifications
            ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        addOnClickListenerStreak();
        addOnClickListenerKarma();
        fetchMeditationsList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        displayStreak();
        displayKarma();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menuItemPreferences) {

            Intent intent = new Intent(this, Preferences.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menuItemCredits) {

            Intent intent = new Intent(this, Credits.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDownloadFinished(String result) {

        final ArrayList<Meditation> meditations = getMeditations(result);
        final MeditationDao dao =
            MeditationDatabase.getDatabase(this).meditationDao();

        MeditationDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.refreshMeditationsList(meditations);
            }
        });
    }

    @Override
    public void onDownloadFailed() {
        Toast.makeText(this, R.string.NoInternet, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProgressUpdate(int code, int size) {
        // do nothing
    }
}