package com.sd.a3kleingroup.classes.UI;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.ReceiveFilesActivity;
import com.sd.a3kleingroup.classes.Holder;
import com.sd.a3kleingroup.classes.RecyclerHolder;

public class ReceiveFilesSorter implements View.OnClickListener {
    private int directionDate = 1;
    private int directionName = 1;
    private ReceiveFilesActivity.RecyclerAdapter adapter;
    private Context context;
    private String LOG_TAG="MY_ReceiveSorter";
    /**
     *
     * @param adapter The receive files adapter
     * @param context The context, e.g. ReceiveFilesActivity.this
     */
    public ReceiveFilesSorter(ReceiveFilesActivity.RecyclerAdapter adapter, Context context) {
        this.adapter = adapter;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        showSortPopup(v);
    }

    private void showSortPopup(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        // Inflate the menu from xml
        popup.inflate(R.menu.popup_recfiles_sort);
        // Setup menu item selection
        popup.setOnMenuItemClickListener(item -> {
            if (adapter == null) return false;
            switch (item.getItemId()) {
                case R.id.menu_date:
                    sortByDate();
                    return true;
                case R.id.menu_name:
                    sortByName();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void sortByName() {
//        if (1==1) return;

        Log.d(LOG_TAG, "We're now sorting by name");
        // now we sort filtered agreements on whatever
        adapter.filteredAgreements.sort((a, b) -> {
            if (RecyclerHolder.cache.containsKey(a.getId()) && RecyclerHolder.cache.containsKey(b.getId())){
                try {
                    String nameA = RecyclerHolder.cache.get(a.getId()).getFile().getFileName();
                    String nameB = RecyclerHolder.cache.get(b.getId()).getFile().getFileName();
                    return nameA.compareTo(nameB) * directionName;
                }catch (Exception e){
                    return directionName;
                }
            }
            return 0;
        });
        directionName*=-1;
        adapter.onChangeDataset();
    }

    private void sortByDate() {
//        if (1==1) return;

        Log.d(LOG_TAG, "We're now sorting by date");
        // now we sort filtered agreements on whatever
        adapter.filteredAgreements.sort((a, b) -> {
            if (a.getValidUntil().after(b.getValidUntil())) return -1 * directionDate;
            else if (a.getValidUntil().equals(b.getValidUntil())) return 0;
            else return directionDate;
        });
        directionDate*=-1;

        adapter.onChangeDataset();
    }

    public void setAdapter(ReceiveFilesActivity.RecyclerAdapter adapter) {
        this.adapter = adapter;
    }

    public ReceiveFilesActivity.RecyclerAdapter getAdapter() {
        return adapter;
    }
}
