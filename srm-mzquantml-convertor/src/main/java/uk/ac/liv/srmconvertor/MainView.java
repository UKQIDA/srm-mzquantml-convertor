/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.srmconvertor;

import au.com.bytecode.opencsv.CSVReader;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Da Qi
 */
public class MainView extends javax.swing.JFrame {

    private static File currentDirectory;
    private static File inputFile;
    private static File outputFile;

    /**
     * Creates new form MainView
     */
    public MainView() {
        //... Setting standard look and feel ...//
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException ex) {
            System.out.println(ex.getStackTrace());
        }
        catch (InstantiationException ex) {
            System.out.println(ex.getStackTrace());
        }
        catch (IllegalAccessException ex) {
            System.out.println(ex.getStackTrace());
        }
        catch (UnsupportedLookAndFeelException ex) {
            System.out.println(ex.getStackTrace());
        }

        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        // Move the window
        this.setLocation(x, y);

        initComponents();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        rightList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        leftList = new javax.swing.JList();
        bLeftToRight = new javax.swing.JButton();
        bRightToLeft = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        inputFileArea = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        run = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jScrollPane1.setViewportView(rightList);

        jScrollPane2.setViewportView(leftList);

        bLeftToRight.setText(">>");
        bLeftToRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLeftToRightActionPerformed(evt);
            }
        });

        bRightToLeft.setText("<<");
        bRightToLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRightToLeftActionPerformed(evt);
            }
        });

        inputFileArea.setEditable(false);
        inputFileArea.setColumns(20);
        inputFileArea.setLineWrap(true);
        inputFileArea.setRows(5);
        inputFileArea.setWrapStyleWord(true);
        inputFileArea.setAutoscrolls(false);
        inputFileArea.setBorder(null);
        inputFileArea.setEnabled(false);
        jScrollPane3.setViewportView(inputFileArea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bLeftToRight, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bRightToLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
            .addComponent(jScrollPane3)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(bLeftToRight)
                        .addGap(18, 18, 18)
                        .addComponent(bRightToLeft))
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2.setPreferredSize(new java.awt.Dimension(525, 80));

        run.setText("Run ...");
        run.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(run)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(run)
                .addGap(0, 57, Short.MAX_VALUE))
        );

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");

        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Cut");
        editMenu.add(cutMenuItem);

        copyMenuItem.setMnemonic('y');
        copyMenuItem.setText("Copy");
        editMenu.add(copyMenuItem);

        pasteMenuItem.setMnemonic('p');
        pasteMenuItem.setText("Paste");
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setText("Delete");
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contents");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-541)/2, (screenSize.height-479)/2, 541, 479);
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed

        JFileChooser fileChooser = new javax.swing.JFileChooser("user.home");
        fileChooser.setDialogTitle("Select a CSV file");
        fileChooser.setCurrentDirectory(currentDirectory);

        //... Applying file extension filters ...//
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV (Comma delimited)(*.csv)", "csv");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            inputFile = fileChooser.getSelectedFile();
            inputFileArea.setText(inputFile.getAbsolutePath() + " is opened.");
            currentDirectory = inputFile.getParentFile();
        }

        //TODO: isFileValid();

        loadOrignalList();
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void bRightToLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRightToLeftActionPerformed

        Object[] rightItems = rightList.getSelectedValues();
        ListModel leftListModel = leftList.getModel();
        int oldSize = leftListModel.getSize();
        Object[] oldLeftMods = new Object[oldSize];
        for (int i = 0; i < oldSize; i++) {
            oldLeftMods[i] = leftListModel.getElementAt(i);
        }

        DefaultListModel listModel = new DefaultListModel();
        Object[] allMods = ArrayUtils.addAll(oldLeftMods, rightItems);
        //Arrays.sort(allMods);
        for (Object mod : allMods) {
            if (!listModel.contains(mod)) {
                listModel.addElement(mod);
            }
        }
        leftList.setModel(listModel);
        DefaultListModel rightListModel = (DefaultListModel) rightList.getModel();
        for (Object o : rightItems) {
            rightListModel.removeElement(o);
        }
    }//GEN-LAST:event_bRightToLeftActionPerformed

    private void bLeftToRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLeftToRightActionPerformed

        Object[] leftItems = leftList.getSelectedValues();
        ListModel rightListModel = rightList.getModel();
        int oldSize = rightListModel.getSize();
        Object[] oldRightMods = new Object[oldSize];
        for (int i = 0; i < oldSize; i++) {
            oldRightMods[i] = rightListModel.getElementAt(i);
        }

//        if (oldRightMods[0].equals("--- none selected ---")) {
//            DefaultListModel listModel = new DefaultListModel();
//
//            if (selectedVarMods.length != 0) {
//                for (Object mod : selectedVarMods) {
//                    listModel.addElement(mod);
//                }
//                jListVarMods.setModel(listModel);
//            }
//        }
//        else {
        DefaultListModel listModel = new DefaultListModel();
        Object[] allMods = ArrayUtils.addAll(oldRightMods, leftItems);
        //Arrays.sort(allMods);
        for (Object mod : allMods) {
            if (!listModel.contains(mod)) {
                listModel.addElement(mod);
            }
        }
        rightList.setModel(listModel);
//        }

        DefaultListModel leftListModel = (DefaultListModel) leftList.getModel();
        for (Object o : leftItems) {
            leftListModel.removeElement(o);
        }

    }//GEN-LAST:event_bLeftToRightActionPerformed

    private void runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runActionPerformed
    
        JFileChooser fileChooser = new javax.swing.JFileChooser("user.home");
        fileChooser.setDialogTitle("Save mzq file");
        fileChooser.setCurrentDirectory(currentDirectory);

        //... Applying file extension filters ...//
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MzQuantML (*.mzq)", "mzq");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            outputFile = fileChooser.getSelectedFile();
           // inputFileArea.setText(inputFile.getAbsolutePath() + " is opened.");
            currentDirectory = outputFile.getParentFile();
        }
        
        
        
    }//GEN-LAST:event_runActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainView().setVisible(true);
            }

        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton bLeftToRight;
    private javax.swing.JButton bRightToLeft;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JTextArea inputFileArea;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList leftList;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JList rightList;
    private javax.swing.JButton run;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    // End of variables declaration//GEN-END:variables

    private void loadOrignalList() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(inputFile));
            CSVReader cr = new CSVReader(br);
            String[] nextLine = cr.readNext();

            DefaultListModel originalLM = new DefaultListModel();
            for (String heading : nextLine) {
                if (!heading.isEmpty()) {
                    originalLM.addElement(heading);
                }
            }

            leftList.setModel(originalLM);
        }
        catch (IOException ioEx) {
            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ioEx);
        }
        finally {
            try {
                br.close();
            }
            catch (IOException ex) {
                Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}