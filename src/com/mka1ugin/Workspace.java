package com.mka1ugin;

import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.State;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;

import com.dobot.api.Dashboard;
import com.dobot.api.DobotMove;
import com.dobot.api.Feedback;

public class Workspace {

    private String ipAddress = "192.168.2.6";
    private int dbPort = 29999;
    private int mvPort = 30003;
    private int fbPort = 30004;

    Map<MembraneType, Point> pickPoints = new HashMap<>();
    Program program;

    private String pickPointsPath = "//pickpoints.csv";

    // TODO
    private Double placeZ = 50.0;

    private Double progress = 0.0;

    private int speedJ = 100;
    private int speedL = 100;
    private int accJ = 100;
    private int accL = 100;
    private int liftPick = 25;
    private int liftPlace = 25;

    private Double delayPick = 0.5;
    private Double delayPlace = 0.5;

    private int pickCS = 0;
    private int placeCS = 0;
    private int toolCS = 0;

    private DO pumpPort = DO.DO1;
    private DO valvePort = DO.DO2;

    private Dashboard dashboard = new Dashboard();
    private DobotMove dobotmove = new DobotMove();
    private Feedback feedback = new Feedback();

    private boolean isPaused = false;

    private Thread programThread;

    private boolean threadExists = false;

    private JTextArea logArea;
    private String pathToPoints;

    public Workspace() {
    };

    public Workspace(String ipAddress,
            int dbPort,
            int mvPort,
            int fbPort) {

        this.ipAddress = ipAddress;
        this.dbPort = dbPort;
        this.mvPort = mvPort;
        this.fbPort = fbPort;

    }

