package com.example.pr29.viewmodels;

import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pr29.datas.apis.WeatherApi;
import com.example.pr29.datas.apis.WeatherResponse;
import com.example.pr29.datas.callbacks.MyResponseCallback;
import com.example.pr29.domains.models.Day;
import com.google.gson.GsonBuilder;

import java.net.ResponseCache;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DayViewModel extends ViewModel {

    MutableLiveData<List<Day>> _days = new MutableLiveData<>();
    public LiveData<List<Day>> days = _days;
    MutableLiveData<String> _nowTemp = new MutableLiveData<>();
    public LiveData<String> nowTemp = _nowTemp;
    MutableLiveData<String> _condition = new MutableLiveData<>();
    public LiveData<String> condition = _condition;

    public DayViewModel() {

        WeatherApi weatherApi = new WeatherApi(58,56, ResponseWeather);
        weatherApi.execute();

    }

    MyResponseCallback ResponseWeather = new MyResponseCallback() {
        @Override
        public void onCompile(String result) {
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

            _days.setValue(daysList);
            _nowTemp.setValue(weatherResponse.fact.temp + "*");
            _condition.setValue(weatherResponse.fact.condition);
        }

        @Override
        public void onError(String error) {

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

        LocalDate date = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date = LocalDate.parse(dateString);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("en"));
        }

    }

    public String getDayCondition(List<WeatherResponse.Forecast.Hour> hours) {

        Map<String, Integer> conditionCount = new HashMap<>();

        for (WeatherResponse.Forecast.Hour hour : hours) {

            if (hour.condition != null && !hour.condition.isEmpty()) {

                conditionCount.put(hour.condition,
                        conditionCount.getOrDefault(hour.condition,0) + 1);

            }

        }
        String mostFrequentCondition = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : conditionCount.entrySet()) {

            if(entry.getValue() > maxCount) {

                maxCount = entry.getValue();
                mostFrequentCondition = entry.getKey();

            }

        }

        return mostFrequentCondition;

    }
}
