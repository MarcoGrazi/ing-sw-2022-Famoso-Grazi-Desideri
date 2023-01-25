package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MotherNatureDialog extends JDialog {
    private BackgroundPanel contentPane;
    private JComboBox chosenMoves;
    private JButton buttonOK;
    private ClientController controller;
    private int playerId;
    private int gameId;

    /**
     * @param controller
     * @param playerId
     * @param gameId
     */
    public MotherNatureDialog(ClientController controller, int playerId, int gameId) {
        this.controller = controller;
        this.playerId = playerId;
        this.gameId=gameId;


        ArrayList<Integer> possibleMoves = new ArrayList<>();
        if(controller.getMode().equals("S")) {
            for(int i=1; i<=5; i++) {
                possibleMoves.add(i);
            }
        } else {
            for(int i=1; i<=7; i++) {
                possibleMoves.add(i);
            }
        }

        chosenMoves = new JComboBox(possibleMoves.toArray());
        chosenMoves.setPreferredSize(new Dimension(100, 25));
        chosenMoves.setMaximumSize(new Dimension(100, 25));
        chosenMoves.setMinimumSize(new Dimension(100, 25));
        chosenMoves.setFont(new Font("Serif", Font.PLAIN, 15));
        chosenMoves.setVisible(true);

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

        Image skyAndFields = Utils.getImageFromPath(controller.getPath(), "Background2.jpg");
        contentPane = new BackgroundPanel(skyAndFields);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(chosenMoves);
        chosenMoves.setAlignmentX(chosenMoves.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(buttonOK);
        buttonOK.setAlignmentX(buttonOK.CENTER_ALIGNMENT);
        setContentPane(contentPane);

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setTitle("Move Mother Nature");
        setSize(200,200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * sends packet to the server and closes the dialog
     */
    private void onOK() {
        Packet packet;
        packet = new Packet("MOVE_MN", playerId, gameId);

        packet.addToPayload("mn_moves", chosenMoves.getSelectedItem().toString());

        controller.guiHandle(packet);
        dispose();
    }
}
