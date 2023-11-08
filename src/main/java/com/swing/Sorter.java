package com.swing;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class Sorter {
    private static final int MAX_COMPONENT_IN_COLUMN = 10;
    private JFrame frame;
    private JPanel introScreen;
    private JPanel leftPanel;
    private JSplitPane splitPane;
    private JTextField inputField;
    private CardLayout cardLayout;
    private JPanel contentPane;
    private JButton[] buttons;
    private int sortDirection = -1;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Sorter().createAndShowUI());
    }

    private void createAndShowUI() {
        // Creating the main window of the application
        frame = new JFrame("Sorter");
        frame.getRootPane().setBorder(BorderFactory
                .createEmptyBorder(10, 40, 10, 40));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        createIntroScreen();
        createSortScreen();
        // Create and configure a placement manager to switch between screens
        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);

        contentPane.add(introScreen, "introScreen");
        contentPane.add(splitPane, "splitPane");

        frame.setContentPane(contentPane);
        frame.setVisible(true);
    }

    // Creating the first screen
    private void createIntroScreen() {
        introScreen = new JPanel();
        introScreen.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.CENTER;
        introScreen.add(new JLabel("How many numbers to display?"), constraints);

        constraints.gridy = 1;
        inputField = new JTextField(5);
        introScreen.add(inputField, constraints);

        createIntroScreenButtons(constraints);
    }

    private void createIntroScreenButtons(GridBagConstraints constraints) {
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    createNumberButtons(Integer.parseInt(inputField.getText()));
                    cardLayout.show(contentPane, "splitPane");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a non-decimal number");
                }
            }
        });
        constraints.gridy = 2;
        introScreen.add(enterButton, constraints);
    }

    // Creating the second screen
    private void createSortScreen() {
        leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerSize(0); // Hiding the separator
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        createSortAndResetButtons(rightPanel);
    }

    private void createSortAndResetButtons(JPanel panel) {
        JButton resetButton = new JButton("Reset");
        resetButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        resetButton.setMaximumSize(new Dimension(100,
                resetButton.getPreferredSize().height));
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputField.setText(""); // Clearing the TextField
                cardLayout.show(contentPane, "introScreen"); // Return to the intro screen
            }
        });

        JButton sortButton = new JButton("Sort");
        sortButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        sortButton.setMaximumSize(new Dimension(100,
                sortButton.getPreferredSize().height));
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QuickSorter worker = new QuickSorter();
                worker.execute();
            }
        });
        // Adding padding to the panel
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(resetButton);
        // Padding between buttons
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(sortButton);
    }

    // Creating buttons with random numbers
    private void createNumberButtons(int numButtons) {
        leftPanel.removeAll();
        buttons = new JButton[numButtons];

        for (int i = 0; i < numButtons; i++) {
            int randomNumber = new Random().nextInt(1000);
            if (i == 0 && randomNumber >= 30) {
                randomNumber = new Random().nextInt(30);
            }
            JButton current = new JButton(Integer.toString(randomNumber));
            buttons[i] = current;
            setupNumberButton(current);
        }
        addButtonsToColumns(numButtons);
    }

    // Customize buttons
    private void setupNumberButton(JButton current) {
        current.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                current.getMinimumSize().height));

        current.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int value = Integer.parseInt(current.getText());
                if (value > 30) {
                    JOptionPane.showMessageDialog(frame,
                            "Please select a value smaller or\n"
                                    + "equal to 30.",
                            "Message", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    createNumberButtons(buttons.length);
                    leftPanel.revalidate();
                    leftPanel.repaint();
                }
            }
        });
    }

    // Placement of number buttons in the form of columns
    private void addButtonsToColumns(int numButtons) {
        JPanel panel = new JPanel();

        int buttonsInColumn = 0;
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < numButtons; i++) {
            columnPanel.add(buttons[i]);
            buttonsInColumn++;
            if (buttonsInColumn == MAX_COMPONENT_IN_COLUMN || i == numButtons - 1) {
                panel.add(columnPanel);

                if (i < numButtons - 1) {
                    // Add vertical padding between columns
                    panel.add(Box.createVerticalStrut(5));
                }
                columnPanel = new JPanel();
                columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));
                buttonsInColumn = 0;
            }
        }
        leftPanel.add(panel);
    }

    private class QuickSorter extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() {
            // Invokes the quick sort method and updates the sort direction
            quickSort(0, buttons.length - 1);
            sortDirection *= -1;
            return null;
        }

        @Override
        protected void done() {
            // Refreshes the buttons after sorting is complete
            updateButtonLabels();
            frame.revalidate();
            frame.repaint();
        }
    }

    public void quickSort(int low, int high) {
        if (low < high) {
            int pivotIndex = partition(low, high);
            try {
                Thread.sleep(200); // Delay for visualization
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Recursive sorting of the left and right parts
            quickSort(low, pivotIndex - 1);
            quickSort(pivotIndex + 1, high);
        }
    }

    public int partition(int low, int high) {
        int pivot = Integer.parseInt(buttons[high].getText());
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (compare(Integer.parseInt(buttons[j].getText()), pivot)) {
                i++;
                String tempText = buttons[i].getText();
                buttons[i].setText(buttons[j].getText());
                buttons[j].setText(tempText);
            }
        }
        String tempText = buttons[i + 1].getText();
        buttons[i + 1].setText(buttons[high].getText());
        buttons[high].setText(tempText);

        return i + 1;
    }

    public void updateButtonLabels() {
        for (JButton button : buttons) {
            button.setText(button.getText());
        }
    }

    // Determines the direction of sorting
    public boolean compare(int a, int b) {
        return sortDirection == 1 ? a < b : a > b;
    }
}
