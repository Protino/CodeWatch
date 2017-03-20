package io.github.protino.codewatch.ui.adapter;


import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.protino.codewatch.R;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

    private List<NavigationItem> dataList;
    private NavigationDrawerCallbacks navigationDrawerCallbacks;
    private View currentSelectedView;
    private int selectedPosition;

    public NavigationDrawerAdapter(List<NavigationItem> data) {
        dataList = data;
    }

    public NavigationDrawerCallbacks getNavigationDrawerCallbacks() {
        return navigationDrawerCallbacks;
    }

    public void setNavigationDrawerCallbacks(NavigationDrawerCallbacks navigationDrawerCallbacks) {
        this.navigationDrawerCallbacks = navigationDrawerCallbacks;
    }

    @Override
    public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.drawer_row, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.itemView.setClickable(true);
        viewHolder.itemView.setBackgroundResource(R.drawable.row_selector);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(dataList.get(i).getText());
        viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(dataList.get(i).getDrawable(), null, null, null);
        if (selectedPosition == i) {
            if (currentSelectedView != null) {
                currentSelectedView.setSelected(false);
            }
            selectedPosition = i;
            currentSelectedView = viewHolder.itemView;
            currentSelectedView.setSelected(true);
        } else {
            if (currentSelectedView != null) {
                currentSelectedView.setSelected(false);
            }
        }
    }


    public void selectPosition(int position) {
        selectedPosition = position;
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.item_name);
        }

        @Override
        public void onClick(View v) {
            if (currentSelectedView != null) {
                currentSelectedView.setSelected(false);
            }
            selectedPosition = getAdapterPosition();
            v.setSelected(true);
            currentSelectedView = v;
            if (navigationDrawerCallbacks != null)
                navigationDrawerCallbacks.onNavigationDrawerItemSelected(getAdapterPosition());
        }
    }

    public static class NavigationItem {

        private String text;
        private Drawable drawable;

        public NavigationItem(String text, Drawable drawable) {
            this.text = text;
            this.drawable = drawable;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }
    }
}
