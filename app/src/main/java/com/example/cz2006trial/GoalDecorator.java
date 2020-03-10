package com.example.cz2006trial;

import android.graphics.Typeface;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.HashSet;

public class GoalDecorator implements DayViewDecorator {

    private final int color;
    private final boolean bold;
    private final HashSet<CalendarDay> markedDates;

    public GoalDecorator(int color, boolean bold, ArrayList<CalendarDay> dates) {
        this.color = color;
        this.bold = bold;
        this.markedDates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return markedDates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if (bold) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.5f));
        } else {
            view.addSpan(new DotSpan(10, color));
        }
    }
}
