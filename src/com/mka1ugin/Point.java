package com.mka1ugin;

import com.dobot.api.entity.MovLEntity;
import com.dobot.api.entity.MovJEntity;

public class Point {

    Double x;
    Double y;
    Double z;
    Double r;

    public Point(Double x,
            Double y,
            Double z,
            Double r) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
    }

    public MovLEntity getMovLEntity() {
        return new MovLEntity(this.x, this.y, this.z, this.r);
    }

    public MovJEntity getMovJEntity() {
        return new MovJEntity(this.x, this.y, this.z, this.r);
    }

    public Double getDeviation(Point p2) {
        return Math.pow(Math.pow((p2.x - this.x), 2) + Math.pow((p2.y - this.y), 2) + Math.pow((p2.z - this.z), 2)
                + Math.pow((p2.r - this.r), 2), 0.5);
    }
}