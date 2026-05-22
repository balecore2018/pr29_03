package com.example.pr29.datas.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.pr29.datas.apis.WeatherApi;
import com.example.pr29.datas.apis.WeatherResponse;
import com.example.pr29.datas.callbacks.MyResponseCallback;
import com.example.pr29.datas.databases.DbContext;
import com.example.pr29.datas.databases.WeatherContext;
import com.example.pr29.domains.models.Day;
import com.example.pr29.presentations.utils.DataNotifier;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WeatherWorker extends Worker {

    String TAG = "WeatherWorker";
    CountDownLatch latch;
    boolean requestSuccessful = false;

    public WeatherWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Start weather request");
        latch = new CountDownLatch(1);
        requestSuccessful = false;

        new DbContext(getApplicationContext());

        try {
            WeatherApi weatherApi = new WeatherApi(58, 56, responseWeather);
            weatherApi.execute();

            boolean completed = latch.await(30, TimeUnit.SECONDS);
            if (completed == false || requestSuccessful == false) {
                Log.e(TAG, "Request failed");
                return Result.failure();
            }

            DataNotifier.getInstance().notifyUpdate();
            return Result.success();
        } catch (InterruptedException e) {
            Log.e(TAG, "Request interrupted", e);
            return Result.failure();
        }
    }

    MyResponseCallback responseWeather = new MyResponseCallback() {
        @Override
        public void onCompile(String result) {
            Log.d(TAG, result);

            List<Day> daysList = new ArrayList<>();
            WeatherResponse weatherResponse = new GsonBuilder().create().fromJson(
                    result,
                    WeatherResponse.class
            );

            for (WeatherResponse.Forecast forecast : weatherResponse.forecasts) {
                if (forecast.hours.isEmpty()) continue;

                Integer avgTemp = avgTemp(forecast.hours);
                String nameDay = getDayOfWeek(forecast.date);
                String condition = getDayCondition(forecast.hours);
                Day day = new Day(nameDay, avgTemp, condition);
                daysList.add(day);
            }

            WeatherContext.save(daysList);
            requestSuccessful = true;
            latch.countDown();
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, error);
            requestSuccessful = false;
            latch.countDown();
        }
    };

    public Integer avgTemp(List<WeatherResponse.Forecast.Hour> hours) {
        Float sumTemp = 0f;
        for (WeatherResponse.Forecast.Hour hour : hours)
            sumTemp += hour.temp;

        Float avgTemp = sumTemp / hours.size();
        return Math.round(avgTemp);
    }

    public String getDayOfWeek(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE", Locale.US);

        try {
            Date date = inputFormat.parse(dateString);
            return date == null ? dateString : outputFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    public String getDayCondition(List<WeatherResponse.Forecast.Hour> hours) {
        Map<String, Integer> conditionCount = new HashMap<>();

        for (WeatherResponse.Forecast.Hour hour : hours) {
            if (hour.condition != null && hour.condition.isEmpty() == false) {
                conditionCount.put(
                        hour.condition,
                        conditionCount.getOrDefault(hour.condition, 0) + 1
                );
            }
        }

        String mostFrequentCondition = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : conditionCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentCondition = entry.getKey();
            }
        }

        return mostFrequentCondition;
    }
}
