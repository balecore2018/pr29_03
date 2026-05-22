package com.example.pr29.datas.apis;

import java.util.ArrayList;

public class WeatherResponse {

    public Fact fact;
    public ArrayList<Forecast> forecasts;

    public class Forecast {

        public String date;
        public ArrayList<Hour> hours;
        public class Hour {

            public String hour;
            public Integer temp;
            public String condition;

        }

    }
    public class Fact {

        public Integer temp;
        public String condition;

    }

}
