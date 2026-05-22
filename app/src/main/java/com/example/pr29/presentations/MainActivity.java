package com.example.pr29.presentations;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.pr29.R;
import com.example.pr29.datas.databases.DbContext;
import com.example.pr29.datas.databases.WeatherContext;
import com.example.pr29.datas.workers.WeatherWorker;
import com.example.pr29.databinding.ActivityMainBinding;
import com.example.pr29.viewmodels.DayViewModel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DayViewModel viewModel;
    DayAdapter adapter;
    DbContext context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(
                this,
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        );

        context = new DbContext(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(DayViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        setupRecyclerView();

        if (WeatherContext.allDays().isEmpty()) {
            onStartWorkerNow();
        }
        onStartWorker();
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

    public void onStartWorker() {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                WeatherWorker.class,
                15, TimeUnit.MINUTES,
                30, TimeUnit.SECONDS
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "WORKER_MANAGER",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        );
    }

    public void onStartWorkerNow() {
        OneTimeWorkRequest immediateWork = new OneTimeWorkRequest.Builder(WeatherWorker.class)
                .build();

        WorkManager.getInstance(this).enqueue(immediateWork);
    }
}
