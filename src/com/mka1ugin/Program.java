package com.mka1ugin;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Program {

    private Map<Integer, Membrane> points = new HashMap<>();
    private Map<MembraneType, Double> angles = new HashMap<>();

    public Program(String fileName, Double placeZ) throws IOException {

        StringBuilder sb = new StringBuilder();

        try (FileReader reader = new FileReader(fileName)) {

            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }

        } catch (IOException ex) {
            throw new IOException("File is missing!");
        }

        String[] lines = sb.toString().split("\r\n");

        int i = 0;

        for (String line : lines) {

            String[] payload = line.split(",");

            if (payload.length != 4) {
                throw new IOException("Wrong line length!");
            }

            MembraneType type = Membrane.stringToMembraneType(payload[0]);
            Double x = Double.parseDouble(payload[1]);
            Double y = Double.parseDouble(payload[2]);
            Double z = placeZ;
            Double r = Double.parseDouble(payload[3]);

            this.points.put(i, new Membrane(new Point(x, y, z, r), type));

            i++;

        }
    }

    public Membrane getItem(int index) {
        return this.points.get(index);
    }

    public int size() {
        return this.points.size();
    }

    public Map<Integer, Membrane> getPoints() {
        return this.points;
    }
}