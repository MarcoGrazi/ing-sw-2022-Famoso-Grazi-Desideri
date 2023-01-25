package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ChoseColorDialog extends JDialog {
    private BackgroundPanel contentPane;
    private JLabel errorLabel;
    private JComboBox colorChoice;
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
    public ChoseColorDialog(ClientController controller, int playerId, int gameId, int character) {
        this.playerId=playerId;
        this.gameId=gameId;
        this.character=character;
        this.controller=controller;

        String text = switchText();
        JLabel effectLabel = new JLabel("<html><div style='text-align: center;'>" + text + "</div></html>");
        effectLabel.setFont(new Font("Serif", Font.PLAIN, 15));
        effectLabel.setHorizontalAlignment(JLabel.CENTER);

        errorLabel = new JLabel("Chose a color");
        errorLabel.setVisible(true);
        errorLabel.setFont(new Font("Serif", Font.PLAIN, 20));

        ArrayList<String> colors = Utils.getRaceString();
        colorChoice = new JComboBox(colors.toArray());
        colorChoice.setPreferredSize(new Dimension(150, 25));
        colorChoice.setMinimumSize(new Dimension(150, 25));
        colorChoice.setMaximumSize(new Dimension(150, 25));
        colorChoice.setFont(new Font("Serif", Font.PLAIN, 15));

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
        contentPane.add(colorChoice);
        colorChoice.setAlignmentX(colorChoice.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(actionPanel);
        actionPanel.setAlignmentX(actionPanel.CENTER_ALIGNMENT);
        setContentPane(contentPane);

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setTitle("Effect " + character);
        if(character==9) {
            setSize(600,200);
        } else {
            setSize(600,400);
        }
        setLocationRelativeTo(null);
        setVisible(true);
    }
    /**
     * sends the packet and closes the dialog
     */
    private void onOK() {
        Packet packet = new Packet("PLAY_CHARACTER", playerId,gameId);

        packet.addToPayload("character", character);
        packet.addToPayload("character_colour_choice_" + character, colorChoice.getSelectedItem());

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
            case 9:
                return ("Chose a race. During this turn's influence calc, that race does not give influence point.");
            case 12:
                return ("Choose a race, every player must return to the bag three students of that race <br>" +
                        "placed inside their tables.<br>" +
                        "If someone has less than three students of that race, they must return as many students as they can,<br>" +
                        "by emptying the table.");
        }
        return "";
    }
}
