package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class MoveStudentDialog extends JDialog {
    private BackgroundPanel contentPane;
    private JRadioButton buttonTable;
    private JRadioButton buttonIsland;
    private JComboBox chosenIsland;
    private JButton buttonOK;
    private JButton buttonCancel;
    private int chosenStudent;
    private ClientController controller;
    private int playerId;
    private int gameId;

    /**
     * @param controller
     * @param playerId
     * @param studentId
     * @param groupsNumber
     * @param gameId
     */
    public MoveStudentDialog(ClientController controller, int playerId, int studentId, int groupsNumber ,int gameId) {
        this.controller = controller;
        this.playerId = playerId;
        this.gameId=gameId;
        this.chosenStudent = studentId;

        ArrayList<Integer> groups = new ArrayList<>();
        for(int i=1; i<groupsNumber+1; i++) {
            groups.add(i);
        }
        chosenIsland = new JComboBox(groups.toArray());
        chosenIsland.setPreferredSize(new Dimension(100, 25));
        chosenIsland.setMaximumSize(new Dimension(100, 25));
        chosenIsland.setMinimumSize(new Dimension(100, 25));
        chosenIsland.setFont(new Font("Serif", Font.PLAIN, 15));
        chosenIsland.setVisible(true);

        buttonTable= new JRadioButton("Move to Table");
        buttonTable.setOpaque(false);
        buttonTable.setFocusable(false);

        buttonIsland = new JRadioButton("Move to Island");
        buttonIsland.setOpaque(false);
        buttonIsland.setFocusable(false);
        buttonIsland.setSelected(true);

        buttonTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(buttonTable.isSelected()){
                    buttonIsland.setSelected(false);
                    chosenIsland.setVisible(false);
                }else{
                    buttonIsland.setSelected(true);
                    chosenIsland.setVisible(true);
                }
            }
        });

        buttonIsland.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(buttonIsland.isSelected()){
                    buttonTable.setSelected(false);
                    chosenIsland.setVisible(true);
                }else{
                    buttonTable.setSelected(true);
                    chosenIsland.setVisible(false);
                }
            }
        });

        buttonOK = new JButton(" Move ");
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
        contentPane.add(buttonTable);
        buttonTable.setAlignmentX(buttonTable.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(buttonIsland);
        buttonIsland.setAlignmentX(buttonIsland.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(chosenIsland);
        chosenIsland.setAlignmentX(chosenIsland.CENTER_ALIGNMENT);
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

        setTitle("Move Student " + chosenStudent);
        setSize(400,300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * closes the dialog and sends packet to the server
     */
    private void onOK() {
        Packet packet;
        packet = new Packet("MOVE_STUDENT", playerId, gameId);
        packet.addToPayload("move_to", (buttonTable.isSelected() ? "T" : "I"));
        packet.addToPayload("student_index", chosenStudent);

        if(buttonIsland.isSelected()) {
            packet.addToPayload("group_number", Math.round(Integer.parseInt(chosenIsland.getSelectedItem().toString())));
        }

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
