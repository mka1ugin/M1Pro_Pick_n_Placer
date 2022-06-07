import com.dobot.api.Dashboard;
import com.dobot.api.DobotMove;
import com.dobot.api.Feedback;
import com.mka1ugin.Deviation;
import com.mka1ugin.Point;

class Sandbox {
    public static void main(String[] args) {
        final String ip = "192.168.2.6";
        final int dbPort = 29999;
        final int mvPort = 30003;
        final int fbPort = 30004;  

        Dashboard dashboard = new Dashboard();
        DobotMove dobotmove = new DobotMove();
        Feedback feedback = new Feedback();

        dashboard.connect(ip, dbPort);
        dobotmove.connect(ip, mvPort);
        feedback.connect(ip, fbPort);

        dobotmove.setMaxDeviation(0.01);

        Point initPos = new Point(400.0, 0.0, 200.0, 0.0);
        Point p1 = new Point(-60.0, -300.0, 200.0, 0.0);
        Point p2 = new Point(-60.0, -300.0, 100.0, 0.0);
        Point p3 = new Point(250.0, -300.0, 200.0, 0.0);
        Point p4 = new Point(250.0, -300.0, 100.0, 0.0);        

        dashboard.resetRobot();
        dashboard.speedFactor(100);
        dashboard.speedJ(100);
        dashboard.speedL(100);
        dashboard.AccJ(100);
        dashboard.AccL(100);

        for (int i = 0; i < 5; i++) {
            dobotmove.MovJ(p1.getMovJEntity());
            System.out.println(dobotmove.waitForReach(dashboard, p1) ? "OK, deviation = " + Deviation.getDeviation(dashboard, p1) : "NOT OK");

            dobotmove.MovL(p2.getMovLEntity());
            System.out.println(dobotmove.waitForReach(dashboard, p2) ? "OK, deviation = " + Deviation.getDeviation(dashboard, p2) : "NOT OK");
            dashboard.DigitalOutputs(1, true);

            dobotmove.MovL(p1.getMovLEntity());
            System.out.println(dobotmove.waitForReach(dashboard, p1) ? "OK, deviation = " + Deviation.getDeviation(dashboard, p1) : "NOT OK");

            dobotmove.MovJ(p3.getMovJEntity());
            System.out.println(dobotmove.waitForReach(dashboard, p3) ? "OK, deviation = " + Deviation.getDeviation(dashboard, p3) : "NOT OK");

            dobotmove.MovL(p4.getMovLEntity());
            System.out.println(dobotmove.waitForReach(dashboard, p4) ? "OK, deviation = " + Deviation.getDeviation(dashboard, p4) : "NOT OK");
            dashboard.DigitalOutputs(1, false);

            dobotmove.MovL(p3.getMovLEntity());
            System.out.println(dobotmove.waitForReach(dashboard, p3) ? "OK, deviation = " + Deviation.getDeviation(dashboard, p3) : "NOT OK");
        }      

        dashboard.disconnect();
        dobotmove.disconnect();
        feedback.disconnect();

    }
}