import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class LeaderboardPanel extends JPanel {
   private JTable leaderboardTable;
   private DefaultTableModel tableModel;
   private boolean scoreAlreadySaved = false; // Flag to track if current score was saved


   public LeaderboardPanel(JPanel screenManager) {
      setLayout(new BorderLayout());
      setBackground(Color.BLACK);
   
      // Main panel using GridBagLayout to center components
      JPanel mainPanel = new JPanel(new GridBagLayout());
      mainPanel.setBackground(Color.BLACK);
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.insets = new Insets(20, 0, 20, 0);
      gbc.anchor = GridBagConstraints.CENTER;
   
      // Game title
      gbc.gridy = 0;
      JLabel gameTitle = new JLabel("TYPOCALYPSE", SwingConstants.CENTER);
      gameTitle.setForeground(Color.WHITE);
      gameTitle.setFont(new Font("VT323", Font.BOLD, 150));
      mainPanel.add(gameTitle, gbc);
   
      // Leaderboard label
      gbc.gridy++;
      JLabel titleLabel = new JLabel("--- LEADERBOARD ---", SwingConstants.CENTER);
      titleLabel.setForeground(Color.WHITE);
      titleLabel.setFont(new Font("VCR OSD Mono", Font.PLAIN, 50));
      mainPanel.add(titleLabel, gbc);
   
      // Table data and column headers
      gbc.gridy++;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.weightx = 1;
      gbc.weighty = 1;
      String[] columnNames = {"RANK", "NAME", "WPM", "COMPLETED SENTENCES", "MINUTE(S) SURVIVED"};
   
      // Create DefaultTableModel with custom isCellEditable override
      tableModel = 
         new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false; // Make all cells non-editable
            }
         };
   
      // Load existing data from file
      loadLeaderboardFromFile();
   
   
   
      // Create table with the model
      leaderboardTable = new JTable(tableModel);
   
      // Custom rendering for centered text in cells
      DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
      centerRenderer.setHorizontalAlignment(JLabel.CENTER);
   
      // Style the table
      leaderboardTable.setFont(new Font("VCR OSD Mono", Font.PLAIN, 20));
      leaderboardTable.setForeground(Color.WHITE);
      leaderboardTable.setBackground(new Color(16, 16, 16));
      leaderboardTable.setGridColor(new Color(70, 70, 70));
      leaderboardTable.setRowHeight(50);
      leaderboardTable.setRowSelectionAllowed(false);
      leaderboardTable.setShowVerticalLines(true);
      leaderboardTable.setShowHorizontalLines(true);
      leaderboardTable.setIntercellSpacing(new Dimension(5, 5));
      leaderboardTable.setPreferredScrollableViewportSize(new Dimension(600, 200));
   
   
      // Apply center renderer to all columns
      for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
         leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
      }
   
      // Set column widths
      int[] columnWidths = {100, 250, 150, 200, 250};
      for (int i = 0; i < columnWidths.length; i++) {
         leaderboardTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
      }
   
      // Customize table header
      JTableHeader header = leaderboardTable.getTableHeader();
      header.setFont(new Font("VCR OSD Mono", Font.BOLD, 22));
      header.setForeground(Color.BLACK);
      header.setBackground(new Color(220, 220, 220));
      header.setOpaque(true);
      header.setReorderingAllowed(false);
      header.setResizingAllowed(false);
   
      // Set header renderer for centered column titles
      ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
   
      // Wrap table in scroll pane (automatically includes header)
      JScrollPane scrollPane = new JScrollPane(leaderboardTable);
      scrollPane.setBackground(Color.BLACK);
      scrollPane.setPreferredSize(new Dimension(400, 300));
      scrollPane.getViewport().setBackground(new Color(16, 16, 16));
      scrollPane.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createEmptyBorder(0, 50, 20, 50), // Padding
             BorderFactory.createLineBorder(new Color(220, 220, 220), 3)    // Border
         ));
   
   
      // Table panel
      JPanel tablePanel = new JPanel(new BorderLayout());
      tablePanel.setBackground(Color.BLACK);
      tablePanel.add(scrollPane, BorderLayout.CENTER);
      tablePanel.setPreferredSize(new Dimension(1000, 400));
   
      // Add to main panel with constraints
      mainPanel.add(tablePanel, gbc);
   
      // Bottom-left back button
      JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      bottomPanel.setBackground(Color.BLACK);
      bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 0));
   
      JButton backButton = new JButton("< BACK");
      backButton.setForeground(Color.WHITE);
      backButton.setFont(new Font("Minecraftia", Font.PLAIN, 25));
      backButton.setBorderPainted(false);
      backButton.setFocusPainted(false);
      backButton.setContentAreaFilled(false);
      backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      backButton.addActionListener(
         e -> {
            AudioManager.stopButton();
            AudioManager.playButton();
            CardLayout cl = (CardLayout) screenManager.getLayout();
            cl.show(screenManager, "TitleScreen");
         });
   
      // Add hover effect for back button
      backButton.addMouseListener(
         new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
               backButton.setFont(new Font("Minecraftia", Font.BOLD, 30));
            }
         
            public void mouseExited(java.awt.event.MouseEvent evt) {
               backButton.setFont(new Font("Minecraftia", Font.PLAIN, 25));
            }
         });
   
      bottomPanel.add(backButton);
      add(mainPanel, BorderLayout.CENTER);
      add(bottomPanel, BorderLayout.SOUTH);
   }

   // Method to update the leaderboard with new data
   public void updateLeaderboard(String playerName, int wpm, int sentences, double survivalTime) {
      // If this score was already saved, don't save it again
      if (scoreAlreadySaved) {
         return;
      }
   
      // Format survival time with two decimal places
      String formattedTime = new DecimalFormat("0.00").format(survivalTime);
   
      // Create a list of all existing entries plus the new one
      List<Object[]> allEntries = new ArrayList<>();
   
      // First, collect all existing entries from the table
      for (int i = 0; i < tableModel.getRowCount(); i++) {
         Object[] rowData = new Object[5];
         for (int j = 0; j < 5; j++) {
            rowData[j] = tableModel.getValueAt(i, j);
         }
         allEntries.add(rowData);
      }
   
      // Add the new entry
      Object[] newEntry = {0, playerName, wpm, sentences, formattedTime};
      allEntries.add(newEntry);
   
      // Sort by survival time (column 4) in descending order
      allEntries.sort(
         (a, b) -> {
            double timeA = Double.parseDouble(a[4].toString().replace(",", "."));
            double timeB = Double.parseDouble(b[4].toString().replace(",", "."));
            return Double.compare(timeB, timeA); // Descending order
         });
   
      // Clear the current table
      while (tableModel.getRowCount() > 0) {
         tableModel.removeRow(0);
      }
   
      // Add top 10 entries with updated ranks
      for (int i = 0; i < Math.min(15, allEntries.size()); i++) {
         Object[] entry = allEntries.get(i);
         entry[0] = i + 1; // Update rank
         tableModel.addRow(entry);
      }
   
      // Save the updated leaderboard
      saveLeaderboardToFile();
   
      // Mark that we've saved this score
      scoreAlreadySaved = true;
   }

   // Method to reset the saved flag (call this when starting a new game)
   public void resetSavedFlag() {
      scoreAlreadySaved = false;
   }


   // Method to save the leaderboard data to a text file
   private void saveLeaderboardToFile() {
      try (PrintWriter writer = new PrintWriter(new FileWriter("leaderboard.txt"))) {
         // Write a header line
         writer.println("PlayerName,WPM,CompletedSentences,Minute(s)Survived");
      
         // Write each row, skipping the rank column (index 0)
         for (int i = 0; i < tableModel.getRowCount(); i++) {
            StringBuilder line = new StringBuilder();
            // Start from column 1 to skip the rank
            line.append(tableModel.getValueAt(i, 1)).append(","); // Player name
            line.append(tableModel.getValueAt(i, 2)).append(","); // WPM
            line.append(tableModel.getValueAt(i, 3)).append(","); // Sentences
            line.append(tableModel.getValueAt(i, 4)); // Survival time
            writer.println(line.toString());
         }
         System.out.println("Leaderboard saved successfully to leaderboard.txt");
      } catch (IOException e) {
         JOptionPane.showMessageDialog(this,
                "Error saving leaderboard: " + e.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
         e.printStackTrace();
      }
   }

   // Method to load leaderboard data from a file
   private void loadLeaderboardFromFile() {
      File file = new File("leaderboard.txt");
      if (!file.exists()) {
         return; // No file to load yet
      }
   
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
         String line;
         boolean isFirstLine = true;
      
         // Temporary list to hold all entries
         List<Object[]> allEntries = new ArrayList<>();
      
         while ((line = reader.readLine()) != null) {
            if (isFirstLine) {
               isFirstLine = false;
               continue; // Skip header line
            }
         
            String[] data = line.split(",");
            if (data.length == 4) { // PlayerName,WPM,Sentences,SurvivalTime
               // Convert string values to appropriate types
               String playerName = data[0];
               int wpm = Integer.parseInt(data[1]);
               int sentences = Integer.parseInt(data[2]);
               String survivalTime = data[3];
            
               // Create row data with temporary rank (will be set properly later)
               Object[] rowData = {0, playerName, wpm, sentences, survivalTime};
               allEntries.add(rowData);
            }
         }
      
         // Sort entries by survival time
         allEntries.sort(
            (a, b) -> {
               double timeA = Double.parseDouble(a[4].toString().replace(",", "."));
               double timeB = Double.parseDouble(b[4].toString().replace(",", "."));
               return Double.compare(timeB, timeA); // Descending order
            });
      
         // Clear existing data
         while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
         }
      
         // Add only top 10 entries with proper ranks
         for (int i = 0; i < Math.min(10, allEntries.size()); i++) {
            Object[] entry = allEntries.get(i);
            entry[0] = i + 1; // Update rank
            tableModel.addRow(entry);
         }
      
         System.out.println("Leaderboard loaded successfully from leaderboard.txt");
      } catch (IOException | NumberFormatException e) {
         JOptionPane.showMessageDialog(this,
                "Error loading leaderboard: " + e.getMessage(),
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
         e.printStackTrace();
      }
   }

}
