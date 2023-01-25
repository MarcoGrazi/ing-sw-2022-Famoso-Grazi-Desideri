package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Effect1Dialog extends JDialog {
    private BackgroundPanel contentPane;
    JComboBox islandIndex;
    private int islIndex;
    JComboBox studentIndex;
    private int studIndex;
    private JButton buttonOK;
    private JButton buttonCancel;
    private ClientController controller;
    private int playerId;
    private int gameId;
    /**
     * @param controller controller that sends the packet
     * @param playerId id of playing player
     * @param gameId id of current game
     */
    public Effect1Dialog(ClientController controller, int playerId, int gameId) {
        this.playerId=playerId;
        this.gameId=gameId;
        this.controller=controller;

        this.studIndex = 1;
        this.islIndex = 1;

        String text = "Take one student from the card and place it on an island of your choice.<br>" +
                            "Then, draw a student from the bag and place it on this card.";
        JLabel effectLabel = new JLabel("<html><div style='text-align: center;'>" + text + "</div></html>");
        effectLabel.setFont(new Font("Serif", Font.PLAIN, 15));
        effectLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel studentIndexLabel = new JLabel("Choose a student");
        studentIndexLabel.setFont(new Font("Serif", Font.PLAIN, 15));

        ArrayList<Integer> choices = new ArrayList<>();
        for(int i=0; i<4; i++) {
            choices.add(i);
        }
        studentIndex= new JComboBox(choices.toArray());
        studentIndex.setPreferredSize(new Dimension(150, 25));
        studentIndex.setMinimumSize(new Dimension(150, 25));
        studentIndex.setMaximumSize(new Dimension(150, 25));
        studentIndex.setFont(new Font("Serif", Font.PLAIN, 15));
        studentIndex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                studIndex = (int) studentIndex.getSelectedItem();
            }
        });

        JLabel islandIndexLabel = new JLabel("Choose an island");
        islandIndexLabel.setFont(new Font("Serif", Font.PLAIN, 15));

        ArrayList<Integer> groups = new ArrayList<>();
        for(int i=1; i< controller.getAvGroupsNumber()+1; i++) {
            groups.add(i);
        }
        islandIndex= new JComboBox(groups.toArray());
        islandIndex.setPreferredSize(new Dimension(150, 25));
        islandIndex.setMinimumSize(new Dimension(150, 25));
        islandIndex.setMaximumSize(new Dimension(150, 25));
        islandIndex.setFont(new Font("Serif", Font.PLAIN, 15));
        islandIndex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                islIndex = (int) islandIndex.getSelectedItem();
            }
        });

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
        contentPane.add(studentIndexLabel);
        studentIndexLabel.setAlignmentX(studentIndexLabel.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(studentIndex);
        studentIndex.setAlignmentX(studentIndex.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(islandIndexLabel);
        islandIndexLabel.setAlignmentX(islandIndexLabel.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(islandIndex);
        islandIndex.setAlignmentX(islandIndex.CENTER_ALIGNMENT);
        contentPane.add(actionPanel);
        actionPanel.setAlignmentX(actionPanel.CENTER_ALIGNMENT);
        setContentPane(contentPane);

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setTitle("Effect 1");
        setSize(600,400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    /**
     * sends the packet and closes the dialog
     */
    private void onOK() {
        Packet packet;
        packet = new Packet("PLAY_CHARACTER",playerId, gameId);

        packet.addToPayload("character", 1);
        packet.addToPayload("character_student_choice_1", studIndex);
        packet.addToPayload("character_island_choice_1", islIndex);

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
