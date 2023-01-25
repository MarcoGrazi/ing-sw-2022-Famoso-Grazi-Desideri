package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageDialog extends JDialog {
    private BackgroundPanel contentPane;
    private JLabel errorLabel;
    private JButton buttonOK;


    /**
     * @param path path where to find assets
     * @param errorText
     * @param color
     */
    public MessageDialog(String path, String errorText, Color color) {

        errorLabel = new JLabel(errorText);
        errorLabel.setVisible(true);
        errorLabel.setFont(new Font("Serif", Font.BOLD, 20));
        errorLabel.setForeground(color);

        buttonOK = new JButton("  OK  ");
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

        Image skyAndFields = Utils.getImageFromPath(path, "Background2.jpg");
        contentPane = new BackgroundPanel(skyAndFields);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(errorLabel);
        errorLabel.setAlignmentX(errorLabel.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(buttonOK);
        buttonOK.setAlignmentX(buttonOK.CENTER_ALIGNMENT);
        setContentPane(contentPane);

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setTitle("Warning");
        setSize(700,200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * closes the dialog
     */
    private void onOK() {
        dispose();
    }
}
