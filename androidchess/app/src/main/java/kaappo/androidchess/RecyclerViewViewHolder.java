package kaappo.androidchess;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecyclerViewViewHolder extends RecyclerView.ViewHolder {
    public TextView moveText;
    public RelativeLayout container;

    public RecyclerViewViewHolder(View itemView) {
        super(itemView);

        this.moveText = (TextView) itemView.findViewById(R.id.move_entry_move);
        this.container = (RelativeLayout) itemView.findViewById(R.id.container4);
    }
}
