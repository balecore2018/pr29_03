package com.example.pr29.presentations;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.pr29.R;
import com.example.pr29.databinding.ActivityMainBinding;
import com.example.pr29.viewmodels.DayViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DayViewModel viewModel;
    DayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(
                this,
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        );
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(DayViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        setupRecyclerView();
    }


    private void setupRecyclerView() {

        if (adapter == null) {

            adapter = new DayAdapter();
            binding.recyclerView.setAdapter(adapter);

        } else {

            adapter = (DayAdapter) binding.recyclerView.getAdapter();

        }

        viewModel.days.observe(this, days -> {

            if (days != null) {

                adapter.setDays(days);

            } else {

                adapter.setDays(new ArrayList<>());

            }

        });

    }
}
