package com.mka1ugin;

public enum DO {
    DO1,
    DO2,
    DO3,
    DO4,
    DO5,
    DO6,
    DO7,
    DO8,
    DO9,
    DO10,
    DO11,
    DO12,
    DO13,
    DO14,
    DO15,
    DO16;

    public static DO parseDO(String s) {
        if (s.equals("DO1")) {
            return DO1;
        }
        if (s.equals("DO2")) {
            return DO2;
        }
        if (s.equals("DO3")) {
            return DO3;
        }
        if (s.equals("DO4")) {
            return DO4;
        }
        if (s.equals("DO5")) {
            return DO5;
        }
        if (s.equals("DO6")) {
            return DO6;
        }
        if (s.equals("DO7")) {
            return DO7;
        }
        if (s.equals("DO8")) {
            return DO8;
        }
        if (s.equals("DO9")) {
            return DO9;
        }
        if (s.equals("DO10")) {
            return DO10;
        }
        if (s.equals("DO11")) {
            return DO11;
        }
        if (s.equals("DO12")) {
            return DO12;
        }
        if (s.equals("DO13")) {
            return DO13;
        }
        if (s.equals("DO14")) {
            return DO14;
        }
        if (s.equals("DO15")) {
            return DO15;
        }
        if (s.equals("DO16")) {
            return DO16;
        }
        return DO1;
    }
}