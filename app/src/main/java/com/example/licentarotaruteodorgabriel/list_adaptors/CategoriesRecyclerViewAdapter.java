package com.example.licentarotaruteodorgabriel.list_adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.interfaces.CategoriesRecyclerViewClickListener;

import java.util.ArrayList;

public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.CategoriesViewHolder> {

    private ArrayList<String> categories;
    private Context context;
    private static CategoriesRecyclerViewClickListener itemListener;

    public CategoriesRecyclerViewAdapter(ArrayList<String> categories, Context context, CategoriesRecyclerViewClickListener itemListener) {
        this.categories = categories;
        this.context = context;
        CategoriesRecyclerViewAdapter.itemListener = itemListener;
    }

    @NonNull
    @Override
    public CategoriesRecyclerViewAdapter.CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_category_row, parent, false);
        return new CategoriesRecyclerViewAdapter.CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesRecyclerViewAdapter.CategoriesViewHolder holder, int position) {
        holder.tvCategoryName.setText(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tvCategoryName;


        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemListener.recyclerViewListClicked(view, this.getLayoutPosition());
        }
    }
}
