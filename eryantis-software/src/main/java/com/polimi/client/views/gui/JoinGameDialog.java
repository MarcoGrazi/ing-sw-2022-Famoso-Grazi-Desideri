package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JoinGameDialog extends JDialog {
    private BackgroundPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox mageName;
    private JComboBox colour;
    private ClientController controller;
    private int playerId;
    private int gameId;

    /**
     * @param controller controller that sends the packet
     * @param playerId id of playing player
     * @param gameId id of current game
     */
    public JoinGameDialog(ClientController controller, int playerId, int gameId) {
        this.controller=controller;
        this.playerId=playerId;
        this.gameId=gameId;

        mageName= new JComboBox(Utils.mageStrings);
        mageName.setPreferredSize(new Dimension(150, 25));
        mageName.setMinimumSize(new Dimension(150, 25));
        mageName.setMaximumSize(new Dimension(150, 25));
        mageName.setFont(new Font("Serif", Font.PLAIN, 15));

        colour=new JComboBox(Utils.colourStrings);
        colour.setPreferredSize(new Dimension(100, 25));
        colour.setMinimumSize(new Dimension(100, 25));
        colour.setMaximumSize(new Dimension(100, 25));
        colour.setFont(new Font("Serif", Font.PLAIN, 15));

        buttonOK = new JButton("  Join  ");
        buttonOK.setFocusable(false);
        buttonOK.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        buttonOK.setFont(new Font("Serif", Font.PLAIN, 15));
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel= new JButton(" Cancel ");
        buttonCancel.setFocusable(false);
        buttonCancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        buttonCancel.setFont(new Font("Serif", Font.PLAIN, 15));
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        actionPanel.add(buttonCancel);
        actionPanel.add(buttonOK);

        Image skyAndFields = Utils.getImageFromPath(controller.getPath(), "Background2.jpg");
        contentPane = new BackgroundPanel(skyAndFields);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(mageName);
        mageName.setAlignmentX(mageName.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(colour);
        colour.setAlignmentX(colour.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(actionPanel);
        actionPanel.setAlignmentX(actionPanel.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        setContentPane(contentPane);

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setTitle("Join Game");
        setSize(400,300);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    /**
     * sends the packet and closes the dialog
     */
    private void onOK() {
        Packet packet;
        packet = new Packet("JOIN_GAME",playerId, gameId);

        packet.addToPayload("mage_name", mageName.getSelectedItem());
        packet.addToPayload("tower_colour",colour.getSelectedItem());

        controller.guiHandle(packet);
        dispose();
    }
    /**
     * closes the dialog
     */
    private void onCancel() {
        dispose();
    }
}
