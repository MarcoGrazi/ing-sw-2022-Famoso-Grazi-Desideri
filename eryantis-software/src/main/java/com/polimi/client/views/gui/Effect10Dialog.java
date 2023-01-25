package main.java.com.polimi.client.views.gui;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Effect10Dialog extends JDialog {
    private BackgroundPanel contentPane;
    private JComboBox replacementsChoice;
    private JPanel tableStudents;
    private JComboBox fairy;
    private JComboBox dragon;
    private JComboBox unicorn;
    private JComboBox frog;
    private JComboBox elf;
    private JPanel hallStudents;
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
    private int numberOfTableReplacements;
    private int numberOfHallReplacements;
    ArrayList<Integer> values;

    /**
     * @param controller controller that sends the packet
     * @param playerId id of playing player
     * @param gameId id of current game
     */
    public Effect10Dialog(ClientController controller, int playerId, int gameId) {
        this.playerId=playerId;
        this.gameId=gameId;
        this.controller=controller;

        this.numberOfHallReplacements = 1;
        this.numberOfTableReplacements = 0;
        values = new ArrayList<>();
        for(int i=0; i<3; i++) {
            values.add(i);
        }

        String text = "You can exchange up to two students from your tables with the same amount of students in your hall.";
        JLabel effectLabel = new JLabel("<html><div style='text-align: center;'>" + text + "</div></html>");
        effectLabel.setFont(new Font("Serif", Font.PLAIN, 15));
        effectLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel movementNumberLabel = new JLabel("Choose the number of replacements");
        movementNumberLabel.setFont(new Font("Serif", Font.BOLD, 15));

        ArrayList<Integer> replacements = new ArrayList<>();
        for(int i=1; i<3; i++) {
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

        initializeTableComboBox();
        initializeHallCheckbox();

        JPanel choicesPanel = new JPanel();
        choicesPanel.setLayout(new GridLayout(1, 2));
        choicesPanel.add(tableStudents);
        choicesPanel.add(hallStudents);
        choicesPanel.setOpaque(false);

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
        buttonOK.setEnabled(false);

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
        contentPane.add(choicesPanel);
        choicesPanel.setAlignmentX(choicesPanel.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(actionPanel);
        actionPanel.setAlignmentX(actionPanel.CENTER_ALIGNMENT);
        setContentPane(contentPane);

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setTitle("Effect 10");
        setSize(800,500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void performCheckboxAction(JCheckBox checkBox) {
        if(checkBox.isSelected()) {
            numberOfHallReplacements++;
        } else {
            numberOfHallReplacements--;
        }
        checkButton();
    }

    private void checkButton() {
        int replacements = (int) replacementsChoice.getSelectedItem();
        int totalTables = 0;

        for(Component c: tableStudents.getComponents()) {
            if(c instanceof JComboBox<?>) {
                totalTables = totalTables + (int) ((JComboBox<?>) c).getSelectedItem();
            }
        }

        if(numberOfHallReplacements != totalTables ||
                numberOfHallReplacements != replacements ||
                totalTables != replacements) {

            buttonOK.setEnabled(false);

            values.clear();
            for(int i=0; i< (int) replacementsChoice.getSelectedItem() + 1; i++) {
                values.add(i);
            }
        } else {
            buttonOK.setEnabled(true);

            values.clear();
            for(int i=0; i< (int) replacementsChoice.getSelectedItem() + 1; i++) {
                values.add(i);
            }
        }
    }
    /**
     * sends the packet and closes the dialog
     */
    private void onOK() {
        Packet packet;
        packet = new Packet("PLAY_CHARACTER",playerId, gameId);

        packet.addToPayload("character", 10);
        packet.addToPayload("character_number_moves", replacementsChoice.getSelectedItem());

        int i=0;
        for(Component c: hallStudents.getComponents()) {
            if(((JCheckBox) c).isSelected()) {
                String text = ((JCheckBox) c).getText();
                Integer index = Integer.parseInt((text).substring(text.length() - 1));

                packet.addToPayload("character_hall_choice_" + i, index);
                i++;
            }
        }

        int index=0;
        int k=0;
        for(Component c: tableStudents.getComponents()) {
            if(c instanceof JComboBox<?>) {
                String labelText = ((JLabel) tableStudents.getComponents()[index-1]).getText().toUpperCase();
                String raceString = labelText.substring(0, labelText.length() - 2);

                int amountSelected = (int) ((JComboBox<?>) c).getSelectedItem();
                for(int h=0; h<amountSelected; h++) {
                    packet.addToPayload("character_table_choice_" + k, raceString);
                    k++;
                }
            }
            index++;
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

    /**
     * initializes table for replacements
     */
    private void initializeTableComboBox() {

        JLabel fairyLabel = new JLabel("Fairy: ");
        fairyLabel.setFont(new Font("Serif", Font.PLAIN, 15));

        fairy = new JComboBox(values.toArray());
        fairy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkButton();
            }
        });

        JLabel dragonLabel = new JLabel("Dragon: ");
        dragonLabel.setFont(new Font("Serif", Font.PLAIN, 15));

        dragon = new JComboBox(values.toArray());
        dragon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                checkButton();
            }
        });

        JLabel unicornLabel = new JLabel("Unicorn: ");
        unicornLabel.setFont(new Font("Serif", Font.PLAIN, 15));

        unicorn = new JComboBox(values.toArray());
        unicorn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                checkButton();
            }
        });

        JLabel frogLabel = new JLabel("Frog: ");
        frogLabel.setFont(new Font("Serif", Font.PLAIN, 15));

        frog = new JComboBox(values.toArray());
        frog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                checkButton();
            }
        });

        JLabel elfLabel = new JLabel("Elf: ");
        elfLabel.setFont(new Font("Serif", Font.PLAIN, 15));

        elf = new JComboBox(values.toArray());
        elf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                checkButton();
            }
        });


        tableStudents = new JPanel();
        tableStudents.setLayout(new GridLayout(6, 2));
        tableStudents.setOpaque(false);
        tableStudents.add(fairyLabel);
        tableStudents.add(fairy);
        tableStudents.add(dragonLabel);
        tableStudents.add(dragon);
        tableStudents.add(unicornLabel);
        tableStudents.add(unicorn);
        tableStudents.add(frogLabel);
        tableStudents.add(frog);
        tableStudents.add(elfLabel);
        tableStudents.add(elf);
    }

    /**
     * initializes checkboxes for hall replacements
     */
    private void initializeHallCheckbox() {

        indexH0 = new JCheckBox("Student 0");
        indexH0.setSelected(true);
        indexH0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH0);
            }
        });

        indexH1 = new JCheckBox("Student 1");
        indexH1.setSelected(false);
        indexH1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH1);
            }
        });

        indexH2 = new JCheckBox("Student 2");
        indexH2.setSelected(false);
        indexH2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH2);
            }
        });

        indexH3 = new JCheckBox("Student 3");
        indexH3.setSelected(false);
        indexH3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH3);
            }
        });

        indexH4 = new JCheckBox("Student 4");
        indexH4.setSelected(false);
        indexH4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH4);
            }
        });

        indexH5 = new JCheckBox("Student 5");
        indexH5.setSelected(false);
        indexH5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckboxAction(indexH5);
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
