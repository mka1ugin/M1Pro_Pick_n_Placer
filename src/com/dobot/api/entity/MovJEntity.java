package com.dobot.api.entity;

public class MovJEntity {
    public Double X;
    public Double Y;
    public Double Z;
    public Double R;
    public Integer User;
    public Integer Tool;
    public Integer SpeedJ;
    public Integer AccJ;

    public MovJEntity() {
    };

    public MovJEntity(Double x,
            Double y,
            Double z,
            Double r) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.R = r;
    }

    public MovJEntity(Double x,
            Double y,
            Double z,
            Double r,
            int speedJ,
            int accJ,
            int userCS,
            int toolCS) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.R = r;
        this.SpeedJ = speedJ;
        this.AccJ = accJ;
        this.User = userCS;
        this.Tool = toolCS;
    }

}
