package com.example.pr29.datas.databases;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.pr29.domains.models.Day;

import java.util.ArrayList;
import java.util.List;

public class WeatherContext {

    public static ArrayList<Day> allDays() {
        ArrayList<Day> days = new ArrayList<>();

        Cursor cursor = DbContext.sqLiteDatabase.query(
                "Days",
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst() == false) {
            cursor.close();
            return days;
        }

        do {
            Day day = new Day(
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3)
            );
            days.add(day);
        } while (cursor.moveToNext());

        cursor.close();
        return days;
    }

    public static void save(List<Day> days) {
        DbContext.sqLiteDatabase.delete("Days", null, null);

        for (Day day : days) {
            ContentValues cvDay = new ContentValues();
            cvDay.put("Name", day.Name);
            cvDay.put("Temp", day.Temp);
            cvDay.put("Condition", day.Condition);

            DbContext.sqLiteDatabase.insert("Days", null, cvDay);
        }
    }
}
