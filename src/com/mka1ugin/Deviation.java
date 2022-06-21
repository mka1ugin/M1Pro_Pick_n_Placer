package com.mka1ugin;

import com.dobot.api.Dashboard;

public class Deviation {

    public static Double getDeviation(Dashboard dashboard,
            Point target) {

        Double deviation = 0.0;
        if (dashboard.getPose()) {
            Point fact = new Point(dashboard.ToolVectorActual()[0],
                    dashboard.ToolVectorActual()[1],
                    dashboard.ToolVectorActual()[2],
                    dashboard.ToolVectorActual()[3]);
            deviation = fact.getDeviation(target);
        }

        return deviation;

    }
}