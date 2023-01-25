package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Effect7Dialog extends JDialog {
    private BackgroundPanel contentPane;
    private JComboBox replacementsChoice;
    private JPanel cardStudents;
    private JCheckBox indexC0;
    private JCheckBox indexC1;
    private JCheckBox indexC2;
    private JCheckBox indexC3;
    private JCheckBox indexC4;
    private JPanel hallStudents;
    private JCheckBox indexC5;
    private JCheckBox indexH0;
    private JCheckBox indexH1;
    private JCheckBox indexH2;
    private JCheckBox indexH3;
    private JCheckBox indexH4;
    private JCheckBox indexH5;
    private JButton buttonOK;
    private JButton buttonCancel;
    private ClientController controller;
    private int playerId;
    private int gameId;
    private int numberOfCardReplacements;
    private int numberOfHallReplacements;

    /**
     * @param controller controller that sends the packet
     * @param playerId id of playing player
     * @param gameId id of current game
     */
    public Effect7Dialog(ClientController controller, int playerId, int gameId) {
        this.playerId=playerId;
        this.gameId=gameId;
        this.controller=controller;

        this.numberOfHallReplacements = 1;
        this.numberOfCardReplacements = 1;

        String text = "You can take up to three students from this card and exchange with the same number of students from your hall.";
        JLabel effectLabel = new JLabel("<html><div style='text-align: center;'>" + text + "</div></html>");
        effectLabel.setFont(new Font("Serif", Font.PLAIN, 15));

        JLabel movementNumberLabel = new JLabel("Choose the number of replacements");
        movementNumberLabel.setFont(new Font("Serif", Font.BOLD, 15));

        ArrayList<Integer> replacements = new ArrayList<>();
        for(int i=1; i<4; i++) {
            replacements.add(i);
        }
        replacementsChoice = new JComboBox(replacements.toArray());
        replacementsChoice.setPreferredSize(new Dimension(150, 25));
        replacementsChoice.setMinimumSize(new Dimension(150, 25));
        replacementsChoice.setMaximumSize(new Dimension(150, 25));
        replacementsChoice.setFont(new Font("Serif", Font.PLAIN, 15));
        replacementsChoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkButton();
            }
        });

        initializeCardCheckbox();
        initializeHallCheckbox();

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new GridLayout(1, 2));
        checkboxPanel.add(cardStudents);
        checkboxPanel.add(hallStudents);
        checkboxPanel.setOpaque(false);

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
        contentPane.add(movementNumberLabel);
        movementNumberLabel.setAlignmentX(movementNumberLabel.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(replacementsChoice);
        replacementsChoice.setAlignmentX(replacementsChoice.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,20)));
        contentPane.add(checkboxPanel);
        checkboxPanel.setAlignmentX(checkboxPanel.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(actionPanel);
        actionPanel.setAlignmentX(actionPanel.CENTER_ALIGNMENT);
        setContentPane(contentPane);

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setTitle("Effect 7");
        setSize(800,600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * @param checkBox checkbox selected
     * @param checkGroup group of students that can be in the left or right
     * selects students from card to replace
     */
    private void performCheckboxAction(JCheckBox checkBox, String checkGroup) {
        if(checkBox.isSelected()) {
            if(checkGroup.equals("Card")) {
                numberOfCardReplacements++;
            } else {
                numberOfHallReplacements++;
            }
        } else {
            if(checkGroup.equals("Card")) {
                numberOfCardReplacements--;
            } else {
                numberOfHallReplacements--;
            }
        }
        checkButton();
    }

    /**
     * checks the nomer of replacements is valid, and the replacements are valid
     */
    private void checkButton() {
        int replacements = (int) replacementsChoice.getSelectedItem();

        if(numberOfHallReplacements != numberOfCardReplacements ||
            numberOfHallReplacements != replacements ||
            numberOfCardReplacements != replacements) {

            buttonOK.setEnabled(false);
        } else {
            buttonOK.setEnabled(true);
        }
    }

    /**
     * sends the packet and closes the dialog
     */
    private void onOK() {
        Packet packet;
        packet = new Packet("PLAY_CHARACTER",playerId, gameId);

        packet.addToPayload("character", 7);
        packet.addToPayload("character_number_moves", replacementsChoice.getSelectedItem());

        int j=0;
        for(Component c: cardStudents.getComponents()) {
            if(((JCheckBox) c).isSelected()) {
                String text = ((JCheckBox) c).getText();
                Integer index = Integer.parseInt((text).substring(text.length() - 1));

                packet.addToPayload("character_card_choice_" + j, index);
                j++;
            }
        }

        int i=0;
        for(Component c: hallStudents.getComponents()) {
            if(((JCheckBox) c).isSelected()) {
                String text = ((JCheckBox) c).getText();
                Integer index = Integer.parseInt((text).substring(text.length() - 1));

                packet.addToPayload("character_hall_choice_" + i, index);
                i++;
            }
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

    private void initializeCardCheckbox() {

        indexC0 = new JCheckBox("Student 0");
        indexC0.setSelected(true);
        indexC0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexC0, "Card");
            }
        });

        indexC1 = new JCheckBox("Student 1");
        indexC1.setSelected(false);
        indexC1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexC1, "Card");
            }
        });

        indexC2 = new JCheckBox("Student 2");
        indexC2.setSelected(false);
        indexC2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexC2, "Card");
            }
        });

        indexC3 = new JCheckBox("Student 3");
        indexC3.setSelected(false);
        indexC3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexC3, "Card");
            }
        });

        indexC4 = new JCheckBox("Student 4");
        indexC4.setSelected(false);
        indexC4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexC4, "Card");
            }
        });

        indexC5 = new JCheckBox("Student 5");
        indexC5.setSelected(false);
        indexC5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexC5, "Card");
            }
        });

        cardStudents = new JPanel();
        cardStudents.setLayout(new GridLayout(6, 1));
        cardStudents.setOpaque(false);
        cardStudents.add(indexC0);
        cardStudents.add(indexC1);
        cardStudents.add(indexC2);
        cardStudents.add(indexC3);
        cardStudents.add(indexC4);
        cardStudents.add(indexC5);
    }

    /**
     * Initializes the dialog and the checkboxes for the hall
     */
    private void initializeHallCheckbox() {

        indexH0 = new JCheckBox("Student 0");
        indexH0.setSelected(true);
        indexH0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH0, "Hall");
            }
        });

        indexH1 = new JCheckBox("Student 1");
        indexH1.setSelected(false);
        indexH1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH1, "Hall");
            }
        });

        indexH2 = new JCheckBox("Student 2");
        indexH2.setSelected(false);
        indexH2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH2, "Hall");
            }
        });

        indexH3 = new JCheckBox("Student 3");
        indexH3.setSelected(false);
        indexH3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH3, "Hall");
            }
        });

        indexH4 = new JCheckBox("Student 4");
        indexH4.setSelected(false);
        indexH4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH4, "Hall");
            }
        });

        indexH5 = new JCheckBox("Student 5");
        indexH5.setSelected(false);
        indexH5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH5, "Hall");
            }
        });

        hallStudents = new JPanel();
        hallStudents.setLayout(new GridLayout(6, 1));
        hallStudents.setOpaque(false);
        hallStudents.add(indexH0);
        hallStudents.add(indexH1);
        hallStudents.add(indexH2);
        hallStudents.add(indexH3);
        hallStudents.add(indexH4);
        hallStudents.add(indexH5);
    }
}
