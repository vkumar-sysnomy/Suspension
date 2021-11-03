package com.farthestgate.suspensions.android.ui.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.farthestgate.suspensions.android.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Suraj Gopal on 1/20/2017.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.TextViewHolder> {
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private Context context;
    private List<ListItem> listItems;

    public GalleryAdapter(Context context, List<ListItem> listItems) {
        this.context = context;
        this.listItems = listItems;
        notifyDataSetChanged();
    }

    public boolean isHeader(int position) {
        if(position == 0) {
            return true;
        }
        if(listItems.get(position).getReference().equals(listItems.get(position - 1).getReference())) {
            return  false;
        }else{
            return true;
        }
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            View header = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_header, parent, false);
            return new TextViewHolder(header);
        } else if(viewType == ITEM_VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
            return new TextViewHolder(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final TextViewHolder holder, final int position) {
        final ListItem currentItem = listItems.get(position);  // Subtract 1 for gallery_header
        if (isHeader(position)) {
            holder.headerText.setText(currentItem.getReference());
            return;
        }

        Picasso.with(context).load(new File(currentItem.getFileName())).into(holder.iv);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        holder.iv.getContext(), currentItem.getReference(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class TextViewHolder extends  RecyclerView.ViewHolder{
        TextView headerText;
        ImageView iv;
        public TextViewHolder(View itemView) {
            super(itemView);
            this.headerText = (TextView) itemView.findViewById(R.id.headerText);
            this.iv = (ImageView)itemView.findViewById(R.id.ivListItem);
        }
    }
}