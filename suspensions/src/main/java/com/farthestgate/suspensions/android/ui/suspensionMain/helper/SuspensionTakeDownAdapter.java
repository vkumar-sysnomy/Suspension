package com.farthestgate.suspensions.android.ui.suspensionMain.helper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.ui.suspensionMain.model.Suspension;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SuspensionTakeDownAdapter extends RecyclerView.Adapter<SuspensionTakeDownAdapter.SuspensionViewHolder> {
    private List<Suspension> suspensionList;
    public class SuspensionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewSuspensionRef)
        public TextView viewSuspensionRef;
        @BindView(R.id.textViewLocation)
        public TextView viewLocation;
        @BindView(R.id.textViewStart)
        public TextView viewStart;
        @BindView(R.id.textViewEnd)
        public TextView viewEnd;
        public SuspensionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public SuspensionTakeDownAdapter(List<Suspension> suspensionList) {
        this.suspensionList = suspensionList;
    }

    @Override
    public SuspensionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suspension_take_down_custom_row, parent, false);
        return new SuspensionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SuspensionViewHolder holder, int position) {
        Suspension suspension = suspensionList.get(position);
        holder.viewSuspensionRef.setText(suspension.getSuspensionNumber());
        holder.viewLocation.setText(suspension.getLocation());
        holder.viewStart.setText(suspension.getStart());
        holder.viewEnd.setText(suspension.getEnd());
        if (position % 2 == 1) {
            holder.itemView.setBackgroundResource(R.color.white);
        } else {
            holder.itemView.setBackgroundResource(R.color.offWhite);
        }
    }

    @Override
    public int getItemCount() {
        return suspensionList.size();
    }
}
