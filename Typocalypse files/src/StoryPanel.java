import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StoryPanel extends JPanel {
    private final JPanel screenManager;
    private int index = 0;
    private final JLabel lineLabel;
    private boolean showingSurvived = false; // 🩶 track if this is the ending story

    // 🕹 Intro story
    private final String[] introStoryLines = {
        "Five days ago, I got a strange email...",
        "No sender, no subject - just a game called <font color='red'>TYPOCALYPSE</font>.",
        "I opened it, and the screen went dark.",
        "Numerous words started appearing.",
        "The game demanded me to type them, one by one.",
        "I had no choice but to comply.",
        "If I stop typing... my life is <font color='red'>over</font>."
    };

    // 🎯 Survived ending story
    private final String[] survivedStoryLines = {
        "You open your eyes to a blinding white light...",
        "A message flashes on the screen, congratulating you for beating the game.",
        "After 5 days of constant typing - it's finally over.",
        "Your reflection stares back from the monitor, tired and still.",
        "But then...",
        "A new notification appears on the screen.",
        "Your eyes widen as you read the message.",
        "<font color='red'>\"Round 2: Are you ready?\"</font>"
    };

    public StoryPanel(JPanel screenManager, PlayPanel playPanel) {
        this.screenManager = screenManager;

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Story text
        lineLabel = new JLabel("", SwingConstants.CENTER);
        lineLabel.setForeground(Color.WHITE);
        lineLabel.setFont(new Font("VCR OSD Mono", Font.PLAIN, 44));
        lineLabel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        add(lineLabel, BorderLayout.CENTER);

        // Hint text at the bottom
        JLabel hint = new JLabel("[Press ENTER or click to continue]", SwingConstants.CENTER);
        hint.setForeground(new Color(180, 180, 180));
        hint.setFont(new Font("VCR OSD Mono", Font.PLAIN, 23));
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
        add(hint, BorderLayout.SOUTH);

        // Key binding (ENTER)
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "next");
        am.put("next", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AudioManager.stopButton();
                AudioManager.playButton();
                advance();
            }
        });

        // Mouse click to advance
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AudioManager.stopButton();
                AudioManager.playButton();
                advance();
            }
        });

        // Reset story when shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                index = 0;
                lineLabel.setText("");
                // preserve current story mode (intro or survived)
                SwingUtilities.invokeLater(() -> showLine());
            }
        });
    }

    // 🧠 Display current line
    private void showLine() {
        String[] lines = showingSurvived ? survivedStoryLines : introStoryLines;
        if (index >= lines.length) index = lines.length - 1;

        lineLabel.setText("<html><div style='text-align:center;'>" + lines[index] + "</div></html>");
    }

    // 🕹 Go to next line or screen
    private void advance() {
        String[] lines = showingSurvived ? survivedStoryLines : introStoryLines;
        index++;

        if (index >= lines.length) {
            CardLayout cl = (CardLayout) screenManager.getLayout();

            if (showingSurvived) {
                // After survived story ends → reset game state and show name input
                resetGameState();
                showingSurvived = false; // Reset back to intro mode for next time
                cl.show(screenManager, "NameInput");
            } else {
                // After intro story ends → show next screen (like guidelines)
                cl.show(screenManager, "Guidelines");
            }
        } else {
            showLine();
        }
    }
    
    // Reset game state after survived mode
    private void resetGameState() {
        PlayPanel.minutesSurvived = 0;
        TypingLogic.totalSentences = 0;
        TypingLogic.currentWPM = 0;
        TypingLogic.currentIndex = 0;
        TypingLogic.minutesSurvived = 0;
        TypingLogic.sentenceCount = 0;
        Timerbar.bonusSeconds = 0;
        
        // Find PlayPanel and update the UI counter
        for (Component comp : screenManager.getComponents()) {
            if (comp instanceof PlayPanel) {
                PlayPanel playPanel = (PlayPanel) comp;
                playPanel.updateCompletedSentencesDisplay(0);
                break;
            }
        }
    }

    // 🎯 Called when the player survives
    public void playSurvivedStory() {
        showingSurvived = true;
        index = 0;
        lineLabel.setText("");
        SwingUtilities.invokeLater(this::showLine);
    }
}
