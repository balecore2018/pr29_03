package com.example.pr29.presentations;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr29.domains.models.Day;

import java.util.List;

public class BindingAdapters {

    @BindingAdapter("days")

    public static void setDays(RecyclerView recyclerView, List<Day> days) {

        if (recyclerView.getAdapter() instanceof DayAdapter) {

            DayAdapter adapter = (DayAdapter) recyclerView.getAdapter();
            adapter.setDays(days);

        }

    }

}
