package com.example.licentarotaruteodorgabriel.interfaces;

import android.view.View;

public interface RecyclerViewClickListener {

    void onPositionClicked(View clickedView, int clickedRecyclerItemPosition);
    void onLongPositionClicked(View clickedView, int clickedRecyclerItemPosition);
}
