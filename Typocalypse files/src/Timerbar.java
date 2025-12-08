import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Timerbar extends JPanel {
   private JProgressBar bar;
   public static Timer timer;
   private long startTime;
   private long durationMillis;
   public static Runnable onTimerEnd;
   public static int bonusSeconds = 0;
   public static double progress;

    // Flags to ensure sounds play only once
   public static boolean heartbeat1Started = false; // Tracks if Heartbeat1 has started
   public static boolean heartbeat2Started = false; // Tracks if Heartbeat2
   public static boolean heartbeat3Started = false;
   public static boolean riserPlayed = false;      // Tracks if Riser has been triggered
   public static boolean bgm2Started = false;

   public Timerbar(long durationMillis) {
      this.durationMillis = durationMillis;
      setLayout(new BorderLayout());
   
      bar = new JProgressBar(0, 1000);
      bar.setValue(1000);
      bar.setForeground(Color.WHITE);
      bar.setBackground(Color.DARK_GRAY);
      bar.setBorderPainted(false);
      bar.setPreferredSize(new Dimension(1550, 50));
   
      add(bar, BorderLayout.CENTER);
   }

   public void start() {
      startTime = System.currentTimeMillis();
   
        // Reset sound flags before starting the timer
      heartbeat1Started = false;
      heartbeat2Started = false;
      riserPlayed = false;
      bgm2Started = false;
   
      if (timer != null && timer.isRunning()) {
         timer.stop(); // Stop any existing timer to avoid conflicts
      }
   
      timer = new Timer(20, 
         (ActionEvent e) -> {
            long elapsed = System.currentTimeMillis() - startTime;
            progress = Math.max(0, 1 - (double) elapsed / durationMillis); // 1.0 = full, 0.0 = empty
         
            // Update the progress bar
            int barValue = (int) (progress * 1000); // Scale progress to 0-1000
            bar.setValue(barValue);
            bar.setForeground(getTransitionColor(progress)); // Update color based on progress

            // Overlay fade effect when approaching empty
            try {
               // Show overlay when progress <= threshold (20% by default)
               double threshold = 0.20;
               if (GameWindow.overlayPanel != null) {
                  double alpha = 0.0;
                  if (progress <= threshold) {
                     // alpha goes from 0 (at threshold) to 0.85 (at empty)
                     alpha = ((threshold - progress) / threshold) * 0.85;
                  }
                  GameWindow.overlayPanel.setAlpha(alpha);
               }
            } catch (Throwable t) {
               // Ignore if overlay not present or any other error
            }
         
            // Start Heartbeat1 immediately when the timer starts
            if (!heartbeat1Started && !bgm2Started) {
               AudioManager.playHeartbeat1(); // Start looping Heartbeat1
               AudioManager.playBGM2();
               heartbeat1Started = true;
            }
         
            //At 50% progress, stop Heartbeat1 and play Heartbeat2
            if (progress <= 0.5 && !heartbeat2Started) {
               AudioManager.stopHeartbeat1();
               AudioManager.playHeartbeat2();
               heartbeat2Started = true;
            }
         
            // At 20% progress, stop Heartbeat2 and play Heartbeat3 + Riser
            if (progress <= 0.2 && !heartbeat3Started && !riserPlayed) {
               AudioManager.stopHeartbeat2(); // Stop the looping Heartbeat1
               AudioManager.playHeartbeat3(); // Play the one-shot Heartbeat2
               AudioManager.playRiser();      // Play the Riser sound
               riserPlayed = true; // Only play Riser once
               heartbeat3Started = true;
            }
         
            // Stop timer and all sounds at the end
            if (progress <= 0 || TypingLogic.totalSentences == 25) {
               timer.stop();
               AudioManager.stopAllSounds();// Stop all active sounds
               heartbeat3Started = false;
               heartbeat2Started = false;
               heartbeat1Started = false;
               riserPlayed = false;
               bgm2Started = false;
               // Reset overlay when timer ends
               try {
                  if (GameWindow.overlayPanel != null) {
                     GameWindow.overlayPanel.setAlpha(0.0);
                  }
               } catch (Throwable t) {
                  // ignore if overlay not available
               }
               if (onTimerEnd != null) {
                  onTimerEnd.run(); // Trigger the end-of-timer callback
               }
            }
         });
   
      timer.start(); // Start the Swing timer
   }

   public void reset() {
        // Stop existing timer if running
      if (timer != null && timer.isRunning()) {
         timer.stop();
      }
      bar.setValue(1000);
      bar.setForeground(getTransitionColor(1.0)); // Set full progress to white
      // Clear overlay when resetting the timer
      try {
         if (GameWindow.overlayPanel != null) {
            GameWindow.overlayPanel.setAlpha(0.0);
         }
      } catch (Throwable t) {
         // ignore
      }
      AudioManager.stopAllSounds(); // Stop any playing sounds when resetting
      heartbeat3Started = false;
      heartbeat2Started = false;
      heartbeat1Started = false;
      riserPlayed = false;
      bgm2Started = false;
      start(); // Restart the timer
   }

   public void deductTime(long millis) {
        // Reduce start time, effectively "fast-forwarding" the timer
      startTime -= millis;
   }

   public void setOnTimerEnd(Runnable onTimerEnd) {
      Timerbar.onTimerEnd = onTimerEnd;
   }

   private Color getTransitionColor(double progress) {
      progress = Math.max(0, Math.min(1, progress)); // Clamp progress to [0,1]
      int red = 255;
      int green = (int) (255 * progress);
      int blue = (int) (255 * progress);
      return new Color(red, green, blue);
   }

   public long getDurationMillis() {
      return durationMillis;
   }

   public void setDurationMillis(long durationMillis) {
      this.durationMillis = durationMillis;
   }


}