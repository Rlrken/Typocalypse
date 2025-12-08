import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;

public class GuidelinesPanel extends JPanel {
   private PlayPanel playPanel;
   private JPanel screenManager;
   private boolean ListenerEnabled = false;

   public GuidelinesPanel(JPanel screenManager, PlayPanel playPanel) {
      this.screenManager = screenManager;
      this.playPanel = playPanel;
      setBackground(Color.BLACK);
      setLayout(new GridBagLayout());  // main panel uses GridBagLayout
   
      JPanel G_Panel = new JPanel(new GridBagLayout());
      G_Panel.setOpaque(false);
      G_Panel.setBackground(new Color(16, 16, 16));
      G_Panel.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(Color.WHITE, 5, true),
             BorderFactory.createEmptyBorder(50, 50, 50, 50)
         ));
      add(G_Panel);
   
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.insets = new Insets(10, 10, 10, 10);
      gbc.anchor = GridBagConstraints.CENTER;
      //gbc.fill = GridBagConstraints.NONE;
   
   
      // Title
      JLabel title = new JLabel("------ GUIDELINES ------");
      title.setForeground(Color.WHITE);
      title.setFont(new Font("VCR OSD Mono", Font.BOLD, 50));
      gbc.gridy = 0;
      gbc.anchor = GridBagConstraints.CENTER;
      G_Panel.add(title, gbc);
   
      // Guidelines list
      List<String> guidelines = new ArrayList<>();
      guidelines.add("• Type as fast as you can.");
      guidelines.add("• The timer speeds up over time.");
      guidelines.add("• Sentences will be checked per current word.");
      guidelines.add("• Use the SPACE BAR to proceed to the next word/sentence.");
      guidelines.add("• Mistakes will cost you.");
      guidelines.add("• Hesitate, and you'll fall behind.");
      guidelines.add("• Don't forget the punctuation marks.");
      guidelines.add("• Don't let IT get YOU.");
      guidelines.add("");
      guidelines.add("Tip: Complete 25 sentences to survive");
   
      gbc.gridwidth = 1;
      for (int i = 0; i < guidelines.size(); i++) {
         JLabel line = new JLabel(guidelines.get(i));
         line.setForeground(Color.WHITE);
         line.setFont(new Font("VCR OSD Mono", Font.PLAIN, 23));
         gbc.anchor = GridBagConstraints.WEST;
         gbc.gridy = i + 1;
         G_Panel.add(line, gbc);
      }
   
      // Ready button
      JButton ready = new JButton("READY");
      ready.setPreferredSize(new Dimension(200, 50));
      ready.setFont(new Font("VCR OSD Mono", Font.PLAIN, 25));
      ready.setForeground(Color.WHITE);
      ready.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3, true));
      ready.setFocusPainted(false);
      ready.setBorderPainted(true);
      ready.setBackground(Color.BLACK);
      ready.setVisible(false);
   
      gbc.gridy = guidelines.size() + 2;
      gbc.anchor = GridBagConstraints.CENTER;
      gbc.gridwidth = 2;
      gbc.insets = new Insets(40, 10, 10, 10);
   
      Color baseColor = new Color(0, 0, 0);
      Color hoverColor = new Color(30, 30, 30);
      Color pressColor = new Color(77, 77, 77);
   
      ready.addMouseListener(
         new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { ready.setBackground(hoverColor);}
         
            @Override
            public void mouseExited(MouseEvent e) {
               ready.setBackground(baseColor);
            }
         
            @Override
            public void mousePressed(MouseEvent e) { ready.setBackground(pressColor); }
         
            @Override
            public void mouseReleased(MouseEvent e) {
               ready.setBackground(ready.getBounds().contains(e.getPoint()) ? hoverColor : baseColor);
            }
         });
   
      //Cursor custom
      ready.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
   
      G_Panel.add(ready, gbc);
   
      javax.swing.Timer timer = new Timer(2000, 
         event -> {
            ready.setVisible(true);
            ListenerEnabled = true;
         });
      timer.setRepeats(false);
   
      // Focusable for key events
      setFocusable(true);
      addComponentListener(
         new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
               requestFocusInWindow();
               ListenerEnabled = false;
               ready.setVisible(false);
               timer.restart();
               // Ensure cursor visible on guidelines screen
               try {
                  java.awt.Window w = javax.swing.JFrame.getFrames().length > 0 ? javax.swing.JFrame.getFrames()[0] : null;
                  if (w != null) w.setCursor(Cursor.getDefaultCursor());
               } catch (Throwable t) {}
            }
         
            @Override
            public void componentHidden(ComponentEvent e) {
            // This is called when the panel is no longer visible
               ready.setVisible(false);
               ListenerEnabled = false;
               if (timer.isRunning()) {
                  timer.stop();
               }
            }
         });
   
   
          // Action: Switch to play screen
      ready.addActionListener(
         e -> {
            if (ListenerEnabled) {
               AudioManager.stopButton();
               AudioManager.playButton();
                  CardLayout cl = (CardLayout) screenManager.getLayout();
                  // Proceed to Play and start the game
                  cl.show(screenManager, "Play");
                  // ensure the PlayPanel starts the game
                  SwingUtilities.invokeLater(() -> playPanel.startGame());
                  AudioManager.stopBGM1();
            }
         });
   
   
      InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
      ActionMap actionMap = getActionMap();
   
          // Map the Enter key to "startGame" action
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "startGame");
   
          // Define what happens for "startGame" action
      actionMap.put("startGame", 
         new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               if (ListenerEnabled) {
                  AudioManager.stopButton();
                  AudioManager.playButton();
                  CardLayout cl = (CardLayout) screenManager.getLayout();
                  cl.show(screenManager, "Play");
                  SwingUtilities.invokeLater(() -> playPanel.startGame());
                  AudioManager.stopBGM1();
               }
            }
         });
   }
}
