package kaappo.androidchess;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewViewHolder> {

    private List<String> moves = new ArrayList<>();
    private List<String> boardHistory = new ArrayList<>();
    private ChessActivity context;
    private Vector< Vector<Integer> > moveVector = new Vector<>();

    public void addMove (String move) {
        moves.add(move);
    }

    public void addBoard (String board, Vector<Integer> lMoveV) {
        this.boardHistory.add(board);
        this.moveVector.add(lMoveV);
    }

    public RecyclerViewAdapter (ChessActivity context) {
        this.context = context;
    }

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

        String board;

        try {
            board = this.boardHistory.get(position);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("RecyclerViewAdapter#onBindViewHolder(): invalid position " + position);
        }

        final String finalBoard = board;
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.setBoard(finalBoard);
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.moves.size();
    }
}
