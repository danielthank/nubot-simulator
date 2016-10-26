// Display.java
// Nubot Simulator
//
// Created by David Chavez on 4/1/14.
// Copyright (c) 2014 Algorithmic Self-Assembly Research Group. All rights reserved.
//

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

class MainFrame implements ComponentListener, MouseWheelListener, MouseMotionListener, MouseListener, KeyListener {
    private final JFrame mainFrame = new JFrame("Nubot Simulator");
    private final JFrame aboutF = new JFrame("A.S.A.R.G");

    //canvas
    private NubotCanvas simNubotCanvas;

    //Driver
    private Driver driver;

    //Config and rules
    private Configuration map = Configuration.getInstance();
    private Configuration mapCopy;

    //Graphics
    private Monomer posLockMon;

    // edit tool bar
    private JMenuBar editToolBar = new JMenuBar();
    private JDialog recordDialog;
    private JLabel statusSimulation = new JLabel();
    private JLabel statusAgitation = new JLabel();
    private JLabel statusSpeed = new JLabel();
    private JLabel statusMonomerNumber = new JLabel();
    private JLabel statusTime = new JLabel();
    private JLabel statusStep = new JLabel();

    //Data
    //change to default starting value later
    private double speedRate = 1;
    private int speedMax = 10;
    private Double totalTime = 0.0;

    //For panning
    private Point lastXY;
    private Point dragCnt = new Point(0, 0);
    private Monomer lastMon = null;
    private boolean editMode = false;
    private boolean brushMode = false;
    private boolean singleMode = true;
    private boolean flexibleMode = false;
    private boolean rigidMode = false;
    private boolean noBondMode = true;
    private boolean statePaint = false;
    private boolean eraser = false;
    private String stateVal = "A";

    //Menus
    private JMenu file = new JMenu("File");
    private JMenu simulation = new JMenu("Simulation");
    private JMenu settings = new JMenu("Settings");
    private JMenu help = new JMenu("Help");

    //record dialog
    private JMenu agitationMenu = new JMenu("Agitation");
    private JMenu speedMenu = new JMenu("Speed");


    //Status bar
    // create sub-menus for each menu
    private JCheckBoxMenuItem editToggle = new JCheckBoxMenuItem("Edit Mode");
    private JRadioButton editBrush = new JRadioButton("Brush");
    private JRadioButton editState = new JRadioButton("State");
    private JRadioButton single = new JRadioButton("Single");
    private JRadioButton editEraser = new JRadioButton("Eraser");

    private JRadioButton rigid = new JRadioButton("Rigid");
    private JRadioButton flexible = new JRadioButton("Flexible");
    private JRadioButton noBond = new JRadioButton("None");

    private JMenuItem loadR = new JMenuItem("Load Rules");
    private JMenuItem exportC = new JMenuItem("Export Configuration");
    private JMenuItem menuReload = new JMenuItem("Reload");
    private JMenuItem menuClear = new JMenuItem("Clear");
    private JMenuItem menuQuit = new JMenuItem("Quit");

    //configurator modes/values
    private JMenuItem simStart = new JMenuItem("Start");
    private JMenuItem record = new JMenuItem("Record");
    private JMenuItem simPause = new JMenuItem("Pause");
    private JMenuItem simStop = new JMenuItem("Stop");
    private JMenuItem ruleMk = new JMenuItem("Rule Creator");

    private JCheckBoxMenuItem agitationToggle = new JCheckBoxMenuItem("On");
    private JMenuItem agitationSetRate = new JMenuItem("Set Rate");

    private JPopupMenu editMonMenu = new JPopupMenu();

    private JMenuItem usagesHelp = new JMenuItem("Configurer Usage");
    private JMenuItem about = new JMenuItem("About");

    private int fps = 60;
    private int recordingLength = 0;

    MainFrame(Dimension size, Driver driver1) {
        //driver
        driver = driver1;

        //record dialog
        createRecordDialog();

        mainFrame.setBackground(Color.WHITE);
        mainFrame.getContentPane().setBackground(Color.WHITE);
        mainFrame.setSize(size.width, size.height);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(new BorderLayout());

        map.simulation.canvasXYoffset = new Point(size.width / 2, -1 * (size.height / 2 - 60));

        initMenuBar();

        //post creation menubar setup
        simStart.setEnabled(false);
        record.setEnabled(false);
        simPause.setEnabled(false);
        simStop.setEnabled(false);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        simNubotCanvas = new NubotCanvas(mainFrame.getSize());
        NubotCanvas.setSimInstance(simNubotCanvas);
        mainFrame.add(simNubotCanvas);

        mainFrame.setVisible(true);
        mainFrame.addComponentListener(this);
        mainFrame.addKeyListener(this);
        simNubotCanvas.addMouseWheelListener(this);
        simNubotCanvas.addMouseMotionListener(this);
        simNubotCanvas.addMouseListener(this);


        ////
        //Status Bar  setup
        ////
        JPanel statusBar = new JPanel();
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBar.setPreferredSize(new Dimension(mainFrame.getWidth(), 25));
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
        mainFrame.add(statusBar, BorderLayout.SOUTH);
        statusSimulation.setText("Waiting on Files ");
        statusAgitation.setText("Agitation off ");
        statusSpeed.setText("Speed: " + speedRate);
        statusTime.setText("Time: " + map.timeElapsed);
        statusMonomerNumber.setText("Monomers: " + map.getSize());
        statusStep.setText("");


        JSeparator statusSeparator1 = new JSeparator(SwingConstants.VERTICAL);
        statusSeparator1.setMaximumSize(new Dimension(5, 25));
        JSeparator statusSeparator2 = new JSeparator(SwingConstants.VERTICAL);
        statusSeparator2.setMaximumSize(new Dimension(5, 25));
        JSeparator statusSeparator3 = new JSeparator(SwingConstants.VERTICAL);
        statusSeparator3.setMaximumSize(new Dimension(5, 25));
        JSeparator statusSeparator4 = new JSeparator(SwingConstants.VERTICAL);
        statusSeparator4.setMaximumSize(new Dimension(5, 25));
        JSeparator statusSeparator5 = new JSeparator(SwingConstants.VERTICAL);
        statusSeparator5.setMaximumSize(new Dimension(5, 25));
        JSeparator statusSeparator6 = new JSeparator(SwingConstants.VERTICAL);
        statusSeparator6.setMaximumSize(new Dimension(5, 25));
        JSeparator statusSeparator7 = new JSeparator(SwingConstants.VERTICAL);
        statusSeparator7.setMaximumSize(new Dimension(5, 25));


        statusBar.add(statusSimulation);
        statusBar.add(statusSeparator1);
        statusBar.add(statusSeparator2);
        statusBar.add(statusSeparator3);
        statusBar.add(statusAgitation);
        statusBar.add(statusSeparator4);
        statusBar.add(statusSpeed);
        statusBar.add(statusSeparator5);

        statusBar.add(statusMonomerNumber);
        statusBar.add(statusSeparator6);
        statusBar.add(statusTime);
        statusBar.add(statusSeparator7);
        statusBar.add(statusStep);

        Timer timer = new Timer(1000 / 60, (ActionEvent e) -> {
            //  canvas.repaint();
        });
        timer.setRepeats(true);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        loadR.addActionListener((ActionEvent e) -> {
            map.timeElapsed = 0;
            map.rules.clear();
            try {
                final JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File("./conf"));
                jfc.setDialogTitle("Select Rules File");
                jfc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory())
                            return true;
                        String fname = f.getName();
                        return fname.length() > 6 &&
                                fname.substring(fname.length() - 6, fname.length()).matches(".rules");
                    }
                    @Override
                    public String getDescription() {
                        return "Nubot Rules File - .rules";
                    }
                });
                int resVal = jfc.showOpenDialog(mainFrame);
                // if the ret flag results as Approve, we parse the file
                if (resVal == JFileChooser.APPROVE_OPTION) {
                    File theFile = jfc.getSelectedFile();
                    map.loadFile(theFile);

                    map.simulation.configLoaded = true;
                    map.simulation.rulesLoaded = true;

                    driver.createMapCC();
                    mapCopy = map.getCopy();

                    renderNubot(map.values());

                    Random rand = new Random();
                    posLockMon = (Monomer) map.values().toArray()[rand.nextInt(map.size())];

                    simStart.setEnabled(true);
                    record.setEnabled(true);

                    setStatus("Ready to Start", "Monomers: " + map.size(), null, null);
                }
            } catch (Exception exc) {
                System.out.println(exc.getMessage());
            }
            System.out.println("Load Rules");
        });
        exportC.addActionListener((ActionEvent e) -> {
            int mapSize = map.size();
            if (mapSize > 0) {
                String saveFN = JOptionPane.showInputDialog("File Name", ".conf");
                File file = new File(saveFN);
                try {
                    BufferedWriter bfW = new BufferedWriter(new FileWriter(file));
                    bfW.write("States:");
                    bfW.newLine();

                    Set<Map.Entry<Point, Monomer>> setPM = map.entrySet();

                    for (Map.Entry<Point, Monomer> set : setPM) {
                        bfW.write((int) set.getKey().getX() + " " + (int) set.getKey().getY() + " " + set.getValue().getState());
                        bfW.newLine();
                    }
                    bfW.newLine();
                    bfW.write("Bonds:");
                    bfW.newLine();
                    for (Map.Entry<Point, Monomer> set : setPM) {
                        ArrayList<Byte> rigidList = set.getValue().getDirsByBondType(Bond.TYPE_RIGID);
                        ArrayList<Byte> flexList = set.getValue().getDirsByBondType(Bond.TYPE_FLEXIBLE);
                        Point startPoint = set.getKey();
                        for (Byte d : rigidList) {
                            Point neighborPoint = Direction.getNeighborPosition(set.getKey(), d);
                            bfW.write(startPoint.x + " " + startPoint.y + " " + neighborPoint.x + " " + neighborPoint.y + " " + 1);
                            bfW.newLine();

                        }
                        for (Byte d : flexList) {
                            Point neighborPoint = Direction.getNeighborPosition(set.getKey(), d);
                            bfW.write(startPoint.x + " " + startPoint.y + " " + neighborPoint.x + " " + neighborPoint.y + " " + 2);
                            bfW.newLine();
                        }
                    }
                    bfW.close();
                } catch (IOException exc) {
                    System.out.println(exc.getMessage());
                }
            } else JOptionPane.showMessageDialog(mainFrame, "No monomers in current configuration.");
        });
        menuReload.addActionListener((ActionEvent e) -> {
            map.clear();
            map.timeElapsed = 0;
            map.markovStep = 0;
            for (Monomer m : mapCopy.values()) map.addMonomer(m);
            driver.createMapCC();
            mapCopy = map.getCopy();
            renderNubot(map.values());
            simStart.setEnabled(true);
            record.setEnabled(true);
            setStatus(null, "Monomers: " + map.size(), "Time: " + map.timeElapsed, "Step: " + map.markovStep);
            System.out.println("Reload configuration");
        });
        menuClear.addActionListener((ActionEvent e) -> {
            map.clear();
            map.rules.clear();
            map.timeElapsed = 0;
            statusTime.setText("Time: " + map.timeElapsed);
            /////Simulation Flags
            map.simulation.configLoaded = false;
            map.simulation.rulesLoaded = false;
            map.simulation.isRunning = false;
            map.simulation.isRecording = false;
            map.simulation.agitationON = false;

            //Simulation values
            simNubotCanvas.setOffset(simNubotCanvas.getWidth() / 2, -simNubotCanvas.getHeight() / 2);

            // Statusbar Text
            statusSimulation.setText("Waiting on Files ");
            statusAgitation.setText("Agitation off ");
            totalTime = 0.0;
            statusMonomerNumber.setText("Monomers: 0");

            driver.simStop();
            simStart.setEnabled(false);
            simPause.setEnabled(false);
            simStop.setEnabled(false);
            loadR.setEnabled(true);
            renderNubot(map.values());
            System.out.println("clear ");
        });
        menuQuit.addActionListener((ActionEvent e) -> {
            System.out.println("quit application");
            System.exit(0);
        });
        simStart.addActionListener((ActionEvent e) -> {
            map.simulation.animate = true;
            map.simulation.isRunning = true;
            map.isFinished = false;
            map.simulation.isPaused = false;
            loadR.setEnabled(false);
            simStart.setEnabled(false);
            simPause.setEnabled(true);
            simStop.setEnabled(true);
            driver.simStart();
            // timer.start();
            System.out.println("start");
        });
        record.addActionListener((ActionEvent e) -> {
            recordDialog.show();
        });
        simPause.addActionListener((ActionEvent e) -> {
            map.simulation.isRunning = false;
            map.simulation.isPaused = true;
            simStart.setEnabled(true);
            simPause.setEnabled(false);

            System.out.println("pause");
        });
        simStop.addActionListener((ActionEvent e) -> {
            map.simulation.isRunning = false;
            //timer.stop();
            map.timeElapsed = 0;
            driver.simStop();
            loadR.setEnabled(true);
            System.out.println("stop");
        });
        agitationSetRate.addActionListener((ActionEvent e) -> {
            String agitationRateString = JOptionPane.showInputDialog(mainFrame, "Set Agitation Rate", "Agitation", JOptionPane.PLAIN_MESSAGE);
            if (agitationRateString != null) {
                map.simulation.agitationRate = Double.parseDouble(agitationRateString);
                map.simulation.agitationON = true;
                statusAgitation.setText("Agitation On: " + map.simulation.agitationRate);
                agitationToggle.setState(true);
                System.out.println("Agitation Rate changed and set to on");

                if (map.simulation.configLoaded && map.simulation.agitationON) {
                    simStart.setEnabled(true);
                    record.setEnabled(true);
                    statusSimulation.setText("Ready to Start");
                }
            }
        });
        agitationToggle.addActionListener((ActionEvent e) -> {
            if (map.simulation.agitationRate == 0.0) {
                JOptionPane.showMessageDialog(mainFrame, "Please set the agitation rate", "Error", JOptionPane.ERROR_MESSAGE);
                agitationToggle.setState(false);
            } else {
                map.simulation.agitationON = agitationToggle.getState();
                if (map.simulation.agitationON)
                    statusAgitation.setText("Agitation On: " + map.simulation.agitationRate);
                else
                    statusAgitation.setText("Agitation Off ");
                System.out.println("Agitation is: " + map.simulation.agitationON + ' ' + map.simulation.agitationRate);
            }
        });
        editToggle.addActionListener((ActionEvent e) -> {
            System.out.println("edit Toggle");
            editMode = true;
            editToolBar.setVisible(editToggle.getState());
        });
        editToolBar.setVisible(false);
        editToolBar.setFocusable(false);
        about.addActionListener((ActionEvent e) -> {
            System.out.println("about this application");
            aboutF.setVisible(true);
        });

        menuBar.add(file);
        menuBar.add(simulation);
        menuBar.add(settings);
        menuBar.add(help);
        menuBar.add(editToggle);
        menuBar.setFocusable(false);
        editToggle.setMaximumSize(new Dimension(100, 50));
        editToggle.setFocusable(false);
        menuBar.add(editToolBar);

        help.add(about);
        help.add(usagesHelp);
        usagesHelp.addActionListener((ActionEvent e) -> {
            String commands = "Ctrl + Drag : Monomer/State\nShift + Drag : Bonds\nCtrl + 1 : Brush\nCtrl + 2 : State Mode\nCtrl + 3 : Single\nCtrl + 4: Eraser\nShift + 1 : Rigid\nShift + 2 : Flexible\nShift + 3 : No Bond\nCtrl + Shift + 1 : Set State Value";
            JOptionPane.showMessageDialog(simNubotCanvas, commands);
        });
        // file.add(ruleMk);
        file.add(loadR);
        file.add(exportC);
        file.add(new JSeparator(SwingConstants.HORIZONTAL));
        file.add(menuReload);
        file.add(menuClear);
        file.add(menuQuit);
        simulation.add(simStart);
        simulation.add(record);
        simulation.add(simPause);
        simulation.add(new JSeparator(SwingConstants.HORIZONTAL));
        simulation.add(simStop);
        settings.add(agitationMenu);
        agitationMenu.add(agitationToggle);
        agitationMenu.add(agitationSetRate);
        settings.add(speedMenu);

        //Edit ToolBar
        ButtonGroup bondGroup = new ButtonGroup();
        ButtonGroup drawMode = new ButtonGroup();
        bondGroup.add(rigid);
        rigid.setFocusable(false);
        bondGroup.add(flexible);
        flexible.setFocusable(false);
        bondGroup.add(noBond);
        noBond.setFocusable(false);
        noBond.setSelected(true);
        drawMode.add(editBrush);
        editBrush.setFocusable(false);
        drawMode.add(editState);
        editState.setFocusable(false);
        drawMode.add(single);
        single.setFocusable(false);
        drawMode.add(editEraser);
        editEraser.setFocusable(false);
        single.setSelected(true);
        editBrush.setHorizontalAlignment(JMenuItem.CENTER);

        editToolBar.add(editBrush);
        editToolBar.add(editState);
        editToolBar.add(single);
        editToolBar.add(editEraser);
        editToolBar.add(new JLabel("|Bonds:"));
        editToolBar.add(rigid);
        editToolBar.add(flexible);
        editToolBar.add(noBond);

        editToolBar.setPreferredSize(new Dimension(20, 20));
        //editToolBar.setFloatable(false);
        editBrush.addActionListener((ActionEvent e) -> {
            brushMode = true;
            singleMode = false;
            statePaint = false;
            eraser = false;
        });
        editState.addActionListener((ActionEvent e) -> {
            brushMode = false;
            singleMode = false;
            statePaint = true;
            eraser = false;
        });
        editEraser.addActionListener((ActionEvent e) -> {
            brushMode = false;
            singleMode = false;
            statePaint = false;
            eraser = true;
        });
        single.addActionListener((ActionEvent e) -> {
            brushMode = true;
            singleMode = true;
            statePaint = false;
            eraser = false;
        });
        rigid.addActionListener((ActionEvent e) -> {
            flexibleMode = false;
            rigidMode = true;
            noBondMode = false;
        });
        flexible.addActionListener((ActionEvent e) -> {
            rigidMode = false;
            flexibleMode = true;
            noBondMode = false;
        });
        noBond.addActionListener((ActionEvent e) -> {
            rigidMode = false;
            flexibleMode = false;
            noBondMode = true;
        });

        mainFrame.setJMenuBar(menuBar);

        // speed Slider
        JSlider speedSlider = new JSlider(JSlider.VERTICAL, -speedMax, speedMax, 0);
        speedSlider.setMajorTickSpacing(20);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        Hashtable speedLabels = new Hashtable();
        speedLabels.put(-speedMax, new JLabel("Fast"));
        speedLabels.put(0, new JLabel("Normal"));
        speedLabels.put(speedMax, new JLabel("Slow"));
        speedSlider.setInverted(true);
        speedSlider.setLabelTable(speedLabels);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener((ChangeEvent changeEvent) -> {
            JSlider sliderSource = (JSlider) changeEvent.getSource();
            if (!sliderSource.getValueIsAdjusting()) {
                speedRate = (double) sliderSource.getValue();
                if (speedRate == 0)
                    speedRate = 1;
                else if (speedRate < 1) {
                    speedRate = (speedMax + speedRate + 1) / 10;

                }
                map.simulation.speedRate = speedRate;
                System.out.println("speed changed to: " + speedRate);
                statusSpeed.setText("Speed: " + speedRate);
            }
        });

        speedMenu.add(speedSlider);
        //about screen
        final JPanel aboutP = new JPanel();
        aboutF.setResizable(false);
        aboutP.setLayout(new BoxLayout(aboutP, BoxLayout.PAGE_AXIS));
        aboutF.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        JLabel aboutGroupName = new JLabel("Algorithmic Self-Assembly Research Group");
        JLabel aboutSchool = new JLabel("The University of Texas - Pan American");
        aboutGroupName.setFont(new Font("", Font.BOLD, 23));
        aboutSchool.setFont(new Font("", Font.BOLD, 20));
        aboutGroupName.setAlignmentX(Component.CENTER_ALIGNMENT);
        aboutSchool.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel groupLink = new JLabel("Visit Our Website");
        groupLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        groupLink.setForeground(Color.blue);

        aboutP.add(aboutGroupName);
        aboutP.add(Box.createRigidArea(new Dimension(0, 15)));
        aboutP.add(aboutSchool);
        aboutP.add(Box.createRigidArea(new Dimension(0, 30)));
        aboutP.add(groupLink);
        final URI websiteLink;
        MouseAdapter openURL = null;

        try {
            websiteLink = new URI("http://faculty.utpa.edu/orgs/asarg/");
            openURL = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(websiteLink);
                    } catch (IOException e1) {
                        System.out.println("error visiting the website URL");
                    }
                }
            };
        } catch (URISyntaxException e) {
            System.out.println("something is wrong with the URI or MouseAdapter");
        }
        groupLink.addMouseListener(openURL);

        aboutP.setBorder(new EmptyBorder(20, 20, 10, 20));
        aboutF.add(aboutP);
        aboutF.pack();
        aboutF.setVisible(false);

        //popup menu
        final JMenuItem removeMonomerMI = new JMenuItem("Remove");
        final JMenuItem changeStateMI = new JMenuItem("State");

        editMonMenu.add(changeStateMI);
        editMonMenu.add(removeMonomerMI);

        removeMonomerMI.addActionListener((ActionEvent e) -> {
            map.removeMonomer(lastMon);
            renderNubot(map.values());
        });

        changeStateMI.addActionListener((ActionEvent e) -> {
            String state = JOptionPane.showInputDialog("New State:");
            if (!state.isEmpty())
                lastMon.setState(state);
            renderNubot(map.values());
        });
    }

    private void createRecordDialog() {
        recordDialog = new JDialog();
        recordDialog.setTitle("Record");
        recordDialog.setLayout(new BoxLayout(recordDialog.getContentPane(), BoxLayout.Y_AXIS));
        recordDialog.setResizable(false);

        Box videoNameBox = new Box(BoxLayout.X_AXIS);
        videoNameBox.add(new JLabel("Name:"));
        final JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(70, 20));
        final JTextField lengthField = new JTextField();
        lengthField.setMaximumSize(new Dimension(40, 20));
        lengthField.setPreferredSize(new Dimension(40, 20));
        final JTextField ratioField = new JTextField();
        ratioField.setMaximumSize(new Dimension(20, 20));
        ratioField.setPreferredSize(new Dimension(20, 20));
        videoNameBox.add(nameField);
        Box lengthBox = new Box(BoxLayout.X_AXIS);
        final JCheckBox toEndCB = new JCheckBox("Finish");
        final JRadioButton realToNubotCB = new JRadioButton("R/N");
        JRadioButton nubotToRealCB = new JRadioButton("N/R");
        nubotToRealCB.setSelected(true);
        ButtonGroup ratioGroup = new ButtonGroup();
        ratioGroup.add(realToNubotCB);
        ratioGroup.add(nubotToRealCB);
        toEndCB.setFont(new Font("TimesRoman", Font.ITALIC | Font.BOLD, 13));
        lengthBox.add(new JLabel("Length:"));
        lengthBox.add(lengthField);
        lengthBox.add(toEndCB);
        final Box ratioBox = new Box(BoxLayout.X_AXIS);
        ratioBox.add(new JLabel("Ratio:"));
        ratioBox.add(ratioField);
        ratioBox.add(realToNubotCB);
        ratioBox.add(nubotToRealCB);
        Box multBox = new Box(BoxLayout.X_AXIS);
        final JCheckBox multCB = new JCheckBox("Multiple");
        final JTextField multField = new JTextField();
        multField.setEnabled(false);
        multField.setMaximumSize(new Dimension(25, 20));
        multField.setPreferredSize(new Dimension(25, 20));
        multBox.add(multCB);
        // multBox.add(new JLabel("Count:"));
        multBox.add(multField);
        Box choiceBox = new Box(BoxLayout.X_AXIS);
        final JButton startButton = new JButton("Start");
        final JButton cancelButton = new JButton("Cancel");
        choiceBox.add(cancelButton);
        choiceBox.add(startButton);

        multCB.addActionListener((ActionEvent e) -> {
            if (multCB.isSelected()) {
                multField.setEnabled(true);
            } else multField.setEnabled(false);
        });
        toEndCB.addActionListener((ActionEvent e) -> {
            if (toEndCB.isSelected()) {
                lengthField.setEnabled(false);
            } else lengthField.setEnabled(true);
        });
        startButton.addActionListener((ActionEvent e) -> {
            if (!nameField.getText().isEmpty()) {
                int recordingsCount = 1;
                boolean toEnd = false;
                boolean RtN = true;
                int recordingLength = 1;
                double ratio = 1.0;
                if (multCB.isSelected()) {
                    if (!multField.getText().isEmpty()) {
                        recordingsCount = Integer.parseInt(multField.getText()) > 0 ? Integer.parseInt(multField.getText()) : 1;
                    }

                }
                if (toEndCB.isSelected()) {
                    toEnd = true;
                } else {
                    recordingLength = Integer.parseInt(lengthField.getText()) > 0 ? Integer.parseInt(lengthField.getText()) : 1;
                }
                if (realToNubotCB.isSelected()) {
                    RtN = true;
                } else RtN = false;
                if (!ratioField.getText().isEmpty()) {
                    ratio = Double.parseDouble(ratioField.getText()) > 0 ? Double.parseDouble(ratioField.getText()) : 1;
                }
                driver.recordSim(nameField.getText(), recordingsCount, recordingLength, toEnd, ratio, RtN);
            }
        });
        cancelButton.addActionListener((ActionEvent e) -> {
            recordDialog.hide();
        });

        recordDialog.add(videoNameBox);
        recordDialog.add(lengthBox);
        recordDialog.add(ratioBox);
        recordDialog.add(multBox);
        recordDialog.add(choiceBox);
        recordDialog.pack();


    }

    void renderNubot(Collection<Monomer> mapM) {
        simNubotCanvas.renderNubot(mapM);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        int monRadius = simNubotCanvas.getMonomerRadius();
        if (e.getWheelRotation() == 1.0 && monRadius > 2) {
            int newRadius = (int) Math.round(monRadius * .92);
            simNubotCanvas.setMonomerRadius(newRadius);
            if (monRadius == newRadius && monRadius > 0) {
                simNubotCanvas.monomerRadiusDecrement();
            }


            //  if(!map.simulation.isRunning)
            renderNubot(map.values());
        } else if (e.getPreciseWheelRotation() == -1.0 && simNubotCanvas.getMonomerRadius() < simNubotCanvas.getWidth() / 10) {
            simNubotCanvas.setMonomerRadius((int) Math.ceil(monRadius * 1.08));
            ;
            //if(!map.simulation.isRunning)
            renderNubot(map.values());
        }


    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown() && !e.isControlDown()) {
            if (lastXY == null)
                lastXY = e.getPoint();
            simNubotCanvas.translateOffset(e.getX() - lastXY.x, -(e.getY() - lastXY.y));
            dragCnt.translate(e.getX() - lastXY.x, e.getY() - lastXY.y);

            lastXY = e.getPoint();
        }

        if (editMode) {
            if (e.isControlDown()) {
                Point cPoint = NubotDrawer.getCanvasToGridPosition(e.getPoint(), simNubotCanvas.getOffset(), simNubotCanvas.getMonomerRadius());

                if (brushMode) {

                    map.addMonomer(new Monomer(cPoint, stateVal));
                } else if (eraser) {

                    if (map.containsKey(cPoint)) {
                        map.removeMonomer(cPoint);
                        renderNubot(map.values());
                    }

                } else if (statePaint) {
                    if (map.containsKey(cPoint)) {
                        map.get(cPoint).setState(stateVal);
                    }
                }
            } else if (e.isShiftDown()) {
                Monomer tmp = null;
                Point gp = NubotDrawer.getCanvasToGridPosition(e.getPoint(), simNubotCanvas.getOffset(), simNubotCanvas.getMonomerRadius());
                if (map.containsKey(gp)) {
                    tmp = map.get(gp);


                }
                if (lastMon != null && tmp != null) {
                    if (flexibleMode) {
                        lastMon.adjustBond(Direction.dirFromPoints(lastMon.getLocation(), tmp.getLocation()), Bond.TYPE_FLEXIBLE);
                        tmp.adjustBond(Direction.dirFromPoints(tmp.getLocation(), lastMon.getLocation()), Bond.TYPE_FLEXIBLE);
                    } else if (rigidMode) {
                        lastMon.adjustBond(Direction.dirFromPoints(lastMon.getLocation(), tmp.getLocation()), Bond.TYPE_RIGID);
                        tmp.adjustBond(Direction.dirFromPoints(tmp.getLocation(), lastMon.getLocation()), Bond.TYPE_RIGID);
                    } else if (noBondMode) {
                        lastMon.adjustBond(Direction.dirFromPoints(lastMon.getLocation(), tmp.getLocation()), Bond.TYPE_NONE);
                        tmp.adjustBond(Direction.dirFromPoints(tmp.getLocation(), lastMon.getLocation()), Bond.TYPE_NONE);
                    }
                }
                lastMon = tmp;
            }
        }


        //if(!map.simulation.isRunning)
        renderNubot(map.values());
    }

    void setStatus(String simStatus, String numMonomers, String time, String step) {
        if (simStatus != null && !simStatus.isEmpty())
            statusSimulation.setText(simStatus);
        if (numMonomers != null && !numMonomers.isEmpty())
            statusMonomerNumber.setText(numMonomers);
        if (time != null && !time.isEmpty())
            statusTime.setText(time);
        if (step != null && !step.isEmpty())
            statusStep.setText(step);
    }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point gp = NubotDrawer.getCanvasToGridPosition(e.getPoint(), simNubotCanvas.getOffset(), simNubotCanvas.getMonomerRadius());
        if (map.containsKey(gp)) {
            Monomer tmp = map.get(NubotDrawer.getCanvasToGridPosition(e.getPoint(), simNubotCanvas.getOffset(), simNubotCanvas.getMonomerRadius()));
            lastMon = tmp;
            //  tmp.setState("awe");
            posLockMon = tmp;
        }
        if (e.isControlDown() && SwingUtilities.isRightMouseButton(e)) {


            if (!map.containsKey(gp)) {

                String state = JOptionPane.showInputDialog("State: ");
                if (!state.isEmpty())
                    map.addMonomer(new Monomer(gp, state));
            }
        } else if (map.containsKey(gp) && SwingUtilities.isRightMouseButton(e)) {
            editMonMenu.show(simNubotCanvas, e.getX(), e.getY());
        }

        renderNubot(map.values());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastXY = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

            if (!map.simulation.isPaused) {

                map.simulation.isRunning = false;
                map.simulation.isPaused = true;
            } else {

                driver.simStart();
                map.simulation.isRunning = true;
                map.simulation.isPaused = false;
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
        int keyCode = e.getKeyCode();
        if (e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {

            switch (keyCode) {
                case KeyEvent.VK_1:
                    simNubotCanvas.showToast(20, 40, "Brush", 1200);
                    brushMode = true;
                    singleMode = false;
                    eraser = false;
                    editBrush.setSelected(true);
                    break;
                case KeyEvent.VK_3:
                    simNubotCanvas.showToast(20, 40, "Single", 1200);
                    brushMode = false;
                    singleMode = true;
                    single.setSelected(true);
                    eraser = false;
                    break;
                case KeyEvent.VK_4:
                    simNubotCanvas.showToast(20, 40, "Eraser", 1200);
                    brushMode = false;
                    singleMode = false;
                    editEraser.setSelected(true);
                    eraser = true;
                    break;
                case KeyEvent.VK_2:
                    editState.setSelected(true);
                    brushMode = false;
                    eraser = false;
                    singleMode = false;
                    statePaint = true;
                    simNubotCanvas.showToast(20, 40, "State Paint", 1200);
                    break;

            }

        } else if (e.isShiftDown() && !e.isControlDown() && !e.isAltDown()) {
            switch (keyCode) {
                case KeyEvent.VK_1:
                    simNubotCanvas.showToast(20, 40, "Rigid", 1200);
                    flexibleMode = false;
                    rigid.setSelected(true);
                    rigidMode = true;
                    break;
                case KeyEvent.VK_2:
                    simNubotCanvas.showToast(20, 40, "Flexible", 1200);
                    rigidMode = false;
                    flexibleMode = true;
                    flexible.setSelected(true);
                    break;
                case KeyEvent.VK_3:
                    simNubotCanvas.showToast(20, 40, "No Bond", 1200);
                    rigidMode = false;
                    noBond.setSelected(true);
                    flexibleMode = false;
                    break;
            }

        } else if (e.isShiftDown() && e.isControlDown()) {
            switch (keyCode) {
                case KeyEvent.VK_1:
                    String state = JOptionPane.showInputDialog("Set paint state:");
                    if (state.length() > 0)
                        stateVal = state;
                    break;
            }
        }
    }

    @Override
    public void componentResized(ComponentEvent e) { }

    @Override
    public void componentMoved(ComponentEvent e) { }

    @Override
    public void componentShown(ComponentEvent e) { }

    @Override
    public void componentHidden(ComponentEvent e) { }
}