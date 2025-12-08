import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class JumpscarePanel extends JPanel {
   private final JPanel screenManager;
   private GameOverPanel gameOverPanel;
   private JLabel jumpscareLabel;


   // Paired image and sound assets
   private final List<JumpscareAsset> jumpscareAssets = List.of(
          new JumpscareAsset("rsc/JumpscareAssets/Jumpscare.png", "rsc/JumpscareAssets/Scream.wav"),
          new JumpscareAsset("rsc/JumpscareAssets/Jumpscare2.png", "rsc/JumpscareAssets/Scream2.wav"),
          new JumpscareAsset("rsc/JumpscareAssets/Jumpscare3.png", "rsc/JumpscareAssets/Scream3.wav") // Add more pairs as needed
   );



   private JumpscareAsset selectedJumpscare;

   public void randomizeJumpscare() {
      Random random = new Random();
      selectedJumpscare = jumpscareAssets.get(random.nextInt(jumpscareAssets.size()));
      // Load the image with multiple fallbacks (classpath -> src/ -> working dir)
      ImageIcon newIcon = loadImageIcon(selectedJumpscare.getImagePath());
      if (newIcon != null && newIcon.getIconWidth() != -1) {
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         Image scaledImage = newIcon.getImage().getScaledInstance(
                screenSize.width,
                screenSize.height,
                Image.SCALE_SMOOTH
            );
         if (jumpscareLabel == null) {
            jumpscareLabel = new JLabel(new ImageIcon(scaledImage));
            jumpscareLabel.setHorizontalAlignment(SwingConstants.CENTER);
            jumpscareLabel.setVerticalAlignment(SwingConstants.CENTER);
            add(jumpscareLabel, BorderLayout.CENTER);
         } else {
            jumpscareLabel.setIcon(new ImageIcon(scaledImage));
         }
         revalidate();
         repaint();
      } else {
         System.err.println("Failed to load image: " + selectedJumpscare.getImagePath());
      }
   }

   public JumpscarePanel(JPanel screenManager, GameOverPanel gameOverPanel) {
      this.gameOverPanel = gameOverPanel;
      this.screenManager = screenManager;
      setLayout(new BorderLayout());
   
      // Randomly select a jumpscare asset (image and sound)
      Random random = new Random();
      selectedJumpscare = jumpscareAssets.get(random.nextInt(jumpscareAssets.size()));
   
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Image blankCursor = toolkit.createImage(new byte[0]);
      Cursor invisibleCursor = toolkit.createCustomCursor(blankCursor, new Point(0, 0), "invisible");
      setCursor(invisibleCursor);
   
      // Load and scale the random image (robustly)
      ImageIcon icon = loadImageIcon(selectedJumpscare.getImagePath());
      if (icon == null || icon.getIconWidth() == -1) {
         System.err.println("Failed to load image: " + selectedJumpscare.getImagePath() + ". Using placeholder.");
         jumpscareLabel = new JLabel("", SwingConstants.CENTER);
         jumpscareLabel.setOpaque(true);
         jumpscareLabel.setBackground(Color.BLACK);
         jumpscareLabel.setForeground(Color.WHITE);
      } else {
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         Image scaledImage = icon.getImage().getScaledInstance(
                screenSize.width,
                screenSize.height,
                Image.SCALE_SMOOTH
            );
         jumpscareLabel = new JLabel(new ImageIcon(scaledImage));
         jumpscareLabel.setHorizontalAlignment(SwingConstants.CENTER);
         jumpscareLabel.setVerticalAlignment(SwingConstants.CENTER);
      }
      add(jumpscareLabel, BorderLayout.CENTER);
   
      // Listener to play sound and switch panel after delay
      this.addComponentListener(
         new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
               // Hide cursor at jumpscare (ensure invisible at frame level)
               try {
                  Toolkit toolkit = Toolkit.getDefaultToolkit();
                  Image blankCursor = toolkit.createImage(new byte[0]);
                  Cursor invisibleCursor = toolkit.createCustomCursor(blankCursor, new Point(0, 0), "invisible");
                  java.awt.Window w = javax.swing.JFrame.getFrames().length > 0 ? javax.swing.JFrame.getFrames()[0] : null;
                  if (w != null) w.setCursor(invisibleCursor);
               } catch (Throwable t) {}
               playScreamSound();
            
            // Auto-switch to the next panel after 2 seconds (2000 ms)
               Timer timer = new Timer(5000, 
                  event -> {
                     gameOverPanel.setStats(TypingLogic.currentWPM, TypingLogic.totalSentences, TypingLogic.minutesSurvived);
                  // Save to leaderboard (only here, not during game restart)
                     gameOverPanel.saveToLeaderboard();
                     CardLayout cl = (CardLayout) screenManager.getLayout();
                     cl.show(screenManager, "GameOver"); // replace with your actual panel name
                  });
               timer.setRepeats(false); // run only once
               timer.start();
            }
         });
   }

   private void playScreamSound() {
      try {
         // Play the associated sound for the selected jumpscare (try multiple locations)
         java.io.InputStream is = getClass().getResourceAsStream("/" + selectedJumpscare.getSoundPath());
         AudioInputStream audioIn = null;
         if (is != null) {
            audioIn = AudioSystem.getAudioInputStream(new java.io.BufferedInputStream(is));
         } else {
            // Try common filesystem locations: src/<path>, <cwd>/<path>
            String rel = selectedJumpscare.getSoundPath();
            File f1 = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + rel);
            File f2 = new File(System.getProperty("user.dir") + File.separator + rel);
            File direct = new File(rel);
            File soundFile = null;
            if (f1.exists()) soundFile = f1;
            else if (f2.exists()) soundFile = f2;
            else if (direct.exists()) soundFile = direct;

            if (soundFile != null) {
               audioIn = AudioSystem.getAudioInputStream(soundFile);
            } else {
               System.err.println("Failed to find scream sound: " + selectedJumpscare.getSoundPath());
               return;
            }
         }
         Clip screamClip = AudioSystem.getClip();
         screamClip.open(audioIn);
         screamClip.start();
      } catch (Exception e) {
         System.err.println("Failed to play scream sound: " + e.getMessage());
      }
   }

   // Helper to load image icons using several fallbacks
   private ImageIcon loadImageIcon(String relativePath) {
      try {
         // 1) Try classpath
         java.net.URL url = getClass().getResource("/" + relativePath);
         if (url != null) return new ImageIcon(url);

         // 2) Try src/ relative to project root
         File f1 = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + relativePath);
         if (f1.exists()) return new ImageIcon(f1.getAbsolutePath());

         // 3) Try working directory + relativePath
         File f2 = new File(System.getProperty("user.dir") + File.separator + relativePath);
         if (f2.exists()) return new ImageIcon(f2.getAbsolutePath());

         // 4) Direct relative path
         File direct = new File(relativePath);
         if (direct.exists()) return new ImageIcon(direct.getAbsolutePath());

         return null;
      } catch (Exception ex) {
         System.err.println("Error loading image " + relativePath + ": " + ex.getMessage());
         return null;
      }
   }


   // Helper class to pair image and sound
   private static class JumpscareAsset {
      private final String imagePath;
      private final String soundPath;
   
      public JumpscareAsset(String imagePath, String soundPath) {
         this.imagePath = imagePath;
         this.soundPath = soundPath;
      }
   
      public String getImagePath() {
         return imagePath;
      }
   
      public String getSoundPath() {
         return soundPath;
      }
   }
}