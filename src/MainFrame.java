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
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class MainFrame implements ActionListener, ComponentListener, MouseWheelListener, MouseMotionListener, MouseListener, KeyListener {
    //canvas
    NubotCanvas simNubotCanvas;

    //Driver
    Driver driver;


    Timer timer;
    final JFrame mainFrame = new JFrame("Nubot Simulator");
    final JFrame aboutF = new JFrame("A.S.A.R.G");

    //Config and rules
    Configuration map = Configuration.getInstance();
    Monomer[] mapCopy;

    //Graphics
    Monomer posLockMon;

    //Menus
    private JMenu file = new JMenu("File");
    private JMenu simulation = new JMenu("Simulation");
    private JMenu settings = new JMenu("Settings");
    private JMenu help = new JMenu("Help");
    private JMenu agitationMenu = new JMenu("Agitation");
    private JMenu speedMenu = new JMenu("Speed");
    // create sub-menus for each menu
    private ButtonGroup bondGroup = new ButtonGroup();
    private ButtonGroup drawMode = new ButtonGroup();

    private JCheckBoxMenuItem editToggle = new JCheckBoxMenuItem("Edit Mode");
    private JRadioButton editBrush = new JRadioButton("Brush");
    private JRadioButton editEraser = new JRadioButton("Eraser");
    private JRadioButton editState = new JRadioButton("State");
    private JRadioButton single = new JRadioButton("Single");
    private JRadioButton rigid = new JRadioButton("Rigid");
    private JRadioButton flexible = new JRadioButton("Flexible");
    private JRadioButton noBond = new JRadioButton("None");

    private JMenuItem loadR = new JMenuItem("Load Rules");
    private JMenuItem exportC = new JMenuItem("Export Configuration");
    private JMenuItem about = new JMenuItem("About");
    private JMenuItem usagesHelp = new JMenuItem("Configurer Usage");
    private JMenuItem loadC = new JMenuItem("Load Configuration");
    private JMenuItem menuReload = new JMenuItem("Reload");
    private JMenuItem menuClear = new JMenuItem("Clear");
    private JMenuItem menuQuit = new JMenuItem("Quit");
    private JMenuItem simStart = new JMenuItem("Start");
    private JMenuItem simStop = new JMenuItem("Stop");
    private JMenuItem simPause = new JMenuItem("Pause");
    private JMenuItem record = new JMenuItem("Record");
    private JMenuItem ruleMk = new JMenuItem("Rule Creator");
    private JCheckBoxMenuItem agitationToggle = new JCheckBoxMenuItem("On");
    private JMenuItem agitationSetRate = new JMenuItem("Set Rate");

    private JPopupMenu editMonMenu = new JPopupMenu();

    // edit tool bar
    JMenuBar editToolBar = new JMenuBar();

    //record dialog

    JDialog recordDialog;
    ActionListener recordDialogListener;


    //Status bar

    JPanel statusBar = new JPanel();
    JLabel statusSimulation = new JLabel();
    JLabel statusRules = new JLabel();
    JLabel statusConfig = new JLabel();
    JLabel statusAgitation = new JLabel();
    JLabel statusSpeed = new JLabel();
    JLabel statusMonomerNumber = new JLabel();
    JLabel statusTime = new JLabel();
    JLabel statusStep = new JLabel();
    //Data
    String rulesFileName = "";
    String configFileName = "";

    //change to default starting value later
    double speedRate = 1;
    int speedMax = 10;
    Double totalTime = 0.0;

    //Threads


    //For panning
    Point lastXY;
    Point dragCnt = new Point(0, 0);


    //Monomer editing

    Monomer lastMon = null;


    //Video

    private int fps = 60;
    private int recordingLength = 0;


    //configurator modes/values

    boolean editMode = false;
    boolean brushMode = false;
    boolean singleMode = true;
    boolean flexibleMode = false;
    boolean rigidMode = false;
    boolean noBondMode = true;
    boolean statePaint = false;
    boolean eraser = false;

    String stateVal = "A";

    public MainFrame(Dimension size, Driver driver1) {

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

        initMenuBar(driver1);
        //post creation menubar setup
        simStart.setEnabled(false);
        simPause.setEnabled(false);
        simStop.setEnabled(false);
        record.setEnabled(false);

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
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBar.setPreferredSize(new Dimension(mainFrame.getWidth(), 25));
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
        mainFrame.add(statusBar, BorderLayout.SOUTH);
        statusSimulation.setText("Waiting on Files ");
        statusRules.setText("No Rules ");
        statusConfig.setText("No config ");
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
        statusBar.add(statusRules);
        statusBar.add(statusSeparator2);
        statusBar.add(statusConfig);
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

        //******************

        //////
        ///Threads & Timer
        /////
        timer = new Timer(1000 / 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //  canvas.repaint();
            }
        });
        timer.setRepeats(true);


    }


    private void initMenuBar(Driver disp) {
        JMenuBar menuBar = new JMenuBar();

        loadR.addActionListener(this);
        about.addActionListener(this);
        loadC.addActionListener(this);
        exportC.addActionListener(this);
        menuReload.addActionListener(this);
        menuClear.addActionListener(this);
        menuQuit.addActionListener(this);
        simPause.addActionListener(this);
        simStart.addActionListener(this);
        simStop.addActionListener(this);
        record.addActionListener(this);
        agitationSetRate.addActionListener(this);
        agitationToggle.addActionListener(this);
        editToggle.addActionListener(this);
        editToolBar.setVisible(false);
        editToolBar.setFocusable(false);

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
        usagesHelp.addActionListener(this);
        // file.add(ruleMk);
        file.add(loadR);
        file.add(loadC);
        file.add(exportC);
        file.add(new JSeparator(SwingConstants.HORIZONTAL));
        //  file.add(menuReload);
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
        bondGroup = new ButtonGroup();
        drawMode = new ButtonGroup();
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
        editBrush.addActionListener(this);
        editState.addActionListener(this);
        editEraser.addActionListener(this);
        single.addActionListener(this);
        rigid.addActionListener(this);
        flexible.addActionListener(this);
        noBond.addActionListener(this);

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
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
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


        ActionListener edit = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == removeMonomerMI) {


                    map.removeMonomer(lastMon);


                    renderNubot(map.values());

                } else if (e.getSource() == changeStateMI) {
                    String state = JOptionPane.showInputDialog("New State:");
                    if (!state.isEmpty())
                        lastMon.setState(state);
                    renderNubot(map.values());
                }


            }
        };
        editMonMenu.add(changeStateMI);
        editMonMenu.add(removeMonomerMI);

        removeMonomerMI.addActionListener(edit);
        changeStateMI.addActionListener(edit);


    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == rigid) {
            flexibleMode = false;
            rigidMode = true;
            noBondMode = false;

        } else if (e.getSource() == flexible) {
            rigidMode = false;
            flexibleMode = true;
            noBondMode = false;

        } else if (e.getSource() == noBond) {
            rigidMode = false;
            flexibleMode = false;
            noBondMode = true;
        } else if (e.getSource() == editBrush) {
            System.out.println("SDFDSF");
            brushMode = true;
            singleMode = false;
            statePaint = false;
            eraser = false;

        } else if (e.getSource() == editEraser) {
            brushMode = false;
            singleMode = false;
            statePaint = false;
            eraser = true;

        } else if (e.getSource() == editState) {
            brushMode = false;
            singleMode = false;
            statePaint = true;
            eraser = false;

        } else if (e.getSource() == single) {
            brushMode = true;
            singleMode = true;
            statePaint = false;
            eraser = false;

        } else if (e.getSource() == usagesHelp) {
            String commands = "Ctrl + Drag : Monomer/State\nShift + Drag : Bonds\nCtrl + 1 : Brush\nCtrl + 2 : State Mode\nCtrl + 3 : Single\nCtrl + 4: Eraser\nShift + 1 : Rigid\nShift + 2 : Flexible\nShift + 3 : No Bond\nCtrl + Shift + 1 : Set State Value";
            JOptionPane.showMessageDialog(simNubotCanvas, commands);
        } else if (e.getSource() == exportC) {
            int mapSize = map.size();


            if (mapSize > 0) {
                String saveFN = JOptionPane.showInputDialog("File Name", ".conf");
                System.out.println("sdfs");
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

                }
            } else JOptionPane.showMessageDialog(mainFrame, "No monomers in current configuration.");


        } else if (e.getSource() == loadR) {
            map.timeElapsed = 0;
            map.rules.clear();
            try {
                final JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File("."));

                jfc.setDialogTitle("Select Rules File");
                // Creating a file filter for .conf
                jfc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory())
                            return true;
                        String fname = f.getName();
                        if (fname.length() > 6 && fname.substring(fname.length() - 6, fname.length()).matches(".rules"))
                            return true;

                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return "Nubot Rules File - .rules";
                        //return null;
                    }
                });

                int resVal = jfc.showOpenDialog(mainFrame);

                // if the ret flag results as Approve, we parse the file
                if (resVal == JFileChooser.APPROVE_OPTION) {
                    statusRules.setText("Loading rules ");
                    File theFile = jfc.getSelectedFile();
                    //if the selected file is of the right extension
                    if (theFile.length() > 5 && theFile.getName().substring(theFile.getName().length() - 6, theFile.getName().length()).matches(".rules")) {
                        System.out.println("SD");
                        map.rules.clear();
                        rulesFileName = theFile.getName();
                        FileReader fre = new FileReader(theFile);
                        BufferedReader bre = new BufferedReader(fre);
                        boolean cont = true;

                        while (cont) {
                            String line = bre.readLine();

                            if (line == null)
                                cont = false;

                            //if it's not a comment line and not empty, we parse
                            if (line != null && !line.contains("[") && !line.isEmpty() && line != "") {
                                String[] splitted = line.split(" ");
                                map.rules.addRule(new Rule(splitted[0], splitted[1], (byte) Integer.parseInt(splitted[2]), Direction.stringToFlag(splitted[3]), splitted[4], splitted[5], (byte) Integer.parseInt(splitted[6]), Direction.stringToFlag(splitted[7])));
                            }
                        }

                        bre.close();

                        map.simulation.rulesLoaded = true;
                        if (map.simulation.debugMode)
                            System.out.println("We have " + map.rules.size() + " rules");

                        statusRules.setText("Rules loaded ");

                        if (map.simulation.rulesLoaded && map.simulation.configLoaded) {

                            Random rand = new Random(System.currentTimeMillis());
                            posLockMon = (Monomer) map.values().toArray()[rand.nextInt(map.size())];
                            System.out.println(rand.nextInt());
                            simStart.setEnabled(true);
                            record.setEnabled(true);
                            statusSimulation.setText("Ready to Start ");
                        }
                    }

                    System.out.println(map.rules.values());
                }
            } catch (Exception exc) {

            }


            System.out.println("Load Rules");
        } else if (e.getSource() == loadC) {
            map.timeElapsed = 0;
            statusTime.setText("Time: " + 0.0);
            map.clear();
            try {
                final JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File("."));

                jfc.setDialogTitle("Select Configuration File");
                // Creating a file filter for .conf
                jfc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory())
                            return true;
                        String fname = f.getName();
                        if (fname.length() > 5 && fname.substring(fname.length() - 5, fname.length()).matches(".conf"))
                            return true;

                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return "Nubot Configuration File - .conf";
                        //return null;
                    }
                });

                int resVal = jfc.showOpenDialog(mainFrame);

                // if the ret flag results as Approve, we parse the file
                if (resVal == JFileChooser.APPROVE_OPTION) {
                    File theFile = jfc.getSelectedFile();
                    //if the selected file is of the right extension
                    if (theFile.length() > 5 && theFile.getName().substring(theFile.getName().length() - 5, theFile.getName().length()).matches(".conf")) {
                        configFileName = theFile.getName();
                        map.clear();
                        boolean inBonds = false;
                        FileReader fre = new FileReader(theFile);
                        BufferedReader bre = new BufferedReader(fre);
                        boolean cont = true;

                        while (cont) {
                            String line = bre.readLine();
                            if (line == null)
                                cont = false;
                            //if it's not a comment line and not empty, we parse
                            if (line != null && !line.contains("[") && !line.isEmpty() && !(line == "")) {
                                if (!inBonds) {
                                    if (line.contains("States:")) {

                                    } else if (line.contains("Bonds:")) {
                                        inBonds = true;
                                    } else {
                                        String[] splitted = line.split(" ");
                                        map.addMonomer(new Monomer(new Point(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1])), splitted[2]));
                                    }
                                } else if (inBonds) {
                                    String[] splitted = line.split(" ");
                                    // map.adjustBond(,);
                                    Point monomerPoint1 = new Point(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]));
                                    Point monomerPoint2 = new Point(Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
                                    byte bondType = (byte) Integer.parseInt(splitted[4]);
                                    if (map.containsKey(monomerPoint1) && map.containsKey(monomerPoint2) && Direction.dirFromPoints(monomerPoint1, monomerPoint2) > 0) {
                                        map.get(monomerPoint1).adjustBond(Direction.dirFromPoints(monomerPoint1, monomerPoint2), bondType);
                                        map.get(monomerPoint2).adjustBond(Direction.dirFromPoints(monomerPoint2, monomerPoint1), bondType);
                                    }
                                } else if (map.simulation.debugMode)
                                    System.out.println("We don't have more sections.");
                            }
                        }

                        bre.close();
                        map.simulation.configLoaded = true;
                        driver.createMapCC();
                        setStatus(null, null, "Config loaded ", "Monomers: " + map.size(), null, null);
                        renderNubot(map.values());
                        if (map.simulation.configLoaded && (map.simulation.rulesLoaded || map.simulation.agitationON)) {
                            Random rand = new Random();
                            posLockMon = (Monomer) map.values().toArray()[rand.nextInt(map.size())];
                            simStart.setEnabled(true);
                            record.setEnabled(true);
                            setStatus("Ready to Start", null, null, null, null, null);
                            map.values().toArray(mapCopy);


                        }
                    }
                }
            } catch (Exception exc) {
                System.out.println(exc.getMessage());
            }
            System.out.println("Load config");
        } else if (e.getSource() == menuReload) {
            map.clear();
            map.resetVals();
            if (mapCopy != null && map.simulation.configLoaded) {
                for (Monomer m : mapCopy) {
                    map.addMonomer(m);
                }
            }
            renderNubot(map.values());
            setStatus(null, null, "Config loaded ", "Monomers: " + map.size(), null, null);
            System.out.println("Reload configuration");

        } else if (e.getSource() == menuClear) {


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


            ///// Statusbar Text
            statusSimulation.setText("Waiting on Files ");
            statusRules.setText("No Rules ");
            statusConfig.setText("No config ");
            statusAgitation.setText("Agitation off ");
            totalTime = 0.0;
            statusMonomerNumber.setText("Monomers: 0");

            driver.simStop();
            simStart.setEnabled(false);
            simPause.setEnabled(false);
            simStop.setEnabled(false);
            loadC.setEnabled(true);
            loadR.setEnabled(true);
            renderNubot(map.values());
            System.out.println("clear ");
        } else if (e.getSource() == menuQuit) {
            System.out.println("quit application");
            System.exit(0);
        } else if (e.getSource() == about) {
            System.out.println("about this application");
            aboutF.setVisible(true);
        } else if (e.getSource() == simStart) {

            map.simulation.animate = true;
            map.simulation.isRunning = true;
            map.isFinished = false;
            map.simulation.isPaused = false;
            loadC.setEnabled(false);
            loadR.setEnabled(false);
            simPause.setEnabled(true);
            simStop.setEnabled(true);
            driver.simStart();
            // timer.start();

            System.out.println("start");
        } else if (e.getSource() == simStop) {
            map.simulation.isRunning = false;
            //timer.stop();
            map.timeElapsed = 0;
            driver.simStop();
            System.out.println("stop");
        } else if (e.getSource() == simPause) {

            map.simulation.isRunning = false;
            map.simulation.isPaused = true;

            System.out.println("pause");
        } else if (e.getSource() == agitationToggle) {
            if (map.simulation.agitationRate == 0.0) {
                JOptionPane.showMessageDialog(mainFrame, "Please set the agitation rate", "Error", JOptionPane.ERROR_MESSAGE);
                agitationToggle.setState(false);
            } else {
                map.simulation.agitationON = agitationToggle.getState();
                if (map.simulation.agitationON == true)
                    statusAgitation.setText("Agitation On: " + map.simulation.agitationRate);
                else
                    statusAgitation.setText("Agitation Off ");
                System.out.println("Agitation is: " + map.simulation.agitationON + ' ' + map.simulation.agitationRate);
            }
        } else if (e.getSource() == agitationSetRate) {
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
        } else if (e.getSource() == record) {


            recordDialog.show();

        } else if (e.getSource() == editToggle) {
            System.out.println("edit Toggle");
            editMode = true;
            editToolBar.setVisible(editToggle.getState());
        }


    }


    public void createRecordDialog() {
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

        recordDialogListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == multCB) {
                    if (multCB.isSelected()) {
                        multField.setEnabled(true);
                    } else multField.setEnabled(false);
                }
                if (e.getSource() == toEndCB) {
                    if (toEndCB.isSelected()) {
                        lengthField.setEnabled(false);
                    } else lengthField.setEnabled(true);
                }
                if (e.getSource() == startButton) {

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


                }
                if (e.getSource() == cancelButton) {
                    recordDialog.hide();
                }
            }
        };
        multCB.addActionListener(recordDialogListener);
        toEndCB.addActionListener(recordDialogListener);
        startButton.addActionListener(recordDialogListener);
        cancelButton.addActionListener(recordDialogListener);


        recordDialog.add(videoNameBox);
        recordDialog.add(lengthBox);
        recordDialog.add(ratioBox);
        recordDialog.add(multBox);
        recordDialog.add(choiceBox);
        recordDialog.pack();


    }


    public void renderNubot(Collection<Monomer> mapM) {

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

    public void setStatus(String simStatus, String rules, String config, String numMonomers, String time, String step) {

        if (simStatus != null && !simStatus.isEmpty())
            statusSimulation.setText(simStatus);
        if (rules != null && !rules.isEmpty())
            statusRules.setText(rules);
        if (config != null && !config.isEmpty())
            statusConfig.setText(config);
        if (numMonomers != null && !numMonomers.isEmpty())
            statusMonomerNumber.setText(numMonomers);
        if (time != null && !time.isEmpty())
            statusTime.setText(time);
        if (step != null && !step.isEmpty())
            statusStep.setText(step);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


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
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    @Override
    public void keyTyped(KeyEvent e) {

    }


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
    public void componentResized(ComponentEvent e) {

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}