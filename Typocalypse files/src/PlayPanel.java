import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class PlayPanel extends JPanel {
   private Timerbar timerbar;
   private JTextField inputField;
   private JLabel CompletedSentences;
   private JLabel WPM;
   private JTextPane centerTextPane;
   public static JumpscarePanel jumpscarePanel;
   public static GameOverPanel gameOverPanel;
   public static LeaderboardPanel leaderboardPanel;
   public static double minutesSurvived = 0;
   public static long totalMinutesStart;
   public static long totalMinutesEnd;
   private JPanel screenManager;

   // 🎯 Target sentences to automatically trigger survival
   public static final int TARGET_SENTENCES = 25;

   public PlayPanel(JPanel screenManager) {
      this.screenManager = screenManager;
      setLayout(new BorderLayout());
      setBackground(Color.BLACK);
   
      // Hide cursor globally
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Image blankCursor = toolkit.createImage(new byte[0]);
      Cursor invisibleCursor = toolkit.createCustomCursor(blankCursor, new Point(0, 0), "invisible");
      setCursor(invisibleCursor);
      try {
         java.awt.Window mainWindow = javax.swing.JFrame.getFrames().length > 0 ? javax.swing.JFrame.getFrames()[0] : null;
         if (mainWindow != null) mainWindow.setCursor(invisibleCursor);
      } catch (Throwable t) {}
      addComponentListener(
         new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
               try {
                  java.awt.Window w = javax.swing.JFrame.getFrames().length > 0 ? javax.swing.JFrame.getFrames()[0] : null;
                  if (w != null) w.setCursor(invisibleCursor);
               } catch (Throwable t) {}
            }
         });
   
      // Timer bar setup
      this.timerbar = new Timerbar(60000);
      timerbar.setOnTimerEnd(this::handleTimerEnd);
      add(timerbar, BorderLayout.NORTH);
   
      // Center UI
      JPanel centerWrapper = new JPanel(new GridBagLayout());
      centerWrapper.setOpaque(false);
      centerWrapper.setBorder(BorderFactory.createEmptyBorder(80, 10, 60, 10));
      add(centerWrapper, BorderLayout.CENTER);
   
      GridBagConstraints gbc1 = new GridBagConstraints();
      gbc1.gridx = 1; gbc1.gridy = 0;
      gbc1.anchor = GridBagConstraints.EAST;
      CompletedSentences = new JLabel("Completed Sentences: " + TypingLogic.totalSentences);
      CompletedSentences.setForeground(Color.WHITE);
      CompletedSentences.setFont(new Font("VCR OSD Mono", Font.PLAIN, 38));
      gbc1.insets = new Insets(0, 0, 30, 0);
      centerWrapper.add(CompletedSentences, gbc1);
   
      GridBagConstraints gbc2 = new GridBagConstraints();
      gbc2.gridx = 0; gbc2.gridy = 0;
      gbc2.anchor = GridBagConstraints.EAST;
      WPM = new JLabel("WPM: " + TypingLogic.currentWPM);
      WPM.setForeground(Color.WHITE);
      WPM.setFont(new Font("VCR OSD Mono", Font.PLAIN, 38));
      gbc2.insets = new Insets(0, 0, 30, 0);
      centerWrapper.add(WPM, gbc2);
   
      GridBagConstraints gbctextpane = new GridBagConstraints();
      gbctextpane.gridy = 1;
      gbctextpane.gridwidth = 2;
      gbctextpane.anchor = GridBagConstraints.CENTER;
   
      centerTextPane = new JTextPane();
      centerTextPane.setPreferredSize(new Dimension(1200, 400));
      centerTextPane.setBackground(Color.BLACK);
      centerTextPane.setBorder(new LineBorder(Color.WHITE, 10, true));
      centerTextPane.setFont(new Font("VCR OSD Mono", Font.PLAIN, 55));
      centerTextPane.setForeground(Color.WHITE);
      centerTextPane.setEditable(false);
      centerTextPane.setFocusable(false);
      centerTextPane.setOpaque(true);
      centerTextPane.setMargin(new Insets(20, 20, 20, 20));
      centerWrapper.add(centerTextPane, gbctextpane);
   
      // Bottom input
      JPanel bottomPanel = new JPanel(new GridBagLayout());
      bottomPanel.setOpaque(false);
      bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));
      add(bottomPanel, BorderLayout.SOUTH);
   
      this.inputField = new JTextField(25);
      inputField.setFont(new Font("VCR OSD Mono", Font.PLAIN, 40));
      inputField.setPreferredSize(new Dimension(600, 80));
      SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
      bottomPanel.add(inputField);
   
      // Sentence generator
      SentenceGenerator sentenceGenerator = new SentenceGenerator();
      String sentence = sentenceGenerator.generateSentence();
   
      // Use class variable (not local)
      gameOverPanel = new GameOverPanel(screenManager, this, leaderboardPanel);
   
      // Typing logic setup
      new TypingLogic(sentence, inputField, centerTextPane, sentenceGenerator, timerbar, this, gameOverPanel);
   }

   /**
    * Called when the timer naturally runs out
    */
   private void handleTimerEnd() {
      TypingLogic.wpmTimer.stop();
      totalMinutesEnd = System.currentTimeMillis();
      TypingLogic.disposeDistraction();
   
      long totalMilliseconds = (Timerbar.bonusSeconds * 1000) + (totalMinutesEnd - totalMinutesStart);
      minutesSurvived = totalMilliseconds / 60000.0;
      TypingLogic.minutesSurvived = minutesSurvived;
   
      gameOverPanel.setStats(TypingLogic.currentWPM, TypingLogic.totalSentences, minutesSurvived);
   
      CardLayout cl = (CardLayout) screenManager.getLayout();
   
      if (TypingLogic.totalSentences >= TARGET_SENTENCES) {
         showSurvivedScreen();
      } else {
         showJumpscareScreen();
      }
   }

   /**
    * Called every time player finishes a sentence
    */
   public void updateStats() {
      CompletedSentences.setText("Completed Sentences: " + TypingLogic.totalSentences);
      WPM.setText("WPM: " + TypingLogic.currentWPM);
   
      // 🎯 Automatically trigger survival screen when reaching target
      if (TypingLogic.totalSentences >= TARGET_SENTENCES) {
         Timerbar.timer.stop(); // stop timer early
         handleTimerEnd(); // trigger survival logic immediately
      }
   }

   /**
    * Displays YOU SURVIVED screen
    */
   private void showSurvivedScreen() {
      CardLayout cl = (CardLayout) screenManager.getLayout();
   
      JPanel whitePanel = new JPanel(new BorderLayout());
      whitePanel.setBackground(Color.WHITE);
   
      JLabel greet = new JLabel("YOU SURVIVED!");
      greet.setForeground(Color.BLACK);
      greet.setFont(new Font("VCR OSD Mono", Font.BOLD, 100));
      greet.setHorizontalAlignment(SwingConstants.CENTER);
      whitePanel.add(greet, BorderLayout.CENTER);
   
      AudioManager.stopCongratulations();
      AudioManager.playCongratulations();
   
      screenManager.add(whitePanel, "whiteScreen");
      gameOverPanel.saveToLeaderboard();
      cl.show(screenManager, "whiteScreen");
   
      Timer delayTimer = new Timer(3000, 
         e -> {
            cl.show(screenManager, "GameOver");
         // No longer automatically transitioning to Story panel
         // The transition will only happen when user presses Enter in GameOverPanel
            ((Timer) e.getSource()).stop();
         });
      delayTimer.setRepeats(false);
      delayTimer.start();
   }

   /**
    * Displays jumpscare sequence
    */
   private void showJumpscareScreen() {
      CardLayout cl = (CardLayout) screenManager.getLayout();
   
      JPanel blackPanel = new JPanel();
      blackPanel.setBackground(Color.BLACK);
   
      screenManager.add(blackPanel, "blackScreen");
      AudioManager.stopPowerOff();
      AudioManager.playPowerOff();
   
      cl.show(screenManager, "blackScreen");
   
      Timer delayTimer = new Timer(5000, 
         e -> {
            jumpscarePanel.randomizeJumpscare();
            cl.show(screenManager, "Jumpscare");
            ((Timer) e.getSource()).stop();
         });
      delayTimer.setRepeats(false);
      delayTimer.start();
   }

   public void startGame() {
      Timerbar.bonusSeconds = 0;
      totalMinutesStart = System.currentTimeMillis();
      timerbar.start();
      inputField.requestFocusInWindow();
   }

   public void restartGame() {
      inputField.setText("");
      minutesSurvived = 0;
      TypingLogic.totalSentences = 0;
      TypingLogic.currentWPM = 0;
      TypingLogic.currentIndex = 0;
      TypingLogic.minutesSurvived = 0;
   
      updateCompletedSentencesDisplay(0);
      WPM.setText("WPM: 0");
   
      for (KeyListener kl : inputField.getKeyListeners()) {
         inputField.removeKeyListener(kl);
      }
   
      SentenceGenerator generator = new SentenceGenerator();
      String newSentence = generator.generateSentence();
      centerTextPane.setText(newSentence);
   
      new TypingLogic(
             newSentence,
             inputField,
             centerTextPane,
             generator,
             timerbar,
             this,
             gameOverPanel
         );
   
      Timerbar.bonusSeconds = 0;
      TypingLogic.sentenceCount = 0;
      inputField.requestFocusInWindow();
   }
   
   // Method to update the completed sentences display
   public void updateCompletedSentencesDisplay(int count) {
      CompletedSentences.setText("Completed Sentences: " + count);
   }
}
