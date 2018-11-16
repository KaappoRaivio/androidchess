package kaappo.androidchess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import kaappo.androidchess.askokaappochess.play;

public class MainActivity extends AppCompatActivity {


    public static String getId(View view) {
        if (view.getId() == -1) {
            return "no-id";
        } else return view.getResources().getResourceName(view.getId());
    }

    public static final String PLAYER_SIDE = "a";
    public static final String COMPUTER_LEVEL = "b";
    public static final String WHITE_LEVEL = "f";
    public static final String BLACK_LEVEL = "g";

    public static final String BUNDLE_KEY = "c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, NewChessActivity.class);
        startActivity(intent);

    }

    public void onPlayButtonClick (View button) {
        Spinner sideSpinner = (Spinner) findViewById(R.id.i_play_as_spinner);
        String playerSide = sideSpinner.getSelectedItem().toString();
        String white_level, black_level;


        Spinner levelSpinner = (Spinner) findViewById(R.id.opponent_level_spinner);
        String computerLevel = levelSpinner.getSelectedItem().toString();

        if (playerSide.equals(getResources().getStringArray(R.array.activity_main_white_and_black)[0])) {
            white_level = String.valueOf(play.PLAYER);
            black_level = computerLevel;
        } else {
            white_level = String.valueOf(computerLevel);
            black_level = String.valueOf(play.PLAYER);
        }

        Bundle bundle = new Bundle();
        bundle.putString(PLAYER_SIDE, playerSide);
        bundle.putString(COMPUTER_LEVEL, computerLevel);
        bundle.putString(WHITE_LEVEL, white_level);
        bundle.putString(BLACK_LEVEL, black_level);

        Intent intent = new Intent(getApplicationContext(), TtyuiActivity.class);
        intent.putExtra(BUNDLE_KEY, bundle);
        startActivity(intent);

    }

}
