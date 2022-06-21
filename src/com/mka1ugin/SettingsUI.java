package com.mka1ugin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.MaskFormatter;

public class SettingsUI {

    static String pickpointsPath = "";
    static String ipAddress = "";

    static int speedJ;
    static int speedL;
    static int accJ;
    static int accL;
    static int liftPick;
    static int liftPlace;

    static int pickCS;
    static int placeCS;
    static int toolCS;

    static Double delayPick;
    static Double delayPlace;

    static DO pumpPort;
    static DO valvePort;

    static Double placeZ;

    static JTextField pickpointsTextField = new JTextField();

    static MaskFormatter maskFormatterIp;
    static MaskFormatter maskFormatterDouble;
    static JFormattedTextField ipAddressTextField = new JFormattedTextField(maskFormatterIp);

    public static void generateSettingsUI(JPanel panel, Workspace workspace, Properties props) throws ParseException {

        maskFormatterIp = new MaskFormatter("###.###.###.###");
        maskFormatterDouble = new MaskFormatter("##.#");

        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(1000, 1000));

        loadProps(props, workspace);

        generatePickPointsPicker(panel);
        panel.add(Box.createVerticalStrut(10));
        generateMoveSettings(panel);
        panel.add(Box.createVerticalStrut(10));
        generateIpSettings(panel);
        panel.add(Box.createVerticalStrut(10));
        generateDelaySettings(panel);
        panel.add(Box.createVerticalStrut(10));
        generateIOSettings(panel);
        panel.add(Box.createVerticalStrut(10));
        generateApplyButton(panel, workspace, props);

    }

    private static void generatePickPointsPicker(JPanel panel) {

        TitledBorder title;
        title = BorderFactory.createTitledBorder("Путь к файлу с точками захвата");
        title.setTitleJustification(TitledBorder.CENTER);

        pickpointsTextField.setText(pickpointsPath);

        Box box = Box.createVerticalBox();
        Box box1 = Box.createHorizontalBox();

        box.setBorder(title);

        JButton button = new JButton("Выбрать файл");

        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        pickpointsTextField.setMaximumSize(new Dimension(600, 25));
        pickpointsTextField.setPreferredSize(new Dimension(600, 25));

        box1.add(Box.createHorizontalGlue());
        box1.add(pickpointsTextField);
        box1.add(Box.createHorizontalStrut(10));
        box1.add(button);
        box1.add(Box.createHorizontalGlue());

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
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
                    pickpointsPath = file.getAbsolutePath();
                    pickpointsTextField.setText(file.getAbsolutePath());
                }
            }
        });

        box.add(box1);

        panel.add(box);
    }

    private static void generateMoveSettings(JPanel panel) {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Настройки перемещений");
        title.setTitleJustification(TitledBorder.CENTER);

        Box box = Box.createVerticalBox();
        Box box1 = Box.createHorizontalBox();
        Box box2 = Box.createHorizontalBox();

        box.setBorder(title);

        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        Box speedJSettingBox = Box.createVerticalBox();
        Box speedLSettingBox = Box.createVerticalBox();
        Box accJSettingBox = Box.createVerticalBox();
        Box accLSettingBox = Box.createVerticalBox();
        Box liftPickSettingBox = Box.createVerticalBox();
        Box liftPlaceSettingBox = Box.createVerticalBox();

        Box picksCSSettingBox = Box.createVerticalBox();
        Box placesCSSettingBox = Box.createVerticalBox();
        Box toolCSSettingBox = Box.createVerticalBox();

        Box placeZSettingBox = Box.createVerticalBox();

        JLabel speedJLabel = new JLabel("Скорость MovJ, %");
        speedJLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        speedJSettingBox.add(speedJLabel);

        JSlider speedJSlider = new JSlider(1, 100, speedJ);
        speedJSlider.setMaximumSize(new Dimension(150, 20));
        speedJSettingBox.add(speedJSlider);

        JLabel speedJRatioLabel = new JLabel(String.valueOf(speedJ));
        speedJSettingBox.add(speedJRatioLabel);

        JLabel speedLLabel = new JLabel("Скорость MovL, %");
        speedLLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        speedLSettingBox.add(speedLLabel);

        JSlider speedLSlider = new JSlider(1, 100, speedL);
        speedLSlider.setMaximumSize(new Dimension(150, 20));
        speedLSettingBox.add(speedLSlider);

        JLabel speedLRatioLabel = new JLabel(String.valueOf(speedL));
        speedLSettingBox.add(speedLRatioLabel);

        JLabel accJLabel = new JLabel("Ускорение MovJ, %");
        accJLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        accJSettingBox.add(accJLabel);

        JSlider accJSlider = new JSlider(1, 100, accJ);
        accJSlider.setMaximumSize(new Dimension(150, 20));
        accJSettingBox.add(accJSlider);

        JLabel accJRatioLabel = new JLabel(String.valueOf(accJ));
        accJSettingBox.add(accJRatioLabel);

        JLabel accLLabel = new JLabel("Ускорение MovL, %");
        accLLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        accLSettingBox.add(accLLabel);

        JSlider accLSlider = new JSlider(1, 100, accL);
        accLSlider.setMaximumSize(new Dimension(150, 20));
        accLSettingBox.add(accLSlider);

        JLabel accLRatioLabel = new JLabel(String.valueOf(accL));
        accLSettingBox.add(accLRatioLabel);

        JLabel liftPickLabel = new JLabel("Подъём над точкой захвата, мм");
        liftPickLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        liftPickSettingBox.add(liftPickLabel);

        JSlider liftPickSlider = new JSlider(10, 100, liftPick);
        liftPickSlider.setMaximumSize(new Dimension(150, 20));
        liftPickSettingBox.add(liftPickSlider);

        JLabel liftPickRatioLabel = new JLabel(String.valueOf(liftPick));
        liftPickSettingBox.add(liftPickRatioLabel);

        JLabel liftPlaceLabel = new JLabel("Подъём над точкой монтажа, мм");
        liftPlaceLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        liftPlaceSettingBox.add(liftPlaceLabel);

        JSlider liftPlaceSlider = new JSlider(10, 100, liftPlace);
        liftPlaceSlider.setMaximumSize(new Dimension(150, 20));
        liftPlaceSettingBox.add(liftPlaceSlider);

        JLabel liftPlaceRatioLabel = new JLabel(String.valueOf(liftPlace));
        liftPlaceSettingBox.add(liftPlaceRatioLabel);

        JLabel pickCSLabel = new JLabel("СК захвата");
        pickCSLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JComboBox<Integer> pickCSComboBox = new JComboBox<>();
        picksCSSettingBox.add(pickCSLabel);
        picksCSSettingBox.add(pickCSComboBox);

        JLabel placeCSLabel = new JLabel("СК установки");
        placeCSLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JComboBox<Integer> placeCSComboBox = new JComboBox<>();
        placesCSSettingBox.add(placeCSLabel);
        placesCSSettingBox.add(placeCSComboBox);

        JLabel toolCSLabel = new JLabel("СК инструмента");
        toolCSLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JComboBox<Integer> toolCSComboBox = new JComboBox<>();
        toolCSSettingBox.add(toolCSLabel);
        toolCSSettingBox.add(toolCSComboBox);

        JLabel placeZLabel = new JLabel("Высота монтажа, мм");
        placeZLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JFormattedTextField placeZTextField = new JFormattedTextField(maskFormatterDouble);
        placeZTextField.setHorizontalAlignment(JFormattedTextField.CENTER);
        placeZTextField.setMaximumSize(new Dimension(35, 25));
        placeZTextField.setText(String.valueOf(placeZ));
        placeZSettingBox.add(placeZLabel);
        placeZSettingBox.add(placeZTextField);

        for (int i = 0; i <= 10; i++) {
            pickCSComboBox.addItem(i);
            placeCSComboBox.addItem(i);
            toolCSComboBox.addItem(i);
        }

        pickCSComboBox.setMaximumSize(new Dimension(60, 25));
        placeCSComboBox.setMaximumSize(new Dimension(60, 25));
        toolCSComboBox.setMaximumSize(new Dimension(60, 25));

        pickCSComboBox.setSelectedIndex(pickCS);
        placeCSComboBox.setSelectedIndex(placeCS);
        toolCSComboBox.setSelectedIndex(toolCS);

        speedJSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                speedJ = speedJSlider.getValue();
                speedJRatioLabel.setText(String.valueOf(speedJ));
            }

        });

        speedLSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                speedL = speedLSlider.getValue();
                speedLRatioLabel.setText(String.valueOf(speedL));
            }

        });

        accJSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                accJ = accJSlider.getValue();
                accJRatioLabel.setText(String.valueOf(accJ));
            }

        });

        accLSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                accL = accLSlider.getValue();
                accLRatioLabel.setText(String.valueOf(accL));
            }

        });

        liftPickSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                liftPick = liftPickSlider.getValue();
                liftPickRatioLabel.setText(String.valueOf(liftPick));
            }

        });

        liftPlaceSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                liftPlace = liftPlaceSlider.getValue();
                liftPlaceRatioLabel.setText(String.valueOf(liftPlace));
            }

        });

        pickCSComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pickCS = pickCSComboBox.getSelectedIndex();
            }

        });

        placeCSComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                placeCS = placeCSComboBox.getSelectedIndex();
            }

        });

        toolCSComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                toolCS = toolCSComboBox.getSelectedIndex();
            }

        });

        placeZTextField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                placeZ = Double.parseDouble(placeZTextField.getText());
            }

        });

        box1.add(Box.createHorizontalGlue());
        box1.add(speedJSettingBox);
        box1.add(speedLSettingBox);
        box1.add(accJSettingBox);
        box1.add(accLSettingBox);
        box1.add(Box.createHorizontalGlue());

        box2.add(Box.createHorizontalGlue());
        box2.add(liftPickSettingBox);
        box2.add(Box.createHorizontalStrut(10));
        box2.add(liftPlaceSettingBox);
        box2.add(Box.createHorizontalStrut(10));
        box2.add(picksCSSettingBox);
        box2.add(Box.createHorizontalStrut(22));
        box2.add(placesCSSettingBox);
        box2.add(Box.createHorizontalStrut(10));
        box2.add(toolCSSettingBox);
        box2.add(Box.createHorizontalStrut(10));
        box2.add(placeZSettingBox);
        box2.add(Box.createHorizontalGlue());

        box.add(box1);
        box.add(Box.createVerticalStrut(10));
        box.add(box2);

        panel.add(box);
    }

    private static void generateIpSettings(JPanel panel) throws ParseException {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("IP-адрес робота");
        title.setTitleJustification(TitledBorder.CENTER);

        ipAddressTextField.setText(ipAddress);

        Box box = Box.createHorizontalBox();
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        box.setBorder(title);

        ipAddressTextField.setMinimumSize(new Dimension(150, 25));
        ipAddressTextField.setMaximumSize(new Dimension(150, 25));
        JButton button = new JButton("OK");

        box.add(Box.createHorizontalGlue());
        box.add(ipAddressTextField);
        box.add(Box.createHorizontalStrut(10));
        box.add(button);
        box.add(Box.createHorizontalGlue());

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ipAddress = ipAddressTextField.getText();
            }

        });

        panel.add(box);
    }

    private static void generateDelaySettings(JPanel panel) {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Настройки задержек");
        title.setTitleJustification(TitledBorder.CENTER);

        Box box = Box.createHorizontalBox();
        box.setBorder(title);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        Box delayPickSettingBox = Box.createVerticalBox();
        JLabel delayPickLabel = new JLabel("Задержка захвата, с");
        delayPickLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        delayPickSettingBox.add(delayPickLabel);

        JSlider delayPickSlider = new JSlider(0, 100, (int) (delayPick * 10));
        delayPickSlider.setPreferredSize(new Dimension(120, 20));
        delayPickSlider.setMaximumSize(new Dimension(120, 20));
        delayPickSettingBox.add(delayPickSlider);

        JLabel delayPickRatioLabel = new JLabel(String.valueOf(delayPick));
        delayPickRatioLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        delayPickSettingBox.add(delayPickRatioLabel);

        Box delayPlaceSettingBox = Box.createVerticalBox();
        JLabel delayPlaceLabel = new JLabel("Задержка монтажа, с");
        delayPlaceLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        delayPlaceSettingBox.add(delayPlaceLabel);

        JSlider delayPlaceSlider = new JSlider(0, 100, (int) (delayPlace * 10));
        delayPlaceSlider.setPreferredSize(new Dimension(120, 20));
        delayPlaceSlider.setMaximumSize(new Dimension(120, 20));
        delayPlaceSettingBox.add(delayPlaceSlider);

        JLabel delayPlaceRatioLabel = new JLabel(String.valueOf(delayPlace));
        delayPlaceRatioLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        delayPlaceSettingBox.add(delayPlaceRatioLabel);

        DecimalFormat df = new DecimalFormat("#0.0");

        delayPickSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                delayPick = delayPickSlider.getValue() * 0.1;
                delayPickRatioLabel.setText(df.format(delayPick));
            }
        });

        delayPlaceSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                delayPlace = delayPlaceSlider.getValue() * 0.1;
                delayPlaceRatioLabel.setText(df.format(delayPlace));
            }
        });

        box.add(Box.createHorizontalGlue());
        box.add(delayPickSettingBox);
        box.add(Box.createHorizontalStrut(20));
        box.add(delayPlaceSettingBox);
        box.add(Box.createHorizontalGlue());

        panel.add(box);

    }

    private static void generateIOSettings(JPanel panel) {
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Настройки периферии");
        title.setTitleJustification(TitledBorder.CENTER);

        Box box = Box.createHorizontalBox();
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.setBorder(title);

        Box pumpPortSettingsBox = Box.createVerticalBox();
        JLabel pumpPortLabel = new JLabel("Порт насоса");
        pumpPortLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        pumpPortSettingsBox.add(pumpPortLabel);

        JComboBox<DO> pumpPortComboBox = new JComboBox<>(DO.values());
        pumpPortComboBox.setEditable(true);
        pumpPortComboBox.setMaximumSize(new Dimension(60, 25));
        pumpPortComboBox.setSelectedIndex(pumpPort.ordinal());

        Box valvePortSettingsBox = Box.createVerticalBox();
        JLabel valvePortLabel = new JLabel("Порт клапана");
        valvePortLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        valvePortSettingsBox.add(valvePortLabel);

        JComboBox<DO> valvePortComboBox = new JComboBox<>(DO.values());
        valvePortComboBox.setEditable(true);
        valvePortComboBox.setMaximumSize(new Dimension(60, 25));
        valvePortComboBox.setSelectedIndex(valvePort.ordinal());

        pumpPortSettingsBox.add(pumpPortComboBox);
        valvePortSettingsBox.add(valvePortComboBox);

        pumpPortComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pumpPort = DO.values()[pumpPortComboBox.getSelectedIndex()];
                if (valvePort == pumpPort) {
                    if (valvePort == DO.DO16) {
                        valvePortComboBox.setSelectedIndex(14);
                    } else {
                        valvePortComboBox.setSelectedIndex(pumpPortComboBox.getSelectedIndex() + 1);
                    }
                    valvePort = DO.values()[valvePortComboBox.getSelectedIndex()];
                }
            }
        });

        valvePortComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                valvePort = DO.values()[valvePortComboBox.getSelectedIndex()];
                if (valvePort == pumpPort) {
                    if (pumpPort == DO.DO1) {
                        pumpPortComboBox.setSelectedIndex(15);
                    } else {
                        pumpPortComboBox.setSelectedIndex(valvePortComboBox.getSelectedIndex() - 1);
                    }
                    pumpPort = DO.values()[pumpPortComboBox.getSelectedIndex()];
                }
            }
        });

        box.add(Box.createHorizontalGlue());
        box.add(pumpPortSettingsBox);
        box.add(valvePortSettingsBox);
        box.add(Box.createHorizontalGlue());

        panel.add(box);
    }

    private static void generateApplyButton(JPanel panel, Workspace workspace, Properties props) {

        Box box = Box.createHorizontalBox();

        JButton button = new JButton("Применить настройки");
        box.add(button);

        panel.add(box);

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                workspace.acceptSettings(ipAddress, pickpointsPath, speedJ, speedL, accJ, accL, liftPick, liftPlace,
                        delayPick, delayPlace, pumpPort, valvePort, pickCS, placeCS, toolCS, placeZ);
                saveProps(props, workspace);
            }

        });
    }

    private static void loadProps(Properties props, Workspace workspace) {

        pickpointsPath = props.getProperty("pickpointsPath", "\\pickpoints.csv");
        ipAddress = props.getProperty("ipAddress", "192.168.002.006");
        speedJ = Integer.parseInt(props.getProperty("speedJ", "100"));
        speedL = Integer.parseInt(props.getProperty("speedL", "100"));
        accJ = Integer.parseInt(props.getProperty("accJ", "100"));
        accL = Integer.parseInt(props.getProperty("accL", "100"));
        liftPick = Integer.parseInt(props.getProperty("liftPick", "25"));
        liftPlace = Integer.parseInt(props.getProperty("liftPlace", "25"));
        delayPick = Double.parseDouble(props.getProperty("delayPick", "0.5"));
        delayPlace = Double.parseDouble(props.getProperty("delayPlace", "0.5"));
        pumpPort = DO.parseDO(props.getProperty("pumpPort", "DO1"));
        valvePort = DO.parseDO(props.getProperty("valvePort", "DO2"));
        pickCS = Integer.parseInt(props.getProperty("pickCS", "0"));
        placeCS = Integer.parseInt(props.getProperty("placeCS", "0"));
        toolCS = Integer.parseInt(props.getProperty("toolCS", "0"));
        placeZ = Double.parseDouble(props.getProperty("placeZ", "50.0"));

        workspace.acceptSettings(ipAddress, pickpointsPath, speedJ, speedL, accJ, accL, liftPick, liftPlace, delayPick,
                delayPlace, pumpPort, valvePort, pickCS, placeCS, toolCS, placeZ);

    }

    private static void saveProps(Properties props, Workspace workspace) {

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.PRC);
        DecimalFormat df = (DecimalFormat) nf;
        // DecimalFormat df = new DecimalFormat("#0.0");

        props.setProperty("pickpointsPath", pickpointsPath);
        props.setProperty("ipAddress", ipAddress);
        props.setProperty("speedJ", String.valueOf(speedJ));
        props.setProperty("speedL", String.valueOf(speedL));
        props.setProperty("accJ", String.valueOf(accJ));
        props.setProperty("accL", String.valueOf(accL));
        props.setProperty("liftPick", String.valueOf(liftPick));
        props.setProperty("liftPlace", String.valueOf(liftPlace));
        props.setProperty("delayPick", df.format(delayPick));
        props.setProperty("delayPlace", df.format(delayPlace));
        props.setProperty("pumpPort", String.valueOf(pumpPort));
        props.setProperty("valvePort", String.valueOf(valvePort));
        props.setProperty("pickCS", String.valueOf(pickCS));
        props.setProperty("placeCS", String.valueOf(placeCS));
        props.setProperty("toolCS", String.valueOf(toolCS));
        props.setProperty("placeZ", df.format(placeZ));

        workspace.acceptSettings(ipAddress, pickpointsPath, speedJ, speedL, accJ, accL, liftPick, liftPlace, delayPick,
                delayPlace, pumpPort, valvePort, pickCS, placeCS, toolCS, placeZ);

        try (OutputStream output = new FileOutputStream("config.properties")) {
            props.store(output, null);
            System.out.println("Settings saved");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

}