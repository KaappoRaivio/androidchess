package kaappo.androidchess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    public final String PLAYER_SIDE = "a";
    public final String COMPUTER_LEVEL = "b";

    public final String BUNDLE_KEY = "c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onPlayButtonClick (View button) {
        Spinner sideSpinner = (Spinner) findViewById(R.id.i_play_as_spinner);
        String playerSide = sideSpinner.getSelectedItem().toString();

        Spinner levelSpinner = (Spinner) findViewById(R.id.opponent_level_spinner);
        String computerLevel = levelSpinner.getSelectedItem().toString();

        Bundle bundle = new Bundle();
        bundle.putString(PLAYER_SIDE, playerSide);
        bundle.putString(COMPUTER_LEVEL, computerLevel);

        Intent intent = new Intent(getApplicationContext(), ChessActivity.class);
        intent.putExtra(BUNDLE_KEY, bundle);
        startActivity(intent);

    }
}
