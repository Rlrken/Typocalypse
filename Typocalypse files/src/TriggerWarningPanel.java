import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TriggerWarningPanel extends JPanel {
    private JPanel screenManager;
    private boolean keyListenerEnabled = false;
    private JTextArea contentArea;

    public TriggerWarningPanel(JPanel screenManager) {
        this.screenManager = screenManager;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Create the warning panel with a vertical box layout
        JPanel warningPanel = new JPanel(new BorderLayout());
        warningPanel.setBackground(Color.BLACK);
        warningPanel.setBorder(BorderFactory.createEmptyBorder(120, 50, 0, 50));
        add(warningPanel, BorderLayout.CENTER);

        // Warning title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        warningPanel.add(titlePanel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("TRIGGER WARNING");
        titleLabel.setForeground(Color.RED);
        titleLabel.setFont(new Font("VCR OSD Mono", Font.BOLD, 80));
        titlePanel.add(titleLabel);

        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.BLACK);
        warningPanel.add(textPanel, BorderLayout.CENTER);

        // Warning content
        contentArea = new JTextArea(
                "This game contains content that may be disturbing to some players, including:\n\n" +
                        "• Flashing images and sudden visual effects\n" +
                        "• Disturbing imagery\n" +
                        "• Jump scares\n" +
                        "• Loud sounds\n" +
                        "• Themes related to anxiety and stress\n\n" +
                        "Player discretion is advised. If you are sensitive to such content, " +
                        "please consider if this game is appropriate for you.\n\n" +
                        "You have been warned, " + NameInputPanel.playerName + "!"
        );
        contentArea.setPreferredSize(new Dimension(1000, 500));
        contentArea.setForeground(Color.WHITE);
        contentArea.setBackground(new Color(0, 0, 0, 0));
        contentArea.setFont(new Font("VCR OSD Mono", Font.PLAIN, 30));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setFocusable(false);
        contentArea.setEnabled(false);
        contentArea.setOpaque(false);
        contentArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentArea.setMargin(new Insets(20, 20, 20, 20));
        textPanel.add(contentArea);

        // ===== BOTTOM PANEL (Retry Prompt) =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0)); // Adds vertical spacing

        JLabel continueLabel = new JLabel("- PRESS ENTER TO CONTINUE -");
        continueLabel.setForeground(Color.WHITE);
        continueLabel.setFont(new Font("VCR OSD Mono", Font.PLAIN, 30));
        continueLabel.setVisible(false);
        bottomPanel.add(continueLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        Timer timer = new Timer(2000, event -> {
            continueLabel.setVisible(true);
            keyListenerEnabled = true;
        });
        timer.setRepeats(false);

        // Focusable for key events
        setFocusable(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                requestFocusInWindow();
                keyListenerEnabled = false;
                continueLabel.setVisible(false);
                if (timer.isRunning()) {
                    timer.stop();
                }
                timer.restart();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // This is called when the panel is no longer visible
                continueLabel.setVisible(false);
                keyListenerEnabled = false;
                if (timer.isRunning()) {
                    timer.stop();
                }
            }
        });


        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (keyListenerEnabled && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    AudioManager.stopButton();
                    AudioManager.playButton();
                    CardLayout cl = (CardLayout) screenManager.getLayout();
                    cl.show(screenManager, "TitleScreen"); // Make sure "NameInput" is the correct ID
                }

            }
        });


        // Add warning panel to the center of the main panel
        add(warningPanel, BorderLayout.CENTER);



    }

    public void updateWarning() {
        contentArea.setText("This game contains content that may be disturbing to some players, including:\n\n" +
                "• Flashing images and sudden visual effects\n" +
                "• Disturbing imagery\n" +
                "• Jump scares\n" +
                "• Loud sounds\n" +
                "• Themes related to anxiety and stress\n\n" +
                "Player discretion is advised. If you are sensitive to such content, " +
                "please consider if this game is appropriate for you.\n\n" +
                "You have been warned, " + NameInputPanel.playerName + "!");
    }
}
