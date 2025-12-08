
import javax.swing.*;
import java.awt.*;

public class GameWindow {
   // Overlay panel instance accessible to other classes (e.g., Timerbar)
   public static OverlayPanel overlayPanel;

   public static void main(String[] args) {
      SwingUtilities.invokeLater(
         () -> {
            JFrame frame = new JFrame("Typocalypse");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
         
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            gd.setFullScreenWindow(frame);
         
            // Use a layered pane so we can place an overlay above the UI
            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setLayout(null);
         
            //Main panel
            JPanel screenManager = new JPanel(new CardLayout());
         
            LeaderboardPanel leaderboardPanel = new LeaderboardPanel(screenManager);
         
            // Pass playPanel to GuidelinesPanel
            PlayPanel playPanel = new PlayPanel(screenManager);
            GuidelinesPanel guidelinesPanel = new GuidelinesPanel(screenManager, playPanel);
         
            TitleScreenPanel titleScreenPanel = new TitleScreenPanel(screenManager);
            TriggerWarningPanel triggerWarningPanel = new TriggerWarningPanel(screenManager);
         
            NameInputPanel nameInputPanel = new NameInputPanel(screenManager, titleScreenPanel, triggerWarningPanel, leaderboardPanel);
         
            // Pass gameOverPanel to JumpscarePanel
            GameOverPanel gameOverPanel = new GameOverPanel(screenManager, playPanel, leaderboardPanel);
            JumpscarePanel jumpscarePanel = new JumpscarePanel(screenManager, gameOverPanel);
            // Story panel shown before the Play panel
            StoryPanel storyPanel = new StoryPanel(screenManager, playPanel);
         
            // Add to card layout
            screenManager.add(nameInputPanel, "NameInput");
            screenManager.add(triggerWarningPanel, "TriggerWarning");
            screenManager.add(titleScreenPanel, "TitleScreen");
            screenManager.add(playPanel, "Play");
            screenManager.add(storyPanel, "Story");
            screenManager.add(leaderboardPanel, "Leaderboard");
            screenManager.add(jumpscarePanel, "Jumpscare");
            screenManager.add(guidelinesPanel, "Guidelines");
            screenManager.add(gameOverPanel, "GameOver");
         
            // Assign to static reference
            PlayPanel.jumpscarePanel = jumpscarePanel;
            PlayPanel.gameOverPanel = gameOverPanel;
         
            // Add screenManager to layered pane as the base layer
            layeredPane.add(screenManager, JLayeredPane.DEFAULT_LAYER);
         
            // Create and add overlay panel on top (initially transparent)
            overlayPanel = new OverlayPanel();
            overlayPanel.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
            layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);
         
            // Resize children when frame resizes
            frame.addComponentListener(
               new java.awt.event.ComponentAdapter() {
                  @Override
                  public void componentResized(java.awt.event.ComponentEvent e) {
                     Dimension s = frame.getSize();
                     screenManager.setBounds(0, 0, s.width, s.height);
                     overlayPanel.setBounds(0, 0, s.width, s.height);
                  }
               });
         
            // Initialize bounds to current size
            Dimension initial = Toolkit.getDefaultToolkit().getScreenSize();
            screenManager.setBounds(0, 0, initial.width, initial.height);
         
            frame.setContentPane(layeredPane);
            frame.setVisible(true);
         });
   }
}
