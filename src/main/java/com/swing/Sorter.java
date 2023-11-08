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
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 500;
    private static final int MAX_COMPONENT_IN_COLUMN = 10;
    private static final int VISUALIZATION_DELAY = 200;
    private static final int MAX_RANDOM_NUMBER = 1000;
    private static final int MAX_INITIAL_RANDOM_NUMBER = 30;
    private static final String INTRO_SCREEN_LABEL = "How many numbers to display?";
    private static final String INCORRECT_INPUT_NUMBER_MESSAGE =
            "Please enter a non-decimal number";
    private static final String VALUE_EXCEEDS_LIMIT_MESSAGE = "Please select a value smaller or\n"
            + "equal to 30.";
    private static final String FRAME_NAME = "Sorter";
    private static final String ENTER_BUTTON_TEXT = "Enter";
    private static final String RESET_BUTTON_TEXT = "Reset";
    private static final String SORT_BUTTON_TEXT = "Sort";
    private static final String INTRO_SCREEN_PANEL_NAME = "introScreen";
    private static final String SPLIT_PANE_NAME = "splitPane";
    private static final Random RANDOM = new Random();
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

    /**
     * Create the main window of the application
     */
    private void createAndShowUI() {
        frame = new JFrame(FRAME_NAME);
        frame.getRootPane().setBorder(BorderFactory
                .createEmptyBorder(10, 40, 10, 40));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

        createIntroScreen();
        createSortScreen();
        // Create and configure a placement manager to switch between screens
        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);

        contentPane.add(introScreen, INTRO_SCREEN_PANEL_NAME);
        contentPane.add(splitPane, SPLIT_PANE_NAME);

        frame.setContentPane(contentPane);
        frame.setVisible(true);
    }

    /**
     * Create the first screen
     */
    private void createIntroScreen() {
        introScreen = new JPanel();
        introScreen.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.CENTER;
        introScreen.add(new JLabel(INTRO_SCREEN_LABEL), constraints);

        constraints.gridy = 1;
        inputField = new JTextField(5);
        introScreen.add(inputField, constraints);

        createIntroScreenButtons(constraints);
    }

    /**
     * Create buttons for the first screen
     * @param constraints imposes constraints on the GridBagLayout
     */
    private void createIntroScreenButtons(GridBagConstraints constraints) {
        JButton enterButton = new JButton(ENTER_BUTTON_TEXT);
        enterButton.addActionListener(e -> {
            try {
                createNumberButtons(Integer.parseInt(inputField.getText()));
                cardLayout.show(contentPane, SPLIT_PANE_NAME);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, INCORRECT_INPUT_NUMBER_MESSAGE);
            }
        });
        constraints.gridy = 2;
        introScreen.add(enterButton, constraints);
    }

    /**
     * Create the second screen
     */
    private void createSortScreen() {
        leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerSize(0); // Hiding the separator
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        createSortAndResetButtons(rightPanel);
    }

    /**
     * Create control buttons for the second screen
     * @param panel indicates the panel where the buttons should be placed
     */
    private void createSortAndResetButtons(JPanel panel) {
        JButton resetButton = new JButton(RESET_BUTTON_TEXT);
        resetButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        resetButton.setMaximumSize(new Dimension(100,
                resetButton.getPreferredSize().height));
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputField.setText(""); // Clearing the TextField
                cardLayout.show(contentPane, INTRO_SCREEN_PANEL_NAME); // Return to the intro screen
            }
        });
        JButton sortButton = new JButton(SORT_BUTTON_TEXT);
        sortButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        sortButton.setMaximumSize(new Dimension(100,
                sortButton.getPreferredSize().height));
        sortButton.addActionListener(e -> {
            QuickSorter worker = new QuickSorter();
            worker.execute();
        });
        // Adding padding to the panel
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(resetButton);
        // Padding between buttons
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(sortButton);
    }

    /**
     * Create buttons with random numbers
     * @param numButtons indicates the number of buttons to be generated
     */
    private void createNumberButtons(int numButtons) {
        leftPanel.removeAll();
        buttons = new JButton[numButtons];

        for (int i = 0; i < numButtons; i++) {
            int randomNumber = RANDOM.nextInt(MAX_RANDOM_NUMBER);
            if (i == 0 && randomNumber >= MAX_INITIAL_RANDOM_NUMBER) {
                randomNumber = RANDOM.nextInt(MAX_INITIAL_RANDOM_NUMBER);
            }
            JButton current = new JButton(Integer.toString(randomNumber));
            buttons[i] = current;
            setupNumberButton(current);
        }
        addButtonsToColumns(numButtons);
    }

    /**
     * Customize created number buttons
     * @param current indicates the button to be customized
     */
    private void setupNumberButton(JButton current) {
        current.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                current.getMinimumSize().height));

        current.addActionListener(e -> {
            int value = Integer.parseInt(current.getText());
            if (value > 30) {
                JOptionPane.showMessageDialog(frame,
                        VALUE_EXCEEDS_LIMIT_MESSAGE);
            } else {
                createNumberButtons(value);
                leftPanel.revalidate();
                leftPanel.repaint();
            }
        });
    }

    /**
     * Places number buttons in the form of columns
     * @param numButtons indicates the number of buttons
     */
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

    /**
     * The QuickSorter class is responsible for performing the Quick Sort algorithm.
     * It extends SwingWorker to allow sorting to be done in the background
     * while providing updates to the UI.
     */
    private class QuickSorter extends SwingWorker<Void, Void> {
        /**
         * Invokes the quick sort method and updates the sort direction
         */
        @Override
        protected Void doInBackground() {
            quickSort(0, buttons.length - 1);
            sortDirection *= -1;
            return null;
        }

        /**
         * Refreshes the buttons after sorting is complete
         */
        @Override
        protected void done() {
            updateButtonLabels();
            frame.revalidate();
            frame.repaint();
        }

        /**
         * Recursively sorts a portion of the array of buttons using the Quick Sort algorithm.
         *
         * @param low  The lowest index of the portion to be sorted.
         * @param high The highest index of the portion to be sorted.
         */
        private void quickSort(int low, int high) {
            if (low < high) {
                int pivotIndex = partition(low, high);
                sleep();
                quickSort(low, pivotIndex - 1);
                quickSort(pivotIndex + 1, high);
            }
        }

        /**
         * Sleeps the current thread for a specified duration to allow visualization.
         */
        private void sleep() {
            try {
                Thread.sleep(VISUALIZATION_DELAY); // Delay for visualization
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * Partitions a portion of the array of buttons based on the pivot value.
         *
         * @param low  The lowest index of the portion to be partitioned.
         * @param high The highest index of the portion to be partitioned.
         * @return The index of the pivot element after partitioning.
         */
        private int partition(int low, int high) {
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

        /**
         * Updates the labels of the buttons to refresh their display.
         */
        private void updateButtonLabels() {
            for (JButton button : buttons) {
                button.setText(button.getText());
            }
        }

        /**
         * Compares two integers based on the sorting direction.
         *
         * @param a The first integer to compare.
         * @param b The second integer to compare.
         * @return a boolean on which the sort order depends
         */
        private boolean compare(int a, int b) {
            return sortDirection == 1 ? a < b : a > b;
        }
    }
}
