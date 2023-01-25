package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.models.Mage;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreateGameDialog extends JDialog {
    private BackgroundPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox mageName;
    private JComboBox colour;
    private JRadioButton sampleMode;
    private JRadioButton expertMode;
    private JComboBox playerNumber;
    private ClientController controller;
    private int playerId;

    /**
     * @param controller controller that sends the packet
     * @param playerId id of playing player
     */
    public CreateGameDialog(ClientController controller, int playerId) {
        this.controller=controller;
        this.playerId=playerId;

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

        sampleMode=new JRadioButton("Simple mode");
        sampleMode.setOpaque(false);
        sampleMode.setFocusable(false);
        sampleMode.setSelected(true);
        sampleMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sampleMode.isSelected()){
                    expertMode.setSelected(false);
                }else{
                    expertMode.setSelected(true);
                }
            }
        });

        expertMode = new JRadioButton("Expert mode");
        expertMode.setOpaque(false);
        expertMode.setFocusable(false);
        expertMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(expertMode.isSelected()){
                    sampleMode.setSelected(false);
                }else{
                    sampleMode.setSelected(true);
                }
            }
        });

        playerNumber=new JComboBox(Utils.playerNumberArray);
        playerNumber.setPreferredSize(new Dimension(75, 25));
        playerNumber.setMinimumSize(new Dimension(75, 25));
        playerNumber.setMaximumSize(new Dimension(75, 25));
        playerNumber.setFont(new Font("Serif", Font.PLAIN, 15));

        buttonOK = new JButton(" Create ");
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
        contentPane.add(sampleMode);
        sampleMode.setAlignmentX(sampleMode.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(expertMode);
        expertMode.setAlignmentX(expertMode.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(playerNumber);
        playerNumber.setAlignmentX(playerNumber.CENTER_ALIGNMENT);
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

        setTitle("Create Game");
        setSize(400,300);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    /**
     * sends the packet and closes the dialog
     */
    private void onOK() {
        Packet packet;
        packet = new Packet("CREATE_GAME",playerId);
        packet.addToPayload("game_mode", sampleMode.isSelected()? "S": "E");
        packet.addToPayload("player_number", Math.round(Integer.parseInt((String) playerNumber.getSelectedItem())));

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
