package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CharacterDialog extends JDialog {
    private BackgroundPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private int playerId;
    private int character;
    private int gameId;
    private ClientController controller;

    /**
     * @param controller contorller that will send the final packet
     * @param playerId id of playing player
     * @param gameId id of current game
     * @param character id of character to play
     */
    public CharacterDialog(ClientController controller, int playerId, int gameId, int character) {
        this.playerId=playerId;
        this.gameId=gameId;
        this.controller=controller;
        this.character=character;

        String text = switchText();
        JLabel effectLabel = new JLabel("<html><div style='text-align: center;'>" + text + "</div></html>");
        effectLabel.setFont(new Font("Serif", Font.PLAIN, 15));
        effectLabel.setHorizontalAlignment(JLabel.CENTER);

        buttonOK = new JButton(" Confirm ");
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
        contentPane.add(effectLabel);
        effectLabel.setAlignmentX(effectLabel.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(actionPanel);
        actionPanel.setAlignmentX(actionPanel.CENTER_ALIGNMENT);
        setContentPane(contentPane);

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setTitle("Effect " + character);
        setSize(600,200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * sends the packet and closes the dialog
     */
    private void onOK() {
        Packet packet = new Packet("PLAY_CHARACTER", playerId, gameId);
        packet.addToPayload("character", character);

        controller.guiHandle(packet);
        dispose();
    }

    /**
     * closes the dialog
     */
    private void onCancel() {
        dispose();
    }

    /**
     * @return text of played character
     */
    private String switchText() {
        switch (character) {
            case 2:
                return ("During this turn, you still gain a professor even if the number of students <br>" +
                        "in your table is even to the number of students inside the table <br>" +
                        "of the player who now controls that professor.");
            case 4:
                return ("You can move mother nature of 2 additional islands<br>" +
                        "compared to what is determined by the assistant card you played.");
            case 6:
                return ("During the influence calc on an island or a group of island,<br> " +
                        "the towers are not counted.");
            case 8:
                return ("During this turn you gain two additional influence points<br> " +
                        "during the influence calc.");
        }

        return "";
    }
}