    public void acceptSettings(String ipAddress,
            String pickPointsPath,
            int speedJ,
            int speedL,
            int accJ,
            int accL,
            int liftPick,
            int liftPlace,
            Double delayPick,
            Double delayPlace,
            DO pumpPort,
            DO valvePort,
            int pickCS,
            int placeCS,
            int toolCS,
            Double placeZ) {
        this.ipAddress = ipAddress;
        this.pickPointsPath = pickPointsPath;
        this.speedJ = speedJ;
        this.speedL = speedL;
        this.accJ = accJ;
        this.accL = accL;
        this.liftPick = liftPick;
        this.liftPlace = liftPlace;
        this.delayPick = delayPick;
        this.delayPlace = delayPlace;
        this.pumpPort = pumpPort;
        this.valvePort = valvePort;
        this.pickCS = pickCS;
        this.placeCS = placeCS;
        this.toolCS = toolCS;
        this.placeZ = placeZ;

        try {
            loadPickpoints(pickPointsPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDebugArea(JTextArea debugTextArea) {
        DobotMove move = new DobotMove();
        move.setDebugArea(debugTextArea);
    }

    public Double getPlaceZ() {
        return this.placeZ;
    }

    public boolean connectRobot() {

        return (this.dashboard.connect(ipAddress, dbPort) && this.dobotmove.connect(ipAddress, mvPort)
                && this.feedback.connect(ipAddress, fbPort));

    }

    public boolean isConnected() {
        return (this.dashboard.isConnected()
                && this.dobotmove.isConnected()
                && this.feedback.isConnected());
    }

    public boolean disconnectRobot() {

        this.dashboard.disconnect();
        this.dobotmove.disconnect();
        this.feedback.disconnect();

        return (!this.dashboard.isConnected()
                && !this.dobotmove.isConnected()
                && !this.feedback.isConnected());

    }

    public boolean loadProgram(Program program) {

        if (program.size() > 0) {
            this.program = program;
            return true;
        }

        return false;

    }

    public Program getProgram() {
        return this.program;
    }

    public Dashboard dashboard() {
        return this.dashboard;
    }

    public DobotMove dobotmove() {
        return this.dobotmove;
    }

    public Feedback feedback() {
        return this.feedback;
    }

    public boolean loadPickpoints(String fileName) throws IOException {

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

        if (lines.length < 1) {
            return false;
        }

        for (String line : lines) {

            String[] payload = line.split(",");

            if (payload.length != 6) {
                throw new IOException("Wrong line length!");
            }

            int i = Integer.parseInt(payload[0]);
            MembraneType type = Membrane.stringToMembraneType(payload[1]);
            Double x = Double.parseDouble(payload[2]);
            Double y = Double.parseDouble(payload[3]);
            Double z = Double.parseDouble(payload[4]);
            Double r = Double.parseDouble(payload[5]);

            this.pickPoints.put(type, new Point(x, y, z, r));

        }

        return true;

    }

    @Deprecated
    public boolean runProgram() {

        if (this.program.size() < 1) {
            return false;
        }

        for (int i : this.program.getPoints().keySet()) {

            Point place = this.program.getPoints().get(i).getPoint();
            Point abovePlace = new Point(place.x, place.y, place.z + this.liftPlace, place.r);
            MembraneType type = this.program.getPoints().get(i).getType();

            Point pick = this.pickPoints.get(type);
            Point abovePick = new Point(pick.x, pick.y, pick.z + this.liftPick, pick.r);

            dobotmove.MovJ(abovePick.getMovJEntity());
            dobotmove.waitForReach(this.dashboard, abovePick);

            dobotmove.MovL(pick.getMovLEntity());
            dobotmove.waitForReach(this.dashboard, pick);

            // TODO

            dobotmove.MovL(abovePick.getMovLEntity());
            dobotmove.waitForReach(this.dashboard, abovePick);

            dobotmove.MovJ(abovePlace.getMovJEntity());
            dobotmove.waitForReach(this.dashboard, abovePlace);

            dobotmove.MovL(place.getMovLEntity());
            dobotmove.waitForReach(this.dashboard, place);

            // TODO

            dobotmove.MovL(abovePlace.getMovLEntity());
            dobotmove.waitForReach(this.dashboard, abovePlace);

            this.progress = (i + 1.0) * 100.0 / this.program.size();
        }
        return true;
    }

    public void runProgramThread(JTextArea logArea, String pathToPoints) {

        this.logArea = logArea;
        this.pathToPoints = pathToPoints;

        if (!threadExists) {
            programThread = getNewProgramThread();
            threadExists = true;
        }

        programThread.setPriority(Thread.MAX_PRIORITY);
        programThread.start();
    }

    public void pauseProgramThread() {

        if (!isPaused) {
            isPaused = true;
            programThread.suspend();
        } else {
            isPaused = false;
            programThread.resume();
        }
    }

    public void stopProgramThread() {

        programThread.stop();
        this.progress = 0.0;
        threadExists = false;
    }

    public Double getProgress() {
        return this.progress;
    }

    @Deprecated
    public void testPickPoints() {

        Double liftZ = 100.0;

        int i = 1;

        for (MembraneType type : this.pickPoints.keySet()) {

            System.out.println("Pickpoint for " + type.name());

            Point p = this.pickPoints.get(type);

            Point abovePoint = new Point(p.getX(),
                    p.getY(),
                    p.getZ() + liftZ,
                    p.getR());

            dobotmove.MovJ(abovePoint.getMovJEntity());
            dobotmove.waitForReach(dashboard, abovePoint);

            dobotmove.MovL(p.getMovLEntity());
            dobotmove.waitForReach(dashboard, p);

            dobotmove.MovL(abovePoint.getMovLEntity());
            dobotmove.waitForReach(dashboard, abovePoint);

            i++;

        }

    }

    public Thread getNewProgramThread() {

        Thread programThread = new Thread() {
            public void run() {

                if (!isConnected()) {
                    connectRobot();
                }

                if (program.size() < 1) {
                    return;
                }



                for (int i : program.getPoints().keySet()) {

                    Point place = program.getPoints().get(i).getPoint();
                    Point abovePlace = new Point(place.x, place.y, place.z + liftPlace, place.r);
                    MembraneType type = program.getPoints().get(i).getType();

                    Point pick = pickPoints.get(type);
                    Point abovePick = new Point(pick.x, pick.y, pick.z + liftPick, pick.r);

                    dobotmove.MovJ(abovePick.getMovJEntity(speedJ, accJ, pickCS, toolCS));
                    while (!dobotmove.waitForReach(dashboard, abovePick)) {
                        try {
                            sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    dobotmove.MovL(pick.getMovLEntity(speedL, accL, pickCS, toolCS));
                    while (!dobotmove.waitForReach(dashboard, pick)) {
                        try {
                            sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    dashboard.DigitalOutputs(valvePort.ordinal() + 1, false); // turns off reverse valve
                    dashboard.DigitalOutputs(pumpPort.ordinal() + 1, true); // turns on pump

                    try {
                        sleep((long) (delayPick * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    dobotmove.MovL(abovePick.getMovLEntity(speedL, accL, pickCS, toolCS));
                    while (!dobotmove.waitForReach(dashboard, abovePick)) {
                        try {
                            sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                   dobotmove.MovJ(abovePlace.getMovJEntity(speedJ, accJ, placeCS, toolCS));
                    while (!dobotmove.waitForReach(dashboard, abovePlace)) {
                        try {
                            sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    dobotmove.MovL(place.getMovLEntity(speedL, accL, placeCS, toolCS));
                    while (!dobotmove.waitForReach(dashboard, place)) {
                        try {
                            sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    dashboard.DigitalOutputs(valvePort.ordinal() + 1, true); // turns on reverse valve
                    dashboard.DigitalOutputs(pumpPort.ordinal() + 1, true); // turns on pump

                    try {
                        sleep((long) (delayPlace * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    dashboard.DigitalOutputs(pumpPort.ordinal() + 1, false); // turns off pump

                    dobotmove.MovL(abovePlace.getMovLEntity(speedL, accL, placeCS, toolCS));
                    while (!dobotmove.waitForReach(dashboard, abovePlace)) {
                        try {
                            sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    progress = (i + 1.0) * 100.0 / program.size();
                }
                logArea.append(LocalTime.now() + ": Завершена программа " + pathToPoints + "\r\n");
            }
        };
        return programThread;
    }
}