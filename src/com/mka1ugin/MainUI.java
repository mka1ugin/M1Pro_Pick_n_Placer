package com.mka1ugin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultCaret;

public class MainUI {

    static boolean isConnected = false;

    static String pathToPoints;
    static String pickpointsPath;

    static JLabel filenameLabel = new JLabel();
    static JLabel progressLabel = new JLabel();
    static JLabel speedRatio = new JLabel();
    static JLabel robotState = new JLabel();
    static JLabel robotPosition = new JLabel("X: ?, Y: ?, Z: ?, R: ?");

    static JLabel programInfo = new JLabel();

    static String currentState = "";

    static JSlider speedRatioSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 100, 100);

    static JProgressBar progressBar = new JProgressBar();

    static JTextArea logArea = new JTextArea(10, 1);
    static JScrollPane logAreaPane = new JScrollPane(logArea);

    static JMenuBar menuBar = new JMenuBar();
    static JMenu menuFile = new JMenu("Файл");
    static JMenu menuProgram = new JMenu("Программа");
    static JMenuItem menuItemOpen = new JMenuItem("Открыть");
    static JMenuItem menuItemExit = new JMenuItem("Выход");
    static JMenuItem menuItemRun = new JMenuItem("Пуск");
    static JMenuItem menuItemPause = new JMenuItem("Пауза/Возобновить");
    static JMenuItem menuItemStop = new JMenuItem("Стоп");

    static JButton startButton = new JButton("Старт");
    static JButton pauseButton = new JButton("Пауза/Продолжить");
    static JButton stopButton = new JButton("Стоп");

    static JTextField programTextField = new JTextField();

    static JTextArea debugTextArea = new JTextArea();

    static Display display = new Display();

    public static void generateMainUI(JPanel panel, Workspace workspace, JFrame frame, Properties props) {

        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(1000, 1000));

        pathToPoints = props.getProperty("pathToPoints");
        pickpointsPath = props.getProperty("pickpointsPath");

        workspace.setDebugArea(debugTextArea);

        generateUpperButtons(panel, workspace);
        generateStatusField(panel);
        generateSpeedSlider(panel, workspace);
        generateFileMenu(panel, frame, workspace);
        generateProgressBar(panel);
        // generateDisplay(panel);
        generateLogArea(panel);
        generateDebugArea(panel, frame);

        try {

            workspace.loadPickpoints(pickpointsPath);
            workspace.loadProgram(new Program(pathToPoints, workspace.getPlaceZ()));

            if (workspace.program.size() != 0) {

                frame.setTitle("Установщик мембран M1 Pro" + " - " + pathToPoints);
                logArea.append(LocalTime.now() + ": Загружена программа "
                        + pathToPoints + "\r\n");
                programInfo.setText("Программа содержит " + workspace.program.size() + " точек установки мембран");

                menuItemRun.setEnabled(true);
                startButton.setEnabled(true);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

    }

    private static String generateFileChooser(JPanel panel) {

        JFileChooser fc = new JFileChooser("C:\\");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".csv");
                }
            }

            @Override
            public String getDescription() {
                return "Файлы CSV";
            }

        });

        int option = fc.showOpenDialog(panel);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            return file.getAbsolutePath();
        } else {
            return "файл не выбран";
        }

    }

    public static JMenuBar generateUpperMenu(JPanel panel, Workspace workspace, JFrame frame) {

        menuItemRun.setEnabled(false);
        menuItemPause.setEnabled(false);
        menuItemStop.setEnabled(false);

        menuItemExit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                workspace.disconnectRobot();
                System.exit(0);
            }
        });

        menuItemOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pathToPoints = generateFileChooser(panel);
                filenameLabel.setText("Файл программы: " + pathToPoints);
                try {
                    workspace.loadPickpoints(pickpointsPath);
                    workspace.loadProgram(new Program(pathToPoints, workspace.getPlaceZ()));

                    frame.setTitle("Установщик мембран M1 Pro" + " - " + pathToPoints);
                    logArea.append(LocalTime.now() + ": Загружена программа "
                            + pathToPoints + "\r\n");

                    programInfo.setText("Программа содержит " + workspace.program.size() + " точек установки мембран");

                    menuItemRun.setEnabled(true);
                    startButton.setEnabled(true);

                } catch (IOException e1) {
                    e1.printStackTrace();
                    pathToPoints = "";
                    filenameLabel.setText("Файл программы: " + pathToPoints);
                    frame.setTitle("Установщик мембран M1 Pro" + " - " + pathToPoints);
                    JOptionPane.showMessageDialog(null,
                            "Невозможно загрузить файл программы!",
                            "Ошибка загрузки!",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        menuItemRun.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                Thread thread = new Thread() {
                    public void run() {

                        logArea.append(LocalTime.now() + ": Запущена программа " + pathToPoints + "\r\n");

                        menuItemRun.setEnabled(false);
                        menuItemPause.setEnabled(true);
                        menuItemStop.setEnabled(true);

                        pauseButton.setEnabled(true);
                        stopButton.setEnabled(true);

                        if (!workspace.isConnected()) {
                            workspace.connectRobot();
                        }
                        workspace.runProgramThread(logArea, pathToPoints);

                        menuItemRun.setEnabled(true);
                    }
                };

                thread.start();
            }
        });

        menuItemPause.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread() {
                    public void run() {

                        menuItemRun.setEnabled(false);
                        menuItemPause.setEnabled(true);
                        menuItemStop.setEnabled(true);

                        startButton.setEnabled(false);
                        pauseButton.setEnabled(true);
                        stopButton.setEnabled(true);

                        workspace.pauseProgramThread();
                    }
                };

                thread.start();
            }
        });

        menuItemStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread() {
                    public void run() {

                        DecimalFormat df = new DecimalFormat("#0.0");
                        logArea.append(LocalTime.now() + ": Прервана программа " + pathToPoints + ", выполнено "
                                + df.format(workspace.getProgress()) + "%.\r\n");

                        menuItemRun.setEnabled(true);
                        menuItemPause.setEnabled(false);
                        menuItemStop.setEnabled(false);

                        startButton.setEnabled(true);
                        pauseButton.setEnabled(false);
                        stopButton.setEnabled(false);

                        workspace.stopProgramThread();
                    }
                };

                thread.start();
            }
        });

        menuFile.add(menuItemOpen);
        menuFile.add(menuItemExit);
        menuBar.add(menuFile);
        menuProgram.add(menuItemRun);
        menuProgram.add(menuItemPause);
        menuProgram.add(menuItemStop);
        menuBar.add(menuProgram);

        return menuBar;
    }

    public static void setProgressLabelValue(String s, int i) {
        progressLabel.setText(s);
        progressBar.setValue(i);
    }

    public static void setRobotState(Workspace workspace) {

        String temp = workspace.dashboard().robotModeRU();
        if (temp != null) {
            robotState.setText(temp);
        }
    }

    public static void setRobotPosition(Workspace workspace) {
        // workspace.dashboard().getPose();
        DecimalFormat df = new DecimalFormat("#0.00");
        robotPosition.setText("X: " + df.format(workspace.dashboard().ToolVectorActual()[0]) +
                ", Y: " + df.format(workspace.dashboard().ToolVectorActual()[1]) +
                ", Z: " + df.format(workspace.dashboard().ToolVectorActual()[2]) +
                ", R: " + df.format(workspace.dashboard().ToolVectorActual()[3]));
    }

    public static JTextArea getDebugArea() {
        return debugTextArea;
    }

    private static void generateUpperButtons(JPanel panel, Workspace workspace) {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Управление роботом");
        title.setTitleJustification(TitledBorder.CENTER);

        Box box = Box.createHorizontalBox();
        box.setBorder(title);

        JButton connectButton = new JButton("Подключить");
        JButton disconnectButton = new JButton("Отключить");
        JButton enableButton = new JButton("Активировать");
        JButton disableButton = new JButton("Деактивировать");
        JButton resetButton = new JButton("Сброс ошибок");

        disconnectButton.setEnabled(false);
        enableButton.setEnabled(false);
        disableButton.setEnabled(false);
        resetButton.setEnabled(false);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isConnected = workspace.connectRobot();
                if (isConnected) {
                    disconnectButton.setEnabled(true);
                    enableButton.setEnabled(true);
                    disableButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    connectButton.setEnabled(false);
                    workspace.dashboard().getPose();
                }
            }
        });

        disconnectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                isConnected = !workspace.disconnectRobot();
                if (isConnected) {
                    disconnectButton.setEnabled(false);
                    enableButton.setEnabled(false);
                    disableButton.setEnabled(false);
                    resetButton.setEnabled(false);
                    connectButton.setEnabled(true);
                }
            }
        });

        enableButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                workspace.dashboard().enableRobot();
            }
        });

        disableButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                workspace.dashboard().disableRobot();
            }
        });

        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                workspace.dashboard().clearError();
            }
        });

        box.add(connectButton);
        box.add(disconnectButton);
        box.add(enableButton);
        box.add(disableButton);
        box.add(resetButton);

        panel.add(box);
        panel.add(Box.createVerticalStrut(10));
    }

    private static void generateStatusField(JPanel panel) {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Состояние робота");
        title.setTitleJustification(TitledBorder.CENTER);

        Box box = Box.createVerticalBox();
        box.setBorder(title);

        Box box1 = Box.createHorizontalBox();
        Box box2 = Box.createHorizontalBox();
        box1.add(new JLabel("Состояние робота: "));
        box1.add(robotState);
        box2.add(new JLabel("Позиция робота: "));
        box2.add(robotPosition);

        box.add(box1);
        box.add(box2);

        panel.add(box);
        panel.add(Box.createVerticalStrut(10));
    }

    private static void generateSpeedSlider(JPanel panel, Workspace workspace) {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Глобальная скорость");
        title.setTitleJustification(TitledBorder.CENTER);

        Box box = Box.createVerticalBox();
        box.setBorder(title);

        speedRatioSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Thread thread = new Thread() {
                    public void run() {
                        workspace.dashboard().speedFactor(speedRatioSlider.getValue());
                    }
                };

                thread.run();

                speedRatio.setText("Скорость: " + speedRatioSlider.getValue() + " %");
            }
        });

        speedRatio.setText("Скорость: " + speedRatioSlider.getValue() + " %");
        speedRatio.setAlignmentX(Component.CENTER_ALIGNMENT);

        speedRatioSlider.setMaximumSize(new Dimension(500, 25));

        box.add(speedRatio);
        box.add(speedRatioSlider);

        panel.add(box);
        panel.add(Box.createVerticalStrut(5));

    }

    private static void generateFileMenu(JPanel panel, JFrame frame, Workspace workspace) {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Файл программы");
        title.setTitleJustification(TitledBorder.CENTER);

        Box box = Box.createVerticalBox();
        box.setBorder(title);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        Box box1 = Box.createHorizontalBox();
        Box box2 = Box.createHorizontalBox();
        Box box3 = Box.createHorizontalBox();

        programTextField.setText(pathToPoints);
        programTextField.setMinimumSize(new Dimension(800, 25));
        programTextField.setMaximumSize(new Dimension(800, 25));

        JButton button = new JButton("Выбрать файл");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pathToPoints = generateFileChooser(panel);
                filenameLabel.setText("Файл программы: " + pathToPoints);
                try {
                    workspace.loadPickpoints(pickpointsPath);
                    workspace.loadProgram(new Program(pathToPoints, workspace.getPlaceZ()));

                    frame.setTitle("Установщик мембран M1 Pro" + " - " + pathToPoints);
                    logArea.append(LocalTime.now() + ": Загружена программа "
                            + pathToPoints + "\r\n");

                    programInfo
                            .setText("Программа содержит " + workspace.program.size() + " точек установки мембран");

                    menuItemRun.setEnabled(true);
                    startButton.setEnabled(true);

                    display.loadProgram(workspace.getProgram());

                } catch (IOException e1) {
                    e1.printStackTrace();
                    pathToPoints = "";
                    filenameLabel.setText("Файл программы: " + pathToPoints);
                    frame.setTitle("Установщик мембран M1 Pro" + " - " + pathToPoints);
                    JOptionPane.showMessageDialog(null,
                            "Невозможно загрузить файл программы!",
                            "Ошибка загрузки!",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread() {
                    public void run() {
                        logArea.append(LocalTime.now() + ": Запущена программа " + pathToPoints + "\r\n");

                        menuItemRun.setEnabled(false);
                        menuItemPause.setEnabled(true);
                        menuItemStop.setEnabled(true);

                        pauseButton.setEnabled(true);
                        stopButton.setEnabled(true);

                        if (!workspace.isConnected()) {
                            workspace.connectRobot();
                        }
                        workspace.runProgramThread(logArea, pathToPoints);

                        menuItemRun.setEnabled(true);
                    }
                };
                thread.start();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread() {
                    public void run() {

                        menuItemRun.setEnabled(false);
                        menuItemPause.setEnabled(true);
                        menuItemStop.setEnabled(true);

                        startButton.setEnabled(false);
                        pauseButton.setEnabled(true);
                        stopButton.setEnabled(true);

                        workspace.pauseProgramThread();
                    }
                };
                thread.start();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread() {
                    public void run() {

                        DecimalFormat df = new DecimalFormat("#0.0");
                        logArea.append(LocalTime.now() + ": Прервана программа " + pathToPoints + ", выполнено "
                                + df.format(workspace.getProgress()) + "%.\r\n");

                        menuItemRun.setEnabled(true);
                        menuItemPause.setEnabled(false);
                        menuItemStop.setEnabled(false);

                        startButton.setEnabled(true);
                        pauseButton.setEnabled(false);
                        stopButton.setEnabled(false);

                        workspace.stopProgramThread();
                    }
                };

                thread.start();
            }
        });

        box1.add(Box.createHorizontalGlue());
        box1.add(programTextField);
        box1.add(Box.createHorizontalStrut(10));
        box1.add(button);
        box1.add(Box.createHorizontalGlue());

        box2.add(startButton);
        box2.add(pauseButton);
        box2.add(stopButton);

        box3.add(programInfo);

        box.add(box1);
        box.add(Box.createVerticalStrut(5));
        box.add(box2);
        box.add(Box.createVerticalStrut(5));
        box.add(box3);
        box.add(Box.createVerticalStrut(5));

        panel.add(box);
        panel.add(Box.createVerticalStrut(10));

    }

    private static void generateProgressBar(JPanel panel) {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Ход выполнения");
        title.setTitleJustification(TitledBorder.CENTER);

        Box box = Box.createVerticalBox();
        box.setBorder(title);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        Box box1 = Box.createHorizontalBox();
        Box box2 = Box.createHorizontalBox();

        progressBar.setStringPainted(true);
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        progressBar.setForeground(Color.decode("#00661e"));
        progressBar.setPreferredSize(new Dimension(800, 25));

        progressLabel.setText("Прогресс 0%");

        box1.add(progressBar);
        box2.add(progressLabel);

        box.add(box1);
        box.add(box2);
        box.add(Box.createVerticalStrut(5));

        panel.add(box);
        panel.add(Box.createVerticalStrut(10));
    }

    private static void generateDisplay(JPanel panel) {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Эскиз");
        title.setTitleJustification(TitledBorder.CENTER);

        Box box = Box.createHorizontalBox();
        box.setBorder(title);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 650));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);

        display.setVisible(true);
        box.add(Box.createHorizontalStrut(230));
        box.add(display);
        panel.add(box);

        panel.add(Box.createVerticalStrut(10));
    }

    private static void generateDebugArea(JPanel panel, JFrame frame) {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Debug");
        title.setTitleJustification(TitledBorder.CENTER);

        Box box = Box.createHorizontalBox();
        box.setBorder(title);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);

        debugTextArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        debugTextArea.setEditable(false);
        debugTextArea.setBackground(frame.getBackground());

        box.add(debugTextArea);
        
        panel.add(box);
    }

    private static void generateLogArea(JPanel panel) {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Лог");
        title.setTitleJustification(TitledBorder.CENTER);

        Box box = Box.createHorizontalBox();
        box.setBorder(title);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        box.add(logAreaPane);
        panel.add(box);
    }
}