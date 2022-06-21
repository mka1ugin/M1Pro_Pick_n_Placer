import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.mka1ugin.MainUI;
import com.mka1ugin.SettingsUI;
import com.mka1ugin.Workspace;

public class App extends JFrame {
    public static void main(String[] args) throws ParseException {

        loadProps();
        App app = new App();

    }

    static Properties props = new Properties();

    static String ip = "192.168.2.6";
    static int dbPort = 29999;
    static int mvPort = 30003;
    static int fbPort = 30004;

    boolean programLoaded = false;

    static String pathToPoints = "C:\\TCP-IP-4Axis-Java\\points.csv";

    Workspace workspace;

    JPanel panelMain = new JPanel();
    JPanel panelSettings = new JPanel();
    JTabbedPane tabbedPane = new JTabbedPane();

    private static void loadProps() {

        try (InputStream input = new FileInputStream("config.properties")) {
            props = new Properties();
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ip = props.getProperty("ipAddress");
        dbPort = Integer.parseInt(props.getProperty("dbPort"));
        mvPort = Integer.parseInt(props.getProperty("mvPort"));
        fbPort = Integer.parseInt(props.getProperty("fbPort"));
        pathToPoints = props.getProperty("pathToPoints");

    }

    private static void saveProps() {

        try (OutputStream output = new FileOutputStream("config.properties")) {
            props.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public App() throws ParseException {

        loadProps();
        workspace = new Workspace(ip, dbPort, mvPort, fbPort);

        this.setSize(1000, 1000);
        this.setTitle("Установщик мембран M1 Pro");
        this.setLocation(200, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.getContentPane().setLayout(
                new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));        

        tabbedPane.addTab("Главная", panelMain);
        tabbedPane.addTab("Настройки", panelSettings);

        SettingsUI.generateSettingsUI(panelSettings, workspace, props);
        MainUI.generateMainUI(panelMain, workspace, this, props);

        refreshInfo.setPriority(Thread.MIN_PRIORITY);
        refreshInfo.start();

        this.setJMenuBar(MainUI.generateUpperMenu(panelMain, workspace, this));

        this.add(tabbedPane);
        this.pack();
        this.setVisible(true);

    }

    Thread refreshInfo = new Thread() {
        public void run() {
            while (true) {
                MainUI.setProgressLabelValue(
                        "Прогресс " + new DecimalFormat("#0.0").format(workspace.getProgress()) + "%",
                        workspace.getProgress().intValue());
                MainUI.setRobotState(workspace);
                MainUI.setRobotPosition(workspace);
                try {
                    sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}