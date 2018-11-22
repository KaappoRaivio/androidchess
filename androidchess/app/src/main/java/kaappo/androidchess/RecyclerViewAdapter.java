package kaappo.androidchess;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kaappo.androidchess.askokaappochess.move;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewViewHolder> {

    private List<String> moves = new ArrayList<>();

    public void addMove (String move) {
        moves.add(move);
    }

    public RecyclerViewAdapter () {}

    @NonNull
    @Override
    public RecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.move_entry, parent, false);
        return new RecyclerViewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewViewHolder holder, int position) {
        String move = this.moves.get(position);
        holder.moveText.setText(move);
    }


    @Override
    public int getItemCount() {
        return this.moves.size();
    }
}
