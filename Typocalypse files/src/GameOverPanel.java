import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class GameOverPanel extends JPanel {

   private JLabel wpmLabel;
   private JLabel sentencesLabel;
   private JLabel minutesLabel;
   private PlayPanel playPanel;
   private LeaderboardPanel leaderboardPanel;
   private JLabel gameOverLabel;
   private JPanel playerInfoPanel;
   private JPanel bottomPanel;
   private JLabel restartLabel;


   public GameOverPanel(JPanel screenManager, PlayPanel playPanel, LeaderboardPanel leaderboardPanel) {
      this.playPanel = playPanel;
      this.leaderboardPanel = leaderboardPanel;
      setLayout(new BorderLayout());
      setBackground(Color.BLACK);
   
      // Hide mouse cursor
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Image blankCursor = toolkit.createImage(new byte[0]);
      Cursor invisibleCursor = toolkit.createCustomCursor(blankCursor, new Point(0, 0), "invisible");
      setCursor(invisibleCursor);
   
      // ===== CENTER PANEL (Game Over + Stats) =====
      JPanel centerPanel = new JPanel(new GridBagLayout());
      centerPanel.setOpaque(false);
      add(centerPanel, BorderLayout.CENTER);
   
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.insets = new Insets(10, 10, 10, 10);
      gbc.anchor = GridBagConstraints.CENTER;
   
      gameOverLabel = new JLabel("GAME OVER");
      gameOverLabel.setForeground(Color.WHITE);
      gameOverLabel.setFont(new Font("VCR OSD Mono", Font.PLAIN, 130));
      centerPanel.add(gameOverLabel, gbc);
   
      gbc.gridy++;
      playerInfoPanel = new JPanel(new GridBagLayout());
      playerInfoPanel.setOpaque(false);
      playerInfoPanel.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(Color.WHITE, 5, true),
             BorderFactory.createEmptyBorder(30, 60, 30, 60)
         ));
   
      GridBagConstraints infoGbc = new GridBagConstraints();
      infoGbc.gridx = 0;
      infoGbc.gridy = 0;
      infoGbc.insets = new Insets(10, 0, 10, 0);
      infoGbc.anchor = GridBagConstraints.CENTER;
   
      wpmLabel = new JLabel("Words Per Minute: " + TypingLogic.currentWPM);
      wpmLabel.setForeground(Color.WHITE);
      wpmLabel.setFont(new Font("VCR OSD Mono", Font.PLAIN, 45));
      playerInfoPanel.add(wpmLabel, infoGbc);
   
      infoGbc.gridy++;
      sentencesLabel = new JLabel("Completed Sentences: " + TypingLogic.totalSentences);
      sentencesLabel.setForeground(Color.WHITE);
      sentencesLabel.setFont(new Font("VCR OSD Mono", Font.PLAIN, 45));
      playerInfoPanel.add(sentencesLabel, infoGbc);
   
      infoGbc.gridy++;
      minutesLabel = new JLabel("Minute(s) Survived: " + TypingLogic.minutesSurvived);
      minutesLabel.setForeground(Color.WHITE);
      minutesLabel.setFont(new Font("VCR OSD Mono", Font.PLAIN, 45));
      playerInfoPanel.add(minutesLabel, infoGbc);
   
      centerPanel.add(playerInfoPanel, gbc);
   
      // ===== BOTTOM PANEL (Retry Prompt) =====
      bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      bottomPanel.setBackground(Color.BLACK);
      bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0)); // Adds vertical spacing
   
      restartLabel = new JLabel("- PRESS ENTER TO RETRY -");
      restartLabel.setForeground(Color.WHITE);
      restartLabel.setFont(new Font("VCR OSD Mono", Font.PLAIN, 30));
      bottomPanel.add(restartLabel);
   
      add(bottomPanel, BorderLayout.SOUTH);
   
      // Focusable for key events
      setFocusable(true);
      addComponentListener(
         new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
               requestFocusInWindow();
               // Update restart label based on survive state
               if (TypingLogic.totalSentences >= PlayPanel.TARGET_SENTENCES) {
                  restartLabel.setText("- PRESS ENTER TO REVEAL THE ENDING -");
               } else {
                  restartLabel.setText("- PRESS ENTER TO RETRY -");
               }
               // Ensure cursor is hidden on game over
               try {
                  Toolkit toolkit = Toolkit.getDefaultToolkit();
                  Image blankCursor = toolkit.createImage(new byte[0]);
                  Cursor invisibleCursor = toolkit.createCustomCursor(blankCursor, new Point(0, 0), "invisible");
                  java.awt.Window w = javax.swing.JFrame.getFrames().length > 0 ? javax.swing.JFrame.getFrames()[0] : null;
                  if (w != null) w.setCursor(invisibleCursor);
               } catch (Throwable t) {}
            }
         });
   
      addKeyListener(
         new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
               if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                  AudioManager.stopButton();
                  AudioManager.playButton();
                  CardLayout cl = (CardLayout) screenManager.getLayout();

                  if (TypingLogic.totalSentences < PlayPanel.TARGET_SENTENCES) {
                     resetGame();
                     cl.show(screenManager, "NameInput"); // Make sure "NameInput" is the correct ID
                  } else {
                     // Get StoryPanel instance and set it to show survived story
                     Component[] components = screenManager.getComponents();
                     for (Component component : components) {
                           if (component instanceof StoryPanel) {
                              StoryPanel storyPanel = (StoryPanel) component;
                              storyPanel.playSurvivedStory();
                              cl.show(screenManager, "Story");
                              // Don't reset game here - it will be reset by StoryPanel after story completes
                              break;
                           }
                     }
                  }
               }
            }
         });
   }

   // Update method for game stats
   public void setStats(int currentWPM, int totalSentences, double minutesSurvived) {
      wpmLabel.setText("Words Per Minute: " + currentWPM);
      sentencesLabel.setText("Completed Sentences: " + totalSentences);
   
      DecimalFormat df = new DecimalFormat("#.##");
      minutesLabel.setText("Minute(s) Survived: " + df.format(minutesSurvived));
   
      if (totalSentences >= PlayPanel.TARGET_SENTENCES) {
         congratulations();
      } else {
         gameover();
      }
   }

   private void congratulations() {
      setBackground(new Color(240, 248, 255));
      gameOverLabel.setText("CONGRATULATIONS!");
      gameOverLabel.setForeground(Color.BLACK);
      gameOverLabel.setFont(new Font("VCR OSD Mono", Font.PLAIN, 90));
   
      playerInfoPanel.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(Color.BLACK, 5, true),
             BorderFactory.createEmptyBorder(30, 60, 30, 60)
         ));
   
      wpmLabel.setForeground(Color.BLACK);
      sentencesLabel.setForeground(Color.BLACK);
      minutesLabel.setForeground(Color.BLACK);
   
      bottomPanel.setBackground(new Color(240, 248, 255));
      restartLabel.setForeground(Color.BLACK);
   }

   private void gameover() {
      setBackground(Color.BLACK);
      gameOverLabel.setText("GAME OVER");
      gameOverLabel.setForeground(Color.WHITE);
      gameOverLabel.setFont(new Font("VCR OSD Mono", Font.PLAIN, 130));
   
      playerInfoPanel.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(Color.WHITE, 5, true),
             BorderFactory.createEmptyBorder(30, 60, 30, 60)
         ));
   
      wpmLabel.setForeground(Color.WHITE);
      sentencesLabel.setForeground(Color.WHITE);
      minutesLabel.setForeground(Color.WHITE);
   
      bottomPanel.setBackground(Color.BLACK);
      restartLabel.setForeground(Color.WHITE);
   }

   // Add a new method specifically for saving to leaderboard
   public void saveToLeaderboard() {
      if (leaderboardPanel != null) {
         // Get the current stats from UI or from the TypingLogic static variables
         int currentWPM = TypingLogic.currentWPM;
         int totalSentences = TypingLogic.totalSentences;
         double minutesSurvived = TypingLogic.minutesSurvived;
      
         // Only update if we have meaningful stats
         if (currentWPM > 0 || totalSentences > 0 || minutesSurvived > 0) {
            leaderboardPanel.updateLeaderboard(
                   NameInputPanel.playerName,
                   currentWPM,
                   totalSentences,
                   minutesSurvived
               );
         }
      }
   }


   // Update your resetGame method in GameOverPanel
   private void resetGame() {
      // Reset static variables
      TypingLogic.totalSentences = 0;
      TypingLogic.currentWPM = 0;
      TypingLogic.currentIndex = 0;
      TypingLogic.minutesSurvived = 0;
   
      // Explicitly reset the leaderboard flag here
      if (leaderboardPanel != null) {
         leaderboardPanel.resetSavedFlag();
         System.out.println("Flag reset in GameOverPanel.resetGame()");
      }
   
      // Update stats display
      playPanel.updateStats();
   
      // Call the new restart method that properly resets the game
      playPanel.restartGame();
   }



}
