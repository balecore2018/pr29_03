package com.example.pr29.presentations;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr29.R;
import com.example.pr29.databinding.ItemDayBinding;
import com.example.pr29.domains.models.Day;
import com.example.pr29.viewmodels.DayViewModel;

import java.util.ArrayList;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    public List<Day> days = new ArrayList<>();

    @NonNull
    @Override
    public DayAdapter.DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemDayBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_day,
                parent,
                false
        );
        return new DayViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull DayAdapter.DayViewHolder holder, int position) {

        Day day = days.get(position);
        holder.bind(day);

    }

    @Override
    public int getItemCount() {

        return days.size();

    }

    public void setDays(List<Day> days) {

        if (days == null)
            this.days = new ArrayList<>();
        else
            this.days = days;

        notifyDataSetChanged();

    }

    static class DayViewHolder extends RecyclerView.ViewHolder {

        final ItemDayBinding binding;
        public DayViewHolder(ItemDayBinding binding) {

            super(binding.getRoot());
            this.binding = binding;

        }

        void bind(Day day) {

            binding.setDay(day);
            binding.executePendingBindings();

        }

    }

}
