package com.farthestgate.suspensions.android.ui.gallery;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.base.BaseToolBarActivity;
import com.farthestgate.suspensions.android.component.MarginDecoration;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class GalleryActivity extends BaseToolBarActivity {

    private static final int COL_NUM = 3;
    @BindView(R.id.my_recycler_view)
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    GalleryAdapter adapter;

    @Override
    public int getLayout() {
        return R.layout.gallery_activity;
    }

    @Override
    public void initializeUi() {
        setupToolbar("Gallery");

        recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.setHasFixedSize(true);

        gridLayoutManager = new GridLayoutManager(this, COL_NUM);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new GalleryAdapter(this, getFPNPhoto());
        recyclerView.setAdapter(adapter);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
    }

    private List<ListItem> getFPNPhoto(){
        List<ListItem> listItems = new ArrayList<ListItem>();
        ListItem listItem = null;
        ListItem headerItem = null;
        String lastRef = "";

        File photosFolder = FileUtils.getDirectory(AppConstant.PHOTOS_FOLDER);
        File[] photoFiles = photosFolder.listFiles();

        for(File photoFile : photoFiles){
            listItem = new ListItem();
            String ref = photoFile.getName().split("-")[0];
            if(lastRef.isEmpty() || !lastRef.equalsIgnoreCase(ref)){
                headerItem = new ListItem();
                headerItem.setReference(ref);
                listItems.add(headerItem);
                lastRef= ref;
            }
            listItem.setReference(ref);
            listItem.setFileName(photoFile.getAbsolutePath());
            listItems.add(listItem);
        }
        return listItems;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }
}
