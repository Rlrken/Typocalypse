import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class TypingLogic {
   private String[] words;
   public static int currentIndex = 0;
   private final JTextField inputField;
   private final JTextPane centerTextPane;
   private final SentenceGenerator sentenceGenerator;
   private final Timerbar timerbar;
   public static int sentenceCount = 0;  // Counter for completed sentences
   public static int totalSentences = 0;
   private int wordsTyped = 0;
   private Instant startTime;
   public static int currentWPM = 0;
   public static double minutesSurvived = 0;
   private final int difficultyThreshold = 5;  // After how many sentences to increase difficulty
   private int TimerDecrease = 5000;      // 5 seconds decrease per sentence in EASY
   private int hardTimerDecrease = 0;          // No decrease in HARD (or set to a value if needed)
   private int currentMaxTimer = 60000; // 60 seconds initial timer
   private PlayPanel playPanel;
   private GameOverPanel gameOverPanel;
   public static Timer wpmTimer;
   private static JDialog dialog;
   private Timer borderResetTimer;
   private Timer incorrectFlashTimer;
   private boolean incorrectFlash = false;

   public TypingLogic(String sentence, JTextField inputField, JTextPane centerTextPane, SentenceGenerator generator, Timerbar timerbar, PlayPanel playPanel, GameOverPanel gameOverPanel) {
      this.inputField = inputField;
      this.centerTextPane = centerTextPane;
      this.sentenceGenerator = generator;
      this.timerbar = timerbar;
      this.playPanel = playPanel;
      this.gameOverPanel = gameOverPanel;
      this.words = sentence.trim().split("\\s+");
      renderText();
      setupInputListener();
   
      wpmTimer = new Timer(1000, event ->
            updateWpmDisplay());
      wpmTimer.start();
   }

   private void setupInputListener() {
      inputField.addKeyListener(
         new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
               if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                  // Remove any trailing whitespace that may have been inserted by the space key
                  String rawText = inputField.getText();
                  if (rawText == null) rawText = "";
                  rawText = rawText.replaceAll("\\s+$", ""); // strip trailing whitespace
                  // Update the field so the user doesn't see the extra space
                  inputField.setText(rawText);
                  String userInput = rawText.trim();
               
                  if (startTime == null) {
                     startTime = Instant.now();
                  }
               
                  if (borderResetTimer != null && borderResetTimer.isRunning()) {
                     borderResetTimer.stop();
                  }
               
                  if (currentIndex < words.length && userInput.equals(words[currentIndex])) {
                     currentIndex++;
                              // If user corrects the word, clear any incorrect flash state
                     if (incorrectFlash) {
                        incorrectFlash = false;
                        if (incorrectFlashTimer != null && incorrectFlashTimer.isRunning()) incorrectFlashTimer.stop();
                     }
                     wordsTyped++;
                     //AudioManager.stopCorrect();
                     //AudioManager.playCorrect();
                     inputField.setText("");
                     centerTextPane.setBorder(new LineBorder(Color.GREEN, 10, true));
                     showRandomDistraction();
                        // Focus on input field to start typing immediately
                     inputField.requestFocusInWindow();
                  
                     if (currentIndex == words.length) {
                            // Sentence completed, generate a new sentence
                        //AudioManager.stopButton();
                        //AudioManager.playButton();
                        //AudioManager.stopCorrect();
                        //AudioManager.playCorrect();
                        String newSentence = sentenceGenerator.generateSentence();
                        words = newSentence.trim().split("\\s+");
                        currentIndex = 0; // Reset index
                     
                            // Increment sentence counter
                        sentenceCount++;
                        totalSentences++;
                     
                     
                        if (Timerbar.progress >= 0.5) {
                           System.out.println("Adding 45 to bonusSeconds (before: " + Timerbar.bonusSeconds + ")");
                           Timerbar.bonusSeconds += 45;
                           System.out.println("bonusSeconds now: " + Timerbar.bonusSeconds);
                        } else if (Timerbar.progress >= 0.2 && Timerbar.progress < 0.5) {
                           System.out.println("Adding 25 to bonusSeconds (before: " + Timerbar.bonusSeconds + ")");
                           Timerbar.bonusSeconds += 25;
                           System.out.println("bonusSeconds now: " + Timerbar.bonusSeconds);
                        } else {
                           System.out.println("Adding 5 to bonusSeconds (before: " + Timerbar.bonusSeconds + ")");
                           Timerbar.bonusSeconds += 5;
                           System.out.println("bonusSeconds now: " + Timerbar.bonusSeconds);
                        }
                     
                     
                        playPanel.updateStats();
                        gameOverPanel.setStats(currentWPM, totalSentences, minutesSurvived);
                     
                            // If the sentence count has reached the threshold, make the timer harder
                        if (sentenceCount % difficultyThreshold == 0) {
                                // Only decrease timer difficulty
                           decreaseTimerDifficulty();
                        } else {
                                    // Decrease the timer for the current sentence
                           decreaseTimerDifficulty();
                        }
                     
                     
                            // Reset the timer
                        timerbar.reset();  // Reset the timer
                     }
                  
                     renderText();
                  } else {
                        // Handle incorrect word typing
                     AudioManager.stopError();
                     AudioManager.playError();
                     System.out.println("Incorrect word: " + userInput);
                     timerbar.deductTime(2000); // Deduct 2 seconds
                     centerTextPane.setBorder(new LineBorder(Color.RED, 10, true));
                     // Flash the current word red for a short duration
                     incorrectFlash = true;
                     if (incorrectFlashTimer != null && incorrectFlashTimer.isRunning()) {
                        incorrectFlashTimer.stop();
                     }
                     incorrectFlashTimer = new Timer(1000, 
                        event -> {
                           incorrectFlash = false;
                           incorrectFlashTimer.stop();
                           renderText();
                        });
                     incorrectFlashTimer.setRepeats(false);
                     incorrectFlashTimer.start();
                  
                     // Immediately update the text display so the red flash is visible
                     renderText();
                  
                     showRandomDistraction();
                     // Focus on input field to start typing immediately
                     inputField.requestFocusInWindow();
                  }
               
                  borderResetTimer = new Timer(1000, 
                     event -> {
                        centerTextPane.setBorder(new LineBorder(Color.WHITE, 10, true));
                        borderResetTimer.stop();
                     });
                  borderResetTimer.setRepeats(false);
                  borderResetTimer.start();
               
               
                  updateWpmDisplay();// Update WPM Display
                  e.consume();  // Prevent space from being typed
               }
            }
         });
   }

   private void updateWpmDisplay() {
      if (startTime == null) {
         return;  // Typing session hasn't started yet
      }
   
        // Calculate the time elapsed in seconds
      Duration timeElapsed = Duration.between(startTime, Instant.now());
      long secondsElapsed = timeElapsed.getSeconds();
   
        // Avoid division by zero
      if (secondsElapsed == 0) {
         return;
      }
   
        // Calculate WPM
      currentWPM = (int) ((wordsTyped / (double) secondsElapsed) * 60);
   
      playPanel.updateStats();
      gameOverPanel.setStats(currentWPM, totalSentences, minutesSurvived);
   }


   private void decreaseTimerDifficulty() {
      SentenceGenerator.Difficulty currentDifficulty = sentenceGenerator.getDifficulty();
   
        // Decrease the maximum timer by 5 seconds (5000ms)
      currentMaxTimer = currentMaxTimer - TimerDecrease;
   
        // Handle different minimums based on difficulty
      if (currentDifficulty == SentenceGenerator.Difficulty.HARD) {
            // In HARD mode, don't let timer go below 30 seconds
         if (currentMaxTimer < 30000) {  // 30 seconds
            currentMaxTimer = 30000;
         }
      } else {
            // For EASY and MEDIUM, if timer reaches 35 seconds
         if (currentMaxTimer <= 35000) {  // 35 seconds
                // Reset timer first
            currentMaxTimer = 60000;  // Reset back to 60 seconds
         
                // Increase difficulty if not already at HARD
            if (currentDifficulty != SentenceGenerator.Difficulty.HARD) {
               SentenceGenerator.Difficulty newDifficulty = sentenceGenerator.increaseDifficulty();
               System.out.println("Difficulty increased to: " + newDifficulty);
            }
         }
      }
   
        // Set the new timer duration
      timerbar.setDurationMillis(currentMaxTimer);
      System.out.println("New timer duration set to: " + (currentMaxTimer / 1000) + " seconds");
   }


   private void showRandomDistraction() {
      SentenceGenerator.Difficulty currentDifficulty = sentenceGenerator.getDifficulty();
      double distractionProbability = 0;
   
      if (currentDifficulty == SentenceGenerator.Difficulty.MEDIUM) {
         distractionProbability = 0.05; // 5% chance for MEDIUM
      } else if (currentDifficulty == SentenceGenerator.Difficulty.HARD) {
         distractionProbability = 0.10; // 10% chance for HARD
      }
   
        // Only proceed if we're on MEDIUM or HARD difficulty
      if (distractionProbability > 0 && Math.random() < distractionProbability) {
            // Randomly choose which distraction to display
         int distractionType = (int)(Math.random() * 2); // Generates 0 or 1
      
         switch (distractionType) {
            case 0:
                    // Original text distraction
               Distraction1();
               break;
            case 1:
                    // New jumpscare distraction
               Distraction2();
                    // Play jumpscare sound
               AudioManager.playDistraction2();
               break;
         }
      }
   }



   private void Distraction1() {
        // Create panel with light black background
      JPanel panel = new JPanel(new BorderLayout(10, 10));
      panel.setBackground(new Color(54, 54, 54)); // Charcoal gray
      panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
   
        // Message label
      JLabel messageLabel = new JLabel("IT SEES YOU!", JLabel.CENTER);
      messageLabel.setFont(new Font("VCR OSD Mono", Font.BOLD, 90));
      messageLabel.setForeground(Color.WHITE);
      messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      panel.add(messageLabel, BorderLayout.CENTER);
   
        // Close button
      JButton closeButton = new JButton("CLICK ME");
      closeButton.setBackground(new Color(75, 75, 75)); // Slightly lighter than background
      closeButton.setForeground(Color.WHITE);
      closeButton.setFocusPainted(false);
      closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
   
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      buttonPanel.setBackground(new Color(54, 54, 54)); // Match panel background
      buttonPanel.add(closeButton);
      panel.add(buttonPanel, BorderLayout.SOUTH);
   
        // Dialog setup
      Frame mainFrame = JFrame.getFrames()[0];  // Get the main application frame
      dialog = new JDialog(mainFrame, "Distraction!", true);  // Make it modal
      dialog.setContentPane(panel);
      dialog.setUndecorated(true);
      dialog.setFocusableWindowState(false);
      dialog.setPreferredSize(new Dimension(900, 400));
      dialog.pack();
      // Position dialog: randomize when in HARD difficulty, otherwise center
      try {
         SentenceGenerator.Difficulty currentDifficulty = sentenceGenerator.getDifficulty();
         if (currentDifficulty == SentenceGenerator.Difficulty.HARD) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle screenBounds = ge.getMaximumWindowBounds();
            Dimension dlgSize = dialog.getSize();
            int maxX = Math.max(0, screenBounds.width - dlgSize.width);
            int maxY = Math.max(0, screenBounds.height - dlgSize.height);
            int x = screenBounds.x + (int) (Math.random() * (maxX + 1));
            int y = screenBounds.y + (int) (Math.random() * (maxY + 1));
            dialog.setLocation(x, y);
         } else {
            dialog.setLocationRelativeTo(null);
         }
      } catch (Throwable t) {
         // Fallback to centered if anything goes wrong
         dialog.setLocationRelativeTo(null);
      }
   
   
        // Make sure the dialog can receive key events
      dialog.setFocusable(true);
      mainFrame.requestFocusInWindow();
      mainFrame.requestFocus();
      dialog.requestFocusInWindow();
      dialog.requestFocus();
      // Create cursors: default (visible) and invisible (for gameplay)
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Image blank = toolkit.createImage(new byte[0]);
      Cursor invisibleCursor = toolkit.createCustomCursor(blank, new Point(0, 0), "invisible");
      Cursor defaultCursor = Cursor.getDefaultCursor();
   
      // Make the cursor visible for this dialog
      try {
         mainFrame.setCursor(defaultCursor);
         dialog.setCursor(defaultCursor);
      } catch (Throwable t) {
         // ignore
      }
   
      AudioManager.playDistraction();
   
      closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      closeButton.addActionListener(
         e -> {
            dialog.dispose();
            AudioManager.stopDistraction();
            // restore invisible cursor when dialog is closed
            try {
               mainFrame.setCursor(invisibleCursor);
            } catch (Throwable t) {
               // ignore
            }
         });
   
      // Show dialog (modal) — when it returns, ensure cursor is hidden again
      dialog.setVisible(true);
      try {
         mainFrame.setCursor(invisibleCursor);
      } catch (Throwable t) {
         // ignore
      }
   }

   public static void disposeDistraction() {
      if (dialog != null) {
         dialog.dispose();
         AudioManager.stopDistraction();
         dialog = null;
      }
   }

   private void Distraction2() {
      SwingUtilities.invokeLater(
         () -> {
            // Load the jumpscare image
            // Load the image from the file system
            ImageIcon jumpscareIcon;
            try {
               String imagePath = "rsc/JumpscareAssets/MiniJumpscare.png";
               URL url = TypingLogic.class.getResource("/" + imagePath);
               if (url != null) {
                  jumpscareIcon = new ImageIcon(url);
               } else {
                  File imageFile = new File(imagePath);
                  if (!imageFile.exists()) {
                     throw new IOException("File not found: " + imageFile.getAbsolutePath());
                  }
                  jumpscareIcon = new ImageIcon(imageFile.getAbsolutePath());
               }
            } catch (Exception e) {
               System.err.println("Failed to load jumpscare image: " + e.getMessage());
               return;
            }
         
            // Create panel with black background
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.BLACK);
         
            // Add image label with scaled icon
            JLabel imageLabel = 
               new JLabel() {
                  @Override
                  protected void paintComponent(Graphics g) {
                     super.paintComponent(g);
                     g.drawImage(jumpscareIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                  }
               };
            panel.add(imageLabel, BorderLayout.CENTER);
         
            // Create full-screen undecorated dialog
            Frame mainFrame = JFrame.getFrames()[0];  // Get the main application frame
            JDialog dialog = new JDialog();
            dialog.setFocusableWindowState(false);
            dialog.setUndecorated(true);
            dialog.setContentPane(panel);
            dialog.setAlwaysOnTop(true);
            dialog.setFocusable(true);
            mainFrame.requestFocusInWindow();
            mainFrame.requestFocus();
            dialog.requestFocusInWindow();
            dialog.requestFocus();
         
            // Set to full screen using screen bounds
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle screenBounds = ge.getMaximumWindowBounds();
            dialog.setBounds(screenBounds);
         
         
         
            // Auto-close after 0.5 seconds
            Timer autoCloseTimer = new Timer(500, e -> dialog.dispose());
            autoCloseTimer.setRepeats(false);
            autoCloseTimer.start();
         
            // Show the dialog
            dialog.setVisible(true);
            inputField.requestFocusInWindow();
         });
   }

   private void renderText() {
      StyledDocument doc = centerTextPane.getStyledDocument();
      doc.setCharacterAttributes(0, doc.getLength(), centerTextPane.getStyle(StyleContext.DEFAULT_STYLE), true);
      centerTextPane.setText("");
   
      for (int i = 0; i < words.length; i++) {
         SimpleAttributeSet attr = new SimpleAttributeSet();
         if (i == currentIndex) {
            // If the last attempt was incorrect, show current word in red temporarily
            if (incorrectFlash) {
               StyleConstants.setForeground(attr, Color.RED);
            } else {
               StyleConstants.setForeground(attr, Color.WHITE); // Current word
            }
            StyleConstants.setBold(attr, true);
         } else {
            StyleConstants.setForeground(attr, Color.GRAY); // Upcoming words
         }
      
         try {
            doc.insertString(doc.getLength(), words[i], attr);
            if (i < words.length - 1) {
               doc.insertString(doc.getLength(), " ", null); // Add space between words
            }
         } catch (BadLocationException ex) {
            ex.printStackTrace();
         }
      }
   
        // Center-align paragraph
      SimpleAttributeSet centerStyle = new SimpleAttributeSet();
      StyleConstants.setAlignment(centerStyle, StyleConstants.ALIGN_CENTER);
      doc.setParagraphAttributes(0, doc.getLength(), centerStyle, false);
   }

}
