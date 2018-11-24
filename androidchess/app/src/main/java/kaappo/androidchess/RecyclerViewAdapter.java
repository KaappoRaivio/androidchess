package kaappo.androidchess;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
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
    private SparseBooleanArray selectedItem = new SparseBooleanArray();

    public void addMove (String move) {
        moves.add(move);
    }

    public void addBoard (String board, Vector<Integer> lMoveV) {
        if (!this.boardHistory.get(boardHistory.size() - 1).equals(board)) {
            this.boardHistory.add(board);
            this.moveVector.add(lMoveV);
        }
    }

    public void setMoves (List<String> moves) {
        this.moves = moves;
    }

    private void boldItem(int pos) {
        selectedItem.clear();
        selectedItem.put(pos, true);
        notifyDataSetChanged();
    }

    public void stepLeft () {
        int position = -1;
        for (int i = 0; i < selectedItem.size(); i++) {
            if (selectedItem.get(i, false)) {
                position = i - 1;
                System.out.println("found!");
                break;
            } else {
                System.out.println("not found!");
            }
        }

        if (position == -1) {
            position = this.moves.size() - 2;
        }

        try {
            context.setBoardByIndex(position);
            boldItem(position);
        } catch (IndexOutOfBoundsException ignored) {
            Log.e("asd",  "", ignored);
        }
    }

    public void stepRight () {
        int position = this.moves.size() + 1;
        for (int i = 0; i < selectedItem.size(); i++) {
            if (selectedItem.get(i, false)) {
                position = i + 1;
                break;
            }
        }

        if (position < this.moves.size()) {
            context.setBoardByIndex(position);
            boldItem(position);
        }
    }

    RecyclerViewAdapter (ChessActivity context) {
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
    public void onBindViewHolder(@NonNull final RecyclerViewViewHolder holder, int position) {
        String move = this.moves.get(position);
        holder.moveText.setText(move);
        holder.moveText.setTypeface(holder.moveText.getTypeface(), selectedItem.get(position, false) ? Typeface.BOLD : Typeface.ITALIC);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.setBoardByIndex(holder.getLayoutPosition());
                boldItem(holder.getLayoutPosition());
            }
        });
//        boldItem(holder.getLayoutPosition());
    }


    @Override
    public int getItemCount() {
        return this.moves.size();
    }
}
