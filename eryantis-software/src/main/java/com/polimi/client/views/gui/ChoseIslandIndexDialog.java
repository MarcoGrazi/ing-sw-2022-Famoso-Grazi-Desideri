package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

public class ChoseIslandIndexDialog extends JDialog {
    private BackgroundPanel contentPane;
    private JComboBox islandIndex;
    private JButton buttonOK;
    private JButton buttonCancel;
    private int playerId;
    private int character;
    private int gameId;
    private ClientController controller;

    /**
     * @param controller controller that sends the packet
     * @param playerId id of playing player
     * @param gameId id of current game
     * @param character character palyed
     */
    public ChoseIslandIndexDialog(ClientController controller, int playerId, int gameId, int character) {
        this.playerId=playerId;
        this.gameId=gameId;
        this.controller=controller;
        this.character=character;

        String text = switchText();
        JLabel effectLabel = new JLabel("<html><div style='text-align: center;'>" + text + "</div></html>");
        effectLabel.setFont(new Font("Serif", Font.PLAIN, 15));
        effectLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel islandIndexLabel = new JLabel("Choose an island");
        islandIndexLabel.setFont(new Font("Serif", Font.PLAIN, 15));

        ArrayList<Integer> groups = new ArrayList<>();
        for(int i=1; i< controller.getAvGroupsNumber(); i++) {
            groups.add(i);
        }
        islandIndex= new JComboBox(groups.toArray());
        islandIndex.setPreferredSize(new Dimension(150, 25));
        islandIndex.setMinimumSize(new Dimension(150, 25));
        islandIndex.setMaximumSize(new Dimension(150, 25));
        islandIndex.setFont(new Font("Serif", Font.PLAIN, 15));

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
        contentPane.add(islandIndexLabel);
        islandIndexLabel.setAlignmentX(islandIndexLabel.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(islandIndex);
        islandIndex.setAlignmentX(islandIndex.CENTER_ALIGNMENT);
        contentPane.add(actionPanel);
        setContentPane(contentPane);

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setTitle("Effect " + character);
        setSize(600,400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    /**
     * sends the packet and closes the dialog
     */
    private void onOK() {
        Packet packet = new Packet("PLAY_CHARACTER", playerId,gameId);

        packet.addToPayload("character", character);
        packet.addToPayload("character_island_choice_" + character, islandIndex.getSelectedItem());

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
            case 3:
                return ("Chose an island and calc the influence as if mother nature is on the island.<br>" +
                        "Mother nature will still move as usual during this turn and influence will be calculated<br> " +
                        "on the island she will land on.");
            case 5:
                return ("Place a prohibition on an island of your choice.<br>" +
                        "The first time that Mother Nature is going to end her movement on<br>" +
                        "that island, replace the prohibition on this card without applying the<br>" +
                        "influence calc and without placing any tower.");
        }
        return "";
    }
}
