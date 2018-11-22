package kaappo.androidchess;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class RecyclerViewViewHolder extends RecyclerView.ViewHolder {
    public TextView moveText;

    public RecyclerViewViewHolder(View itemView) {
        super(itemView);

        this.moveText = (TextView) itemView.findViewById(R.id.move_entry_move);
    }
}
