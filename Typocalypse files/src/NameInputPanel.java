import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NameInputPanel extends JPanel {
   public static String playerName;
   private TitleScreenPanel titleScreenPanel;
   private TriggerWarningPanel triggerWarningPanel;
   private JTextField textfield;
   private JPanel screenManager;
   private LeaderboardPanel leaderboardPanel;

   public NameInputPanel (JPanel screenManager, TitleScreenPanel titleScreenPanel, TriggerWarningPanel triggerWarningPanel, LeaderboardPanel leaderboardPanel) {
      this.titleScreenPanel = titleScreenPanel;
      this.screenManager = screenManager;
      this.triggerWarningPanel = triggerWarningPanel;
      this.leaderboardPanel = leaderboardPanel;
      setBackground(Color.BLACK);  // This sets the panel's background
      setLayout(new GridBagLayout());
   
      JPanel Panel1;
      JPanel Panel2;
      JPanel Panel3;
      JPanel Panel4;
      JPanel mainContentPanel = new JPanel(new BorderLayout());
      mainContentPanel.setBackground(Color.BLACK);
      mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
   
      Panel1 = new JPanel(new BorderLayout());
      Panel1.setBackground(Color.BLACK);
      //Panel1.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
      Panel2 = new JPanel();
      Panel2.setBackground(Color.BLACK);
      Panel3 = new JPanel();
      Panel3.setBackground(Color.BLACK);
      Panel4 = new JPanel();
      Panel4.setBackground(Color.BLACK);
      Panel4.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0)); // top, left, bottom, right
   
      JLabel Title = new JLabel("TYPOCALYPSE");
      Title.setFont(new Font("VT323", Font.PLAIN, 180));
      Title.setForeground(Color.WHITE);
      Panel2.add(Title);
      Panel1.add(Panel2, BorderLayout.NORTH);
   
   
      JLabel Tagline = new JLabel("[TYPE FAST, DIE LAST]");
      Tagline.setFont(new Font("VCR OSD Mono", Font.PLAIN, 60));
      Tagline.setForeground(Color.WHITE);
      Panel3.add(Tagline);
      Panel1.add(Panel3, BorderLayout.SOUTH);
   
      JPanel InputPanel = new JPanel(new GridBagLayout());
      InputPanel.setBackground(new Color(16, 16, 16));
      InputPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 30, 60));
   
      GridBagConstraints gbclabel = new GridBagConstraints();
      gbclabel.gridx = 0;
      gbclabel.gridy = 0;
      gbclabel.anchor = GridBagConstraints.WEST;
      gbclabel.insets = new Insets(10, 10, 10, 10);
      JLabel Name = new JLabel("PLEASE ENTER YOUR NAME:");
      Name.setFont(new Font("VCR OSD Mono", Font.PLAIN, 35));
      Name.setForeground(Color.WHITE);
      InputPanel.add(Name, gbclabel);
   
      GridBagConstraints gbctextfield = new GridBagConstraints();
      gbctextfield.gridy = 1;
      gbctextfield.insets = new Insets(20, 10, 10, 10);
      textfield = new JTextField();
      textfield.setPreferredSize(new Dimension (700, 70));
      textfield.setFont(new Font("Inter", Font.PLAIN, 30));
      InputPanel.add(textfield, gbctextfield);
   
      addComponentListener(
         new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
               SwingUtilities.invokeLater(() -> textfield.requestFocusInWindow());
            }
         });
         // Ensure cursor is visible when name input is shown
         addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
               try {
                  java.awt.Window w = javax.swing.JFrame.getFrames().length > 0 ? javax.swing.JFrame.getFrames()[0] : null;
                  if (w != null) w.setCursor(Cursor.getDefaultCursor());
               } catch (Throwable t) {}
            }
         });
   
      GridBagConstraints gbcwarning = new GridBagConstraints();
      gbcwarning.gridy = 2;
      gbcwarning.insets = new Insets(10, 10, 10, 10);
      JLabel warning = new JLabel("");
      warning.setFont(new Font("VCR OSD Mono", Font.PLAIN, 20));
      warning.setForeground(Color.RED);
      InputPanel.add(warning, gbcwarning);
   
   
      GridBagConstraints gbcbutton = new GridBagConstraints();
      gbcbutton.gridy = 3;
      gbcbutton.insets = new Insets(60, 10, 10, 10);
      JButton submit = new JButton("ENTER");
      submit.setPreferredSize(new Dimension(200, 50));
      submit.setFont(new Font("VCR OSD Mono", Font.PLAIN, 25));
      submit.setForeground(Color.WHITE);
      submit.setBorder(BorderFactory.createLineBorder(new Color(44, 44, 44), 10, true));
      submit.setFocusPainted(false); // removes focus border
      submit.setBorderPainted(false); // removes border line
      submit.setContentAreaFilled(true); // ensures background is filled
      InputPanel.add(submit, gbcbutton);
      Panel4.add(InputPanel);
   
      // Button colors
      Color baseColor = new Color(44, 44, 44);
      Color hoverColor = new Color(70, 70, 70);
      Color pressColor = new Color(35, 35, 35);  // darker than baseColor and hoverColor
   
      // Set initial color
      submit.setBackground(baseColor);
   
      // Mouse effects
      submit.addMouseListener(
         new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
               submit.setBackground(hoverColor);
            }
         
            @Override
            public void mouseExited(MouseEvent e) {
               submit.setBackground(baseColor);
            }
         
            @Override
            public void mousePressed(MouseEvent e) {
               submit.setBackground(pressColor);
            }
         
            @Override
            public void mouseReleased(MouseEvent e) {
               if (submit.contains(e.getPoint())) {
                  submit.setBackground(hoverColor);
               } else {
                  submit.setBackground(baseColor);
               }
            }
         });
   
   
      //Action listener for button
      submit.addActionListener(
         new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            //submitAction(textfield, screenManager);
               String PlayerName = textfield.getText().trim().toUpperCase();
               if (!PlayerName.isEmpty()) {
                  playerName = PlayerName;
                  AudioManager.stopButton();
                  AudioManager.playButton();
                  textfield.setText("");
                  titleScreenPanel.updateGreeting();
                  triggerWarningPanel.updateWarning();
                  CardLayout cl = (CardLayout) screenManager.getLayout();
                  cl.show(screenManager, "TriggerWarning");
                  warning.setText("");
                  AudioManager.playBGM1();
               
               } else {
                  AudioManager.stopError();
                  AudioManager.playError();
                  warning.setText("PLEASE ENTER YOUR NAME.");
               
                  textfield.requestFocusInWindow();
               }
            }
         });
   
   
      // Add this to your NameInputPanel constructor
      InputMap inputMap = textfield.getInputMap(JComponent.WHEN_FOCUSED);
      ActionMap actionMap = textfield.getActionMap();
   
      // For Enter key to submit
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submit");
      actionMap.put("submit", 
         new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String playerName = textfield.getText().trim().toUpperCase();
               if (!playerName.isEmpty()) {
                  NameInputPanel.playerName = playerName;
                  textfield.setText("");
                  AudioManager.stopButton();
                  AudioManager.playButton();
                  titleScreenPanel.updateGreeting();
                  triggerWarningPanel.updateWarning();
                  CardLayout cl = (CardLayout) screenManager.getLayout();
                  cl.show(screenManager, "TriggerWarning");
                  AudioManager.playBGM1();
                  warning.setText("");
               } else {
                  AudioManager.stopError();
                  AudioManager.playError();
                  warning.setText("PLEASE ENTER YOUR NAME.");
               
               }
            
            
            }
         });
   
      //Cursor custom
      submit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
   
   
   
      mainContentPanel.add(Panel1, BorderLayout.NORTH);
      mainContentPanel.add(Panel4, BorderLayout.CENTER);
      add(mainContentPanel);
      textfield.requestFocusInWindow();
   }
}
