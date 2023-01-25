package main.java.com.polimi.client.views.gui;

import com.google.gson.internal.LinkedTreeMap;
import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.models.ClientModel;
import main.java.com.polimi.client.models.Message;
import main.java.com.polimi.client.utils.Utils;
import main.java.com.polimi.client.views.ViewInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class ClientGUIView extends Observable implements Observer, ViewInterface {
    private final String path;
    private JPanel MainPanel;
    private JPanel joinGamePanel;
    private BackgroundPanel gamesPanel;
    private BackgroundPanel setNicknamePanel;
    private JLabel mainTitle;
    private JLabel setNicknameErrorLabel;
    private JLabel availableGamesLabel;
    private JButton setNicknameButton;
    private JButton createGameButton;
    private JButton refreshTableButton;
    private JButton joinGameButton;
    private JTextField setNicknameTextField;
    private JTextField joinGameTextField;
    private JTable gamesTable;
    Integer gameId=-1;
    Integer playerId;
    Packet packet;
    Boolean inAGame = false;
    Integer tempPlayerId;
    private ClientController clientController;
    PlayerGuiView playerView;
    JFrame frame;
    private BackgroundPanel serverDownPanel;

    /**
     * @param path path of graphical assests
     */
    public ClientGUIView(String path){
        this.path=path;
        MainPanel=new JPanel(new CardLayout());

        Image skyAndClouds = Utils.getImageFromPath(this.path, "Background1.jpg");
        setNicknamePanel= new BackgroundPanel(skyAndClouds);

        mainTitle = new EriantysTitleLabel("ERIANTYS");
        mainTitle.setFont(new Font("Serif", Font.PLAIN, 150));
        mainTitle.setPreferredSize(new Dimension(300, 30));
        mainTitle.setMinimumSize(new Dimension(300, 30));

        setNicknameTextField=new JTextField();
        setNicknameTextField.setFont(new Font("Serif", Font.PLAIN, 15));
        setNicknameTextField.setPreferredSize(new Dimension(400, 30));
        setNicknameTextField.setMaximumSize(new Dimension(400, 30));

        setNicknameErrorLabel = new JLabel();
        setNicknameErrorLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        setNicknameErrorLabel.setForeground(Color.RED);
        setNicknameErrorLabel.setVisible(false);

        setNicknameButton=new JButton(" Set Nickname ");
        setNicknameButton.setFocusable(false);
        setNicknameButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        setNicknameButton.setFont(new Font("Serif", Font.PLAIN, 15));

        setNicknameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = setNicknameTextField.getText();
                if(!Objects.equals(text, "")) {
                    if((text.charAt(0) >= '0' && text.charAt(0) <= '9')) {
                        setNicknameErrorLabel.setText("ERROR! Nickname can't start with a number");
                        setNicknameErrorLabel.setVisible(true);
                        setNicknameButton.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.RED, 3),
                                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
                    } else {
                        setNicknameErrorLabel.setVisible(false);
                        setNicknameButton.setBorder(null);

                        packet = new Packet("SET_NICKNAME", playerId);
                        packet.addToPayload("nickname", setNicknameTextField.getText());
                        setChanged();
                        notifyObservers(packet);
                    }
                } else {
                    setNicknameErrorLabel.setText("ERROR! Nickname is required");
                    setNicknameErrorLabel.setVisible(true);
                    setNicknameButton.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.RED, 3),
                            BorderFactory.createEmptyBorder(10, 5, 10, 5)));
                }
            }
        });

        setNicknamePanel.setLayout(new BoxLayout(setNicknamePanel, BoxLayout.Y_AXIS));
        setNicknamePanel.add(Box.createRigidArea(new Dimension(0,10)));
        setNicknamePanel.add(mainTitle);
        mainTitle.setAlignmentX(mainTitle.CENTER_ALIGNMENT);
        setNicknamePanel.add(Box.createRigidArea(new Dimension(0,50)));
        setNicknamePanel.add(setNicknameTextField);
        setNicknameTextField.setAlignmentX(mainTitle.CENTER_ALIGNMENT);
        setNicknamePanel.add(Box.createRigidArea(new Dimension(0,10)));
        setNicknamePanel.add(setNicknameErrorLabel);
        setNicknameErrorLabel.setAlignmentX(mainTitle.CENTER_ALIGNMENT);
        setNicknamePanel.add(Box.createRigidArea(new Dimension(0,5)));
        setNicknamePanel.add(setNicknameButton);
        setNicknameButton.setAlignmentX(mainTitle.CENTER_ALIGNMENT);

        MainPanel.add(setNicknamePanel, "SETNICKNAME");


        this.frame = new JFrame("ERIANTYS");
        frame.setSize(900,600);
        frame.setResizable(false);
        frame.setContentPane(MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * @param path
     * starts the gui and connects to the server
     */
    public static void startGui(String path) {
        ClientGUIView g = new ClientGUIView(path);
        ClientController client = new ClientController("169.254.233.39",9999, g, path);

        client.connect();
    }

    @Override
    public void update(Observable o, Object arg) {
        ClientModel clientModel = (ClientModel) o;
        this.playerId = clientModel.getPlayerId();
        this.gameId = clientModel.getGameId();
        this.inAGame = clientModel.isInAGame();
        this.tempPlayerId = clientModel.getTempPlayerId();
        Message msg = (Message) arg;
        switch (msg.getAction()) {
            case "PRINT_PLAYER":
                switchToGamesPanel();
                break;
            case "PRINT_PHASE":
                if(this.playerView!=null){
                    this.playerView.setPhase(clientModel.getPhase());
                }
                break;
            case "ATTEMPT_RECONNECTION":
                attemptReconnection(clientModel);
        }
    }


    /**
     * @return player view
     */
    public PlayerGuiView getPlayerView() {
        return playerView;
    }

    /**
     * sends a request for the games
     */
    private void switchToGamesPanel() {
        packet = new Packet("FETCH_GAMES", playerId);

        setChanged();
        notifyObservers(packet);
    }

    /**
     * @param view
     * switches to the game view
     */
    public void switchToGamePanel(PlayerGuiView view){
        this.playerView=view;

        frame.setSize(new Dimension(1200, 700));
        frame.setLocationRelativeTo(null);
        MainPanel.add(view.getMainPanel(), "GAME_PANEL");

        CardLayout cl = (CardLayout)(MainPanel.getLayout());
        cl.show(MainPanel,"GAME_PANEL");
    }

    /**
     * prints nickname warning dialog
     */
    public void printNicknameWarning() {
        setNicknameErrorLabel.setText("ERROR! This nickname is already taken");
        setNicknameErrorLabel.setVisible(true);
        setNicknameButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 3),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
    }

    /**
     * @param packet prints available games with info
     */
    @Override
    public void printGames(Packet packet) {
        JScrollPane gamesTablePanel = new JScrollPane();

        String[] columnNames = {"Game id", "Mode", "Available space", "mages already taken", "colors already taken"};
        int length = packet.getPayload().keySet().size();
        if (length > 0) {
            String[][] data = new String[length][5];
            int k = 0;
            for (String gameId : packet.getPayload().keySet()) {
                data[k][0] = gameId;
                LinkedTreeMap<String, Object> game = (LinkedTreeMap<String, Object>) packet.getFromPayload(gameId);

                data[k][1] = (String) game.get("mode").toString();
                data[k][2] = (String) game.get("empty").toString();

                ArrayList<String> mages = (ArrayList<String>) game.get("mages");
                String printString = "";
                for (String mage : mages) {
                    printString += mage + "\n";
                }
                data[k][3] = printString;



                printString = "";
                ArrayList<String> colors = (ArrayList<String>) game.get("towers");
                for (String color : colors) {
                    printString += color + "\n";
                }
                data[k][4] = printString;
                k++;
            }
            TableModel model = new DefaultTableModel(data, columnNames)
            {
                public boolean isCellEditable(int row, int column)
                {
                    return false;
                }
            };

            if(gamesTable == null) {
                gamesTable = new JTable(model);
                gamesTable.getTableHeader().setReorderingAllowed(false);
                gamesTable.setCellSelectionEnabled(false);
                gamesTable.setFocusable(false);
            } else {
                gamesTable.removeAll();
            }

            UIDefaults defaults = UIManager.getLookAndFeelDefaults();
            if (defaults.get("Table.alternateRowColor") == null)
                defaults.put("Table.alternateRowColor", new Color(196, 254, 255));

            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.CENTER);
            for(int i=0; i<5; i++) {
                gamesTable.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
            }

            availableGamesLabel = new JLabel("Available Games:");
            availableGamesLabel.setFont(new Font("Serif", Font.PLAIN, 30));
            availableGamesLabel.setVisible(true);

            joinGameTextField= new JTextField();
            joinGameTextField.setPreferredSize(new Dimension(100, 25));
            joinGameTextField.setMinimumSize(new Dimension(100, 25));
            joinGameTextField.setFont(new Font("Serif", Font.PLAIN, 15));

            joinGameButton=new JButton("Join Game");
            joinGameButton.setFocusable(false);
            joinGameButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                    BorderFactory.createEmptyBorder(10, 5, 10, 5)));
            joinGameButton.setFont(new Font("Serif", Font.PLAIN, 15));
            joinGameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JoinGameDialog dialog = new JoinGameDialog(clientController, playerId, Integer.valueOf(joinGameTextField.getText()));
                }
            });

            joinGamePanel= new JPanel();
            joinGamePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            joinGamePanel.setOpaque(false);
            joinGamePanel.add(joinGameTextField);
            joinGamePanel.add(joinGameButton);
        }

        createGameButton =new JButton("Create Game");
        createGameButton.setFocusable(false);
        createGameButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        createGameButton.setFont(new Font("Serif", Font.PLAIN, 15));
        createGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CreateGameDialog(clientController, playerId);
            }
        });

        refreshTableButton = new JButton("Look for games");
        refreshTableButton.setFocusable(false);
        refreshTableButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        refreshTableButton.setFont(new Font("Serif", Font.PLAIN, 15));
        refreshTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Packet packet = new Packet("FETCH_GAMES", playerId);

                setChanged();
                notifyObservers(packet);
            }
        });

        Image skyAndClouds = Utils.getImageFromPath(this.path, "Background1.jpg");
        gamesPanel=new BackgroundPanel(skyAndClouds);
        gamesPanel.setLayout(new BoxLayout(gamesPanel, BoxLayout.Y_AXIS));

        gamesPanel.add(Box.createRigidArea(new Dimension(0,10)));
        gamesPanel.add(createGameButton);
        createGameButton.setAlignmentX(createGameButton.CENTER_ALIGNMENT);
        gamesPanel.add(Box.createRigidArea(new Dimension(0,10)));
        gamesPanel.add(refreshTableButton);
        refreshTableButton.setAlignmentX(refreshTableButton.CENTER_ALIGNMENT);

        if(gamesTable != null) {
            gamesTablePanel.setViewportView(gamesTable);
            gamesTablePanel.setOpaque(false);
            gamesTablePanel.getViewport().setOpaque(false);

            gamesPanel.add(Box.createRigidArea(new Dimension(0,10)));
            gamesPanel.add(availableGamesLabel);
            availableGamesLabel.setAlignmentX(availableGamesLabel.CENTER_ALIGNMENT);
            gamesPanel.add(Box.createRigidArea(new Dimension(0,20)));
            gamesPanel.add(gamesTablePanel);
            gamesTablePanel.setAlignmentX(gamesTablePanel.CENTER_ALIGNMENT);
            gamesPanel.add(Box.createRigidArea(new Dimension(0,10)));
            gamesPanel.add(joinGamePanel);
            joinGamePanel.setAlignmentX(joinGamePanel.CENTER_ALIGNMENT);
            gamesPanel.add(Box.createRigidArea(new Dimension(0,10)));
        }

        MainPanel.add(gamesPanel,"GAMES");
        CardLayout cl = (CardLayout)(MainPanel.getLayout());
        cl.show(MainPanel,"GAMES");
    }

    /**
     * @param message prints a generic error
     */
    @Override
    public void printErrorMessage(String message) {
        if(this.playerView != null) {
            this.playerView.printErrorMessage(message);
        } else {
            new MessageDialog(this.path,message,new Color(207, 14, 14) );
        }

    }

    /**
     * @param message prints a generic message
     */
    @Override
    public void printMessage(Object message) {
        if(this.playerView != null) {
            this.playerView.printMessage(message.toString());
        } else {
            new MessageDialog(this.path, message.toString(), Color.BLACK );
        }
    }

    /**
     * @param packet prints available characters
     */
    @Override
    public void printCharacters(Packet packet) {
        this.playerView.printCharacters(packet);
    }

    /**
     * @param clientController adds a controller to the observers
     */
    @Override
    public void addController(ClientController clientController) {
        this.clientController=clientController;
        addObserver(clientController);
    }

    /**
     * shows the server down panel with a button to piung the server
     */
    public void serverDown() {

        String text = "Server has malfunctioned and is currently offline. <br>" +
                        "Press ping button to attempt a reconnection and restore your last checkpoint<br>";
        JLabel serverDownLabel = new JLabel("<html><div style='text-align: center;'>" + text + "</div></html>");
        serverDownLabel.setFont(new Font("Serif", Font.PLAIN, 15));
        serverDownLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton pingServerButton = new JButton("Ping Server");
        pingServerButton.setFocusable(false);
        pingServerButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        pingServerButton.setFont(new Font("Serif", Font.PLAIN, 15));
        pingServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                packet = new Packet("PING_SERVER");
                setChanged();
                notifyObservers(packet);
            }
        });

        Image background = Utils.getImageFromPath(this.path, "Background1.jpg");
        serverDownPanel= new BackgroundPanel(background);
        serverDownPanel.setLayout(new BoxLayout(serverDownPanel, BoxLayout.Y_AXIS));

        serverDownPanel.add(Box.createRigidArea(new Dimension(0,20)));
        serverDownPanel.add(serverDownLabel);
        serverDownLabel.setAlignmentX(serverDownLabel.CENTER_ALIGNMENT);
        serverDownPanel.add(Box.createRigidArea(new Dimension(0,20)));
        serverDownPanel.add(pingServerButton);
        pingServerButton.setAlignmentX(pingServerButton.CENTER_ALIGNMENT);

        MainPanel.add(serverDownPanel,"SERVER_DOWN");

        CardLayout cl = (CardLayout)(MainPanel.getLayout());
        cl.show(MainPanel,"SERVER_DOWN");
    }

    /**
     * @param clientModel
     * shows the panel to reset nickname
     */
    private void attemptReconnection(ClientModel clientModel) {
        serverDownPanel.removeAll();

        JTextField resetNicknameTextField = new JTextField();
        resetNicknameTextField.setFont(new Font("Serif", Font.PLAIN, 15));
        resetNicknameTextField.setPreferredSize(new Dimension(400, 30));
        resetNicknameTextField.setMaximumSize(new Dimension(400, 30));

        JButton resetNicknameButton = new JButton();
        if(clientModel.getNickname() == null) {
            resetNicknameButton.setText("Set Nickname");
        }else {
            resetNicknameButton.setText("Reset Nickname");
        }
        resetNicknameButton.setFocusable(false);
        resetNicknameButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        resetNicknameButton.setFont(new Font("Serif", Font.PLAIN, 15));

        resetNicknameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = resetNicknameTextField.getText();
                if(!Objects.equals(text, "")) {
                    if((text.charAt(0) >= '0' && text.charAt(0) <= '9')) {
                        setNicknameErrorLabel.setText("ERROR! Nickname can't start with a number");
                        setNicknameErrorLabel.setVisible(true);
                        resetNicknameButton.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.RED, 3),
                                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
                    } else {
                        setNicknameErrorLabel.setVisible(false);
                        resetNicknameButton.setBorder(null);

                        if(clientModel.getNickname()==null) {
                            packet = new Packet("SET_NICKNAME", clientModel.getTempPlayerId());
                            packet.addToPayload("nickname", resetNicknameTextField.getText());

                            setChanged();
                            notifyObservers(packet);
                        } else {
                            packet = new Packet("RESET_NICKNAME",playerId, gameId);
                            packet.addToPayload("nickname", resetNicknameTextField.getText());
                            packet.addToPayload("tmpPlId",clientModel.getTempPlayerId());
                            setChanged();
                            notifyObservers(packet);
                        }
                    }
                } else {
                    setNicknameErrorLabel.setText("ERROR! Nickname is required");
                    setNicknameErrorLabel.setVisible(true);
                    resetNicknameButton.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.RED, 3),
                            BorderFactory.createEmptyBorder(10, 5, 10, 5)));
                }
            }
        });

        serverDownPanel.add(Box.createRigidArea(new Dimension(0,20)));
        serverDownPanel.add(resetNicknameTextField);
        resetNicknameTextField.setAlignmentX(resetNicknameTextField.CENTER_ALIGNMENT);
        serverDownPanel.add(Box.createRigidArea(new Dimension(0,20)));
        serverDownPanel.add(setNicknameErrorLabel);
        setNicknameErrorLabel.setAlignmentX(setNicknameErrorLabel.CENTER_ALIGNMENT);
        serverDownPanel.add(Box.createRigidArea(new Dimension(0,20)));
        serverDownPanel.add(resetNicknameButton);
        resetNicknameButton.setAlignmentX(resetNicknameButton.CENTER_ALIGNMENT);

        serverDownPanel.validate();
        serverDownPanel.repaint();
    }

    /**
     * shows the panel to wait for other players to rejoin
     */
    public void switchToRejoinPanel() {

        JLabel waitForRejoinLabel=new JLabel("Wait for others to rejoin");
        waitForRejoinLabel.setFont(new Font("Serif", Font.PLAIN, 25));

        Image skyAndClouds = Utils.getImageFromPath(this.path, "Background1.jpg");
        BackgroundPanel waitForRejoinPanel = new BackgroundPanel(skyAndClouds);
        waitForRejoinPanel.setLayout(new BoxLayout(waitForRejoinPanel, BoxLayout.Y_AXIS));

        waitForRejoinPanel.add(Box.createRigidArea(new Dimension(0,10)));
        waitForRejoinPanel.add(waitForRejoinLabel);
        waitForRejoinLabel.setAlignmentX(waitForRejoinLabel.CENTER_ALIGNMENT);

        MainPanel.add(waitForRejoinPanel,"WAIT_FOR_REJOIN");

        CardLayout cl = (CardLayout)(MainPanel.getLayout());
        cl.show(MainPanel,"WAIT_FOR_REJOIN");
    }
}
