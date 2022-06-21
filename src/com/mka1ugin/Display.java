package com.mka1ugin;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Display extends JComponent {

    private int width = 500;
    private int height = 500;
    private int canvasOriginX = 25;
    private int canvasOriginY = 25;

    private Program program;

    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2d = (Graphics2D) g;

        drawMesh(g2d);

        drawLegend(g2d, canvasOriginX + width + 15, canvasOriginY, 30);

        drawProgram(g2d);

        super.repaint();
    }

    public void loadProgram(Program program) {
        this.program = program;
    }

    public void drawProgram(Graphics2D g2d) {

        if (this.program == null) {
            return;
        }

        for (int i = 0; i < program.size(); i++) {

            drawMembrane(g2d, program.getItem(i).getType(), program.getItem(i).getPoint().x / 2 + 250,
                    program.getItem(i).getPoint().y / 2 + 250, 8);

        }

    }

    public void drawMesh(Graphics2D g2d) {
        g2d.setPaint(Color.decode("#a8d5e5"));
        g2d.fillRect(canvasOriginX, canvasOriginY, width, height);

        g2d.setPaint(Color.decode("#66b6d2"));
        g2d.drawRect(canvasOriginX, canvasOriginY, width, height);

        for (int i = 50; i < width; i += 50) {
            g2d.drawLine(canvasOriginX + i, canvasOriginY,
                    canvasOriginX + i, canvasOriginY + height);
        }

        for (int i = 50; i < height; i += 50) {
            g2d.drawLine(canvasOriginX, canvasOriginY + i,
                    canvasOriginX + width, canvasOriginY + i);
        }

        g2d.drawString("-500", canvasOriginX - 10, canvasOriginY - 5);
        g2d.drawString("0", 22 + width / 2, canvasOriginY - 5);
        g2d.drawString(String.valueOf(width), 15 + width, canvasOriginY - 5);


        g2d.drawString(String.valueOf(height), canvasOriginX - 10, canvasOriginY * 2 + height);
    }

    public void drawPoint(Graphics2D g2d, int x, int y, int canvasOriginX, int canvasOriginY, Color color, int dia) {

        g2d.setPaint(color);
        g2d.fillOval(x - dia / 2 + canvasOriginX, y - dia / 2 + canvasOriginY, dia, dia);

    }

    public void drawCross(Graphics2D g2d, int x, int y, int canvasOriginX, int canvasOriginY, Color color, int size) {
        g2d.setPaint(color);
        g2d.drawOval(x - size / 2 + canvasOriginX, y - size / 2 + canvasOriginY, size, size);
        g2d.drawLine(canvasOriginX + x - size / 2, canvasOriginY + y - size / 2, canvasOriginX + x + size / 2,
                canvasOriginY + y + size / 2);
        g2d.drawLine(canvasOriginX + x - size / 2, canvasOriginY + y + size / 2, canvasOriginX + x + size / 2,
                canvasOriginY + y - size / 2);
    }

    public void drawLegend(Graphics2D g2d, int x, int y, int step) {

        int i = 0;
        int t = 1;
        for (MembraneType type : MembraneType.values()) {
            drawMembrane(g2d, type, x, y + i, 8);
            g2d.setPaint(Color.BLACK);
            g2d.drawString("Мембрана типа " + t, x + 10 + this.canvasOriginX, y + 5 + i + this.canvasOriginY);
            i += step;
            t += 1;
        }

    }

    public Color getMembraneColor(MembraneType type) {
        switch (type) {
            case TYPE_1:
            case TYPE_6:
                return Color.decode("#E21b53");
            case TYPE_2:
            case TYPE_7:
                return Color.decode("#E2df1b");
            case TYPE_3:
            case TYPE_8:
                return Color.decode("#1be237");
            case TYPE_4:
            case TYPE_9:
                return Color.decode("#1b5ae2");
            case TYPE_5:
            case TYPE_10:
                return Color.decode("#E21bde");
            default:
                return Color.decode("#1be2df");

        }
    }

    public void drawMembrane(Graphics2D g2d, MembraneType type, int x, int y, int size) {
        switch (type) {
            case TYPE_1:
                drawPoint(g2d, x, y, this.canvasOriginX, this.canvasOriginY, getMembraneColor(type),
                        size);
                break;
            case TYPE_2:
                drawPoint(g2d, x, y, this.canvasOriginX, this.canvasOriginY, getMembraneColor(type),
                        size);
                break;
            case TYPE_3:
                drawPoint(g2d, x, y, this.canvasOriginX, this.canvasOriginY, getMembraneColor(type),
                        size);
                break;
            case TYPE_4:
                drawPoint(g2d, x, y, this.canvasOriginX, this.canvasOriginY, getMembraneColor(type),
                        size);
                break;
            case TYPE_5:
                drawPoint(g2d, x, y, this.canvasOriginX, this.canvasOriginY, getMembraneColor(type),
                        size);
                break;
            case TYPE_6:
                drawCross(g2d, x, y, this.canvasOriginX, this.canvasOriginY, getMembraneColor(type),
                        size);
                break;
            case TYPE_7:
                drawCross(g2d, x, y, this.canvasOriginX, this.canvasOriginY, getMembraneColor(type),
                        size);
                break;
            case TYPE_8:
                drawCross(g2d, x, y, this.canvasOriginX, this.canvasOriginY, getMembraneColor(type),
                        size);
                break;
            case TYPE_9:
                drawCross(g2d, x, y, this.canvasOriginX, this.canvasOriginY, getMembraneColor(type),
                        size);
                break;
            case TYPE_10:
                drawCross(g2d, x, y, this.canvasOriginX, this.canvasOriginY, getMembraneColor(type),
                        size);
                break;
        }
    }

    public void drawMembrane(Graphics2D g2d, MembraneType type, Double x, Double y, int size) {
        switch (type) {
            case TYPE_1:
                drawPoint(g2d, x.intValue(), y.intValue(), this.canvasOriginX, this.canvasOriginY,
                        getMembraneColor(type),
                        size);
                break;
            case TYPE_2:
                drawPoint(g2d, x.intValue(), y.intValue(), this.canvasOriginX, this.canvasOriginY,
                        getMembraneColor(type),
                        size);
                break;
            case TYPE_3:
                drawPoint(g2d, x.intValue(), y.intValue(), this.canvasOriginX, this.canvasOriginY,
                        getMembraneColor(type),
                        size);
                break;
            case TYPE_4:
                drawPoint(g2d, x.intValue(), y.intValue(), this.canvasOriginX, this.canvasOriginY,
                        getMembraneColor(type),
                        size);
                break;
            case TYPE_5:
                drawPoint(g2d, x.intValue(), y.intValue(), this.canvasOriginX, this.canvasOriginY,
                        getMembraneColor(type),
                        size);
                break;
            case TYPE_6:
                drawCross(g2d, x.intValue(), y.intValue(), this.canvasOriginX, this.canvasOriginY,
                        getMembraneColor(type),
                        size);
                break;
            case TYPE_7:
                drawCross(g2d, x.intValue(), y.intValue(), this.canvasOriginX, this.canvasOriginY,
                        getMembraneColor(type),
                        size);
                break;
            case TYPE_8:
                drawCross(g2d, x.intValue(), y.intValue(), this.canvasOriginX, this.canvasOriginY,
                        getMembraneColor(type),
                        size);
                break;
            case TYPE_9:
                drawCross(g2d, x.intValue(), y.intValue(), this.canvasOriginX, this.canvasOriginY,
                        getMembraneColor(type),
                        size);
                break;
            case TYPE_10:
                drawCross(g2d, x.intValue(), y.intValue(), this.canvasOriginX, this.canvasOriginY,
                        getMembraneColor(type),
                        size);
                break;
        }
    }
}