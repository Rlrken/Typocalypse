import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TitleScreenPanel extends JPanel {
   private static int selectedIndex = 0;
   private static JLabel[] menuItems = new JLabel[3];
   private static JLabel arrowLabel;
   private JPanel screenManager;
   private JLayeredPane layeredPane;
   private JLabel greeting;

   public TitleScreenPanel(JPanel screenManager) {
      this.screenManager = screenManager;
      setLayout(new BorderLayout()); // Changed from null to BorderLayout
      setBackground(Color.BLACK);
      // Ensure cursor is visible on title screen
      addComponentListener(
         new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
               try {
                  java.awt.Window w = javax.swing.JFrame.getFrames().length > 0 ? javax.swing.JFrame.getFrames()[0] : null;
                  if (w != null) w.setCursor(Cursor.getDefaultCursor());
               } catch (Throwable t) {}
            }
         });
   
      setupLayeredPane();
      setupBackground();
      setupTitle();
      setupGreeting();
      setupMenuItems();
      setupArrow();
      setupKeyBindings();
   }

   private void setupLayeredPane() {
      layeredPane = new JLayeredPane();
      // Remove hardcoded bounds, let it fill the panel
      add(layeredPane, BorderLayout.CENTER);
   }

   private void setupBackground() {
      BackgroundPanel backgroundPanel = new BackgroundPanel("/rsc/backgrounds/bg5.gif");
      backgroundPanel.setLayout(null);
   
      // Add a component listener to ensure the background panel always fits the layered pane
      layeredPane.addComponentListener(
         new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
               backgroundPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
            }
         });
   
      layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);
   }

   private void setupTitle() {
      JLabel titleLabel = new JLabel("TYPOCALYPSE");
      titleLabel.setFont(new Font("VT323", Font.PLAIN, 160));
      titleLabel.setForeground(Color.WHITE);
      titleLabel.setBounds(100, 80, 1000, 100);
      layeredPane.add(titleLabel, JLayeredPane.PALETTE_LAYER);
   
      JLabel taglineLabel = new JLabel("[TYPE FAST, DIE LAST]");
      taglineLabel.setFont(new Font("VCR OSD Mono", Font.PLAIN, 50));
      taglineLabel.setForeground(Color.WHITE);
      taglineLabel.setBounds(110, 190, 1000, 100);
      layeredPane.add(taglineLabel, JLayeredPane.PALETTE_LAYER);
   
      JLabel credits = new JLabel("ANIMATION BY OTI");
      credits.setFont(new Font("VCR OSD Mono", Font.PLAIN, 30));
      credits.setForeground(new Color(255, 255, 255, 200));
      credits.setBounds(100, 750, 1000, 100);
      layeredPane.add(credits, JLayeredPane.PALETTE_LAYER);
   }

   private void setupGreeting() {
      greeting = new JLabel("GOODLUCK, " + NameInputPanel.playerName + "!");
      greeting.setFont(new Font("VCR OSD Mono", Font.PLAIN, 30));
      greeting.setForeground(new Color(255, 255, 255, 200));
      greeting.setBounds(1100, 750, 1000, 100);
      layeredPane.add(greeting, JLayeredPane.PALETTE_LAYER);
   }

   public void updateGreeting() {
      greeting.setText("GOODLUCK, " + NameInputPanel.playerName + "!");
   }

   private void setupMenuItems() {
      String[] options = {"PLAY", "LEADERBOARD", "EXIT"};
      int startY = 350;
   
      // Define font sizes
      final Font regularFont = new Font("Minecraftia", Font.PLAIN, 35);
      final Font selectedFont = new Font("Minecraftia", Font.BOLD, 45);
   
      for (int i = 0; i < options.length; i++) {
         final JLabel item = new JLabel(options[i]);
         item.setFont(regularFont);
         item.setForeground(Color.WHITE);
         item.setBounds(250, startY + i * 80, 400, 60);
      
         // Add mouse cursor effect
         item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      
         // Store the index for use in the listener
         final int index = i;
      
         // Add mouse listeners for hover effect and click
         item.addMouseListener(
            new MouseAdapter() {
               @Override
               public void mouseEntered(MouseEvent e) {
               // Change to selected index when mouse hovers
                  selectedIndex = index;
                  updateMenuSelection();
                  updateArrowPosition();
               }
            
               @Override
               public void mouseClicked(MouseEvent e) {
               // Handle selection when clicked
                  selectedIndex = index;
                  handleSelection();
               }
            });
      
         layeredPane.add(item, JLayeredPane.MODAL_LAYER);
         menuItems[i] = item;
      }
   
      // Initialize the first item as selected
      updateMenuSelection();
   }

   // Add a new method to update font sizes based on selection
   private void updateMenuSelection() {
      Font regularFont = new Font("Minecraftia", Font.PLAIN, 35);
      Font selectedFont = new Font("Minecraftia", Font.PLAIN, 45);
   
      for (int i = 0; i < menuItems.length; i++) {
         if (i == selectedIndex) {
            menuItems[i].setFont(selectedFont);
         } else {
            menuItems[i].setFont(regularFont);
         }
      }
   }

   private void setupArrow() {
      arrowLabel = new JLabel(">");
      arrowLabel.setFont(new Font("Minecraftia", Font.PLAIN, 40));
      arrowLabel.setForeground(Color.WHITE);
      layeredPane.add(arrowLabel, JLayeredPane.MODAL_LAYER);
      updateArrowPosition();
   }

   private void setupKeyBindings() {
      // Up Arrow
      getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "moveUp");
      getActionMap().put("moveUp", 
         new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               moveArrowUp();
            }
         });
   
      // Down Arrow
      getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
      getActionMap().put("moveDown", 
         new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               moveArrowDown();
            }
         });
   
      // Enter Key
      getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "select");
      getActionMap().put("select", 
         new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               handleSelection();
            }
         });
   }

   private void moveArrowUp() {
      selectedIndex = (selectedIndex - 1 + menuItems.length) % menuItems.length;
      updateArrowPosition();
      updateMenuSelection();
   }

   private void moveArrowDown() {
      selectedIndex = (selectedIndex + 1) % menuItems.length;
      updateArrowPosition();
      updateMenuSelection();
   }

   private void updateArrowPosition() {
      JLabel current = menuItems[selectedIndex];
      arrowLabel.setBounds(current.getX() - 60, current.getY(), 50, 60);
      layeredPane.repaint();
   }

   private void handleSelection() {
      switch (selectedIndex) {
         case 0:
            AudioManager.stopButton();
            AudioManager.playButton();
            CardLayout cl = (CardLayout) screenManager.getLayout();
            // Show the story panel before guidelines
            cl.show(screenManager, "Story");
            break;
         case 1:
            AudioManager.stopButton();
            AudioManager.playButton();
            CardLayout cl2 = (CardLayout) screenManager.getLayout();
            cl2.show(screenManager, "Leaderboard");
            break;
         case 2:
            AudioManager.stopButton();
            AudioManager.playButton();
            AudioManager.playBGM1();
            CardLayout cl3 = (CardLayout) screenManager.getLayout();
            cl3.show(screenManager, "NameInput");
            break;
      }
   }
}

// BackgroundPanel for animated GIFs
class BackgroundPanel extends JPanel {
   private ImageIcon gifIcon;

   public BackgroundPanel(String gifPath) {
      java.net.URL imageURL = getClass().getResource(gifPath);
      if (imageURL != null) {
         gifIcon = new ImageIcon(imageURL);
      } else {
         System.out.println("GIF not found: " + gifPath);
      }
   
      Timer timer = new Timer(40, e -> repaint());
      timer.start();
   }

   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (gifIcon != null) {
         Image gifFrame = gifIcon.getImage();
      
         // Get actual panel dimensions
         int panelWidth = getWidth();
         int panelHeight = getHeight();
      
         // Get original GIF dimensions
         int originalWidth = gifIcon.getIconWidth();
         int originalHeight = gifIcon.getIconHeight();
      
         // Calculate scale to maintain aspect ratio while fitting in panel
         double scaleX = (double) panelWidth / originalWidth;
         double scaleY = (double) panelHeight / originalHeight;
         double scale = Math.min(scaleX, scaleY) * 1.0; // 80% of max size for some margin
      
         // Calculate new dimensions
         int newWidth = (int) (originalWidth * scale);
         int newHeight = (int) (originalHeight * scale);
      
         // Center the image within the panel
         int x = (panelWidth - newWidth) / 2;
         int y = (panelHeight - newHeight) / 2;
      
         g.drawImage(gifFrame, x, y, newWidth, newHeight, this);
      }
   }
}