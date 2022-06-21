package com.mka1ugin;

public class Membrane {

    private Point point;
    private MembraneType type;

    public Membrane(Point point, MembraneType type) {
        this.point = point;
        this.type = type;
    }

    public Point getPoint() {
        return this.point;
    }

    public MembraneType getType() {
        return this.type;
    }

    public static MembraneType stringToMembraneType(String data) {
        switch (data) {
            case "TYPE_1":
                return MembraneType.TYPE_1;
            case "TYPE_2":
                return MembraneType.TYPE_2;
            case "TYPE_3":
                return MembraneType.TYPE_3;
            case "TYPE_4":
                return MembraneType.TYPE_4;
            case "TYPE_5":
                return MembraneType.TYPE_5;
            case "TYPE_6":
                return MembraneType.TYPE_6;
            case "TYPE_7":
                return MembraneType.TYPE_7;
            case "TYPE_8":
                return MembraneType.TYPE_8;
            case "TYPE_9":
                return MembraneType.TYPE_9;
            case "TYPE_10":
                return MembraneType.TYPE_10;
            default:
                throw new IllegalArgumentException("Invalid membrane type: " + data);
        }
    }
}