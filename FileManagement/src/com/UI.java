package com;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;

public class UI extends JFrame{
    private JTree tree;
    private JScrollPane treePane;
    private JScrollPane tablePane;
    private tableModel model = new tableModel();
    private JTable fileTable;
    private JFileChooser chooser;

    private File rootFile;
    private File copyFile;

    private Block block1;
    private Block block2;
    private Block block3;
    private Block block4;
    private ArrayList<Block> blocks = new ArrayList<Block>();

    private JLabel nameField = new JLabel();

    // Delete a dir
    public static void deleteDirectory(String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            return;
        }
        if(file.isFile()){
            file.delete();
        }else if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File myfile : files) {
                deleteDirectory(filePath + File.separator + myfile.getName());
            }
            file.delete();
        }
    }
    
    // Copy file
    private void copyFile(File source, File dest)
            throws IOException {
    	Files.copy(source.toPath(), dest.toPath());
    }
 
    // Ui
    public UI() throws IOException {
        setTitle("File System Demo");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // JFileChooser init
        String path = File.listRoots()[0].getPath();
        String rootPath = new String();
        chooser = new JFileChooser(path);
        chooser.setDialogTitle("Choose a dir for this demo");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setPreferredSize(new Dimension(800, 600));
        int result = chooser.showOpenDialog(this);
        if (result == chooser.APPROVE_OPTION){
            System.out.println(chooser.getSelectedFile().getAbsolutePath());
            rootPath = chooser.getSelectedFile().getPath();
        }

        // Create work space
        rootFile = new File(rootPath + File.separator + "ÎÒµÄµçÄÔ");

        // JTree init
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(new myFiles(rootFile, 0));
        if (!rootFile.exists()) {
            try {
                rootFile.mkdir();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "The place is not support to create dir!", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }

        block1 = new Block(1, new File(rootFile.getPath() + File.separator + "C"));
        blocks.add(block1);
        block2 = new Block(2, new File(rootFile.getPath() + File.separator + "D"));
        blocks.add(block2);
        block3 = new Block(3, new File(rootFile.getPath() + File.separator + "E"));
        blocks.add(block3);
        block4 = new Block(4, new File(rootFile.getPath() + File.separator + "F"));
        blocks.add(block4);

        root.add(new DefaultMutableTreeNode(new myFiles(block1.getBlockFile(), 1)));
        model.addRow(new myFiles(block1.getBlockFile(), 1));
        ((DefaultMutableTreeNode)root.getChildAt(0)).add(new DefaultMutableTreeNode("temp"));

        root.add(new DefaultMutableTreeNode(new myFiles(block2.getBlockFile(), 2)));
        model.addRow(new myFiles(block2.getBlockFile(), 2));
        ((DefaultMutableTreeNode)root.getChildAt(1)).add(new DefaultMutableTreeNode("temp"));

        root.add(new DefaultMutableTreeNode(new myFiles(block3.getBlockFile(), 3)));
        model.addRow(new myFiles(block3.getBlockFile(), 3));
        ((DefaultMutableTreeNode)root.getChildAt(2)).add(new DefaultMutableTreeNode("temp"));

        root.add(new DefaultMutableTreeNode(new myFiles(block4.getBlockFile(), 4)));
        model.addRow(new myFiles(block4.getBlockFile(), 4));
        ((DefaultMutableTreeNode)root.getChildAt(3)).add(new DefaultMutableTreeNode("temp"));

        
        // Table init
        fileTable = new JTable(model);
        fileTable.getTableHeader().setFont(new Font(Font.DIALOG,Font.CENTER_BASELINE,24));
        fileTable.setSelectionBackground(Color.ORANGE);
        fileTable.updateUI();

        final DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.putClientProperty("Jtree.lineStyle",  "Horizontal");
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode parent = null;
                TreePath parentPath = e.getPath();
                if (parentPath == null){
                    parent = root;
                }else{
                    parent = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
                }
                int blokName = ((myFiles)parent.getUserObject()).getBlockName();
                Block currentBlock = blocks.get(blokName - 1);
                if (parentPath == null){
                    parent = root;
                }else{
                    parent = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
                }

                nameField.setText(String.valueOf(blokName));

                model.removeRows(0, model.getRowCount());
                File rootFile = new File(((myFiles)parent.getUserObject()).getFilePath());
                if (parent.getChildCount() > 0) {
                    File[] childFiles = rootFile.listFiles();

                    for (File file : childFiles) {
                        model.addRow(new myFiles(file, blokName));
                    }
                }
                else{
                    model.addRow(new myFiles(rootFile, blokName));
                }
                fileTable.updateUI();

            }
        });
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                DefaultMutableTreeNode parent = null;
                TreePath parentPath = event.getPath();
                if (parentPath == null){
                    parent = root;
                }else{
                    parent = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
                }

                int blokName = ((myFiles)parent.getUserObject()).getBlockName();

                File rootFile = new File(((myFiles)parent.getUserObject()).getFilePath());
                File [] childFiles = rootFile.listFiles();

                model.removeRows(0, model.getRowCount());
                for (File myFile : childFiles){
                    DefaultMutableTreeNode node = null;
                    node = new DefaultMutableTreeNode(new myFiles(myFile, blokName));
                    if (myFile.isDirectory() && myFile.canRead()) {
                        node.add(new DefaultMutableTreeNode("temp"));
                    }

                    treeModel.insertNodeInto(node, parent,parent.getChildCount());
                    model.addRow(new myFiles(myFile, blokName));
                }
                if (parent.getChildAt(0).toString().equals("temp") && parent.getChildCount() != 1)
                    treeModel.removeNodeFromParent((MutableTreeNode) parent.getChildAt(0));
                fileTable.updateUI();
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                DefaultMutableTreeNode parent = null;
                TreePath parentPath = event.getPath();
                if (parentPath == null){
                    parent = root;
                }else{
                    parent = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
                }
                if (parent.getChildCount() > 0) {
                    int count = parent.getChildCount();
                    for (int i = count - 1; i >= 0; i--){
                        treeModel.removeNodeFromParent((MutableTreeNode) parent.getChildAt(i));
                    }
                    treeModel.insertNodeInto(new DefaultMutableTreeNode("temp"), parent,parent.getChildCount());
                }
                model.removeRows(0, model.getRowCount());
                fileTable.updateUI();
            }
        });
        treePane = new JScrollPane(tree);
        treePane.setPreferredSize(new Dimension(150, 400));
        add(treePane, BorderLayout.WEST);

        tablePane = new JScrollPane(fileTable);
        add(tablePane, BorderLayout.CENTER);

        // Mouse DoubleClick to open a file
        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1){
                    String fileName = ((String) model.getValueAt(fileTable.getSelectedRow(), 0));
                    String filePath = ((String) model.getValueAt(fileTable.getSelectedRow(), 1));
                    try {
                        if(Desktop.isDesktopSupported()) {
                            Desktop desktop = Desktop.getDesktop();
                            desktop.open(new File(filePath));
                        }
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "Sorry, some thing wrong!", "Fail to open",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    JOptionPane.showMessageDialog(null, "File Name: " + fileName + "\n File Path: " + filePath, "content",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Menu init
        final JPopupMenu myMenu = new JPopupMenu();
        myMenu.setPreferredSize(new Dimension(300, 200));

        // Create a file
        JMenuItem createFileItem = new JMenuItem("create a file");
        createFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);

                String inputValue;
                JOptionPane inputPane = new JOptionPane();
                inputPane.setPreferredSize(new Dimension(600, 600));
                inputPane.setInputValue(JOptionPane.showInputDialog("File name:"));
                if (inputPane.getInputValue() == null) {
                    return;
                }
                inputValue = inputPane.getInputValue().toString();

                File newFile = new File(temp.getFilePath() + File.separator + inputValue + ".txt");
                if (!newFile.exists() && !inputValue.equals(null)){
                    try {
                    	currentBlock.createFile(newFile);
                        model.removeRows(0, model.getRowCount());
                        model.addRow(new myFiles(newFile, blokName));
                        fileTable.updateUI();
                        JOptionPane.showMessageDialog(null, "Create success! Reopen the parent dir to reflash!", "Success", JOptionPane.DEFAULT_OPTION);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "Create fail!!!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        myMenu.add(createFileItem);

        // create a dir
        JMenuItem createDirItem = new JMenuItem("create a dir");
        createDirItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();               
                String inputValue = JOptionPane.showInputDialog("Dir name:");
                if (inputValue == null) {
                    return;
                }
                File newDir = new File(temp.getFilePath() + File.separator + inputValue);
                if (newDir.exists())
                    deleteDirectory(newDir.getPath());
                try{
                    newDir.mkdir();
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new myFiles(newDir, blokName));
                    newNode.add(new DefaultMutableTreeNode("temp"));
                    model.removeRows(0, model.getRowCount());
                    model.addRow(new myFiles(newDir, blokName));
                    fileTable.updateUI();
                    JOptionPane.showMessageDialog(null, "Create success! Reopen the parent dir to reflash!", "Success", JOptionPane.DEFAULT_OPTION);
                }catch (Exception E){
                    JOptionPane.showMessageDialog(null, "Create fail!!!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        myMenu.add(createDirItem);

        // Delete a file or a dir
        JMenuItem deleteItem = new JMenuItem("delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);
                int choose = JOptionPane.showConfirmDialog(null, "Are you sure to delete this file/dir?", "confirm", JOptionPane.YES_NO_OPTION);
                if (choose == 0){
                    if (currentBlock.deleteFile(temp.getMyFile())){                     
                        JOptionPane.showMessageDialog(null, "Delete success! Reopen the parent dir to reflash!", "Success", JOptionPane.DEFAULT_OPTION);
                    }else{
                        JOptionPane.showMessageDialog(null, "Delete fail!!!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        myMenu.add(deleteItem);

        // Copy a file
        JMenuItem copyItem = new JMenuItem("copy");
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);
                
                copyFile = new File(temp.getFilePath().substring(0,temp.getFilePath().length() - 4) + "(1).txt");
                
                if (!copyFile.exists()){
                    try {
                    	copyFile(temp.getMyFile(), copyFile);
                        JOptionPane.showMessageDialog(null, "Copy success! Reopen the parent dir to reflash!", "Success", JOptionPane.DEFAULT_OPTION);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "Copy fail!!!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                }
            }
        );
        myMenu.add(copyItem);
        
        // Rename a dir/file
        JMenuItem renameItem = new JMenuItem("rename");
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);

                String inputValue = null;
                JOptionPane inputPane = new JOptionPane();
                inputPane.setInputValue(JOptionPane.showInputDialog("New file name:"));
                if (inputPane.getInputValue() == null) {
                    return;
                }
                inputValue = inputPane.getInputValue().toString();
                try {
                    currentBlock.renameFile(temp.getMyFile(), inputValue);
                    JOptionPane.showMessageDialog(null, "Rename success! Reopen the parent dir to reflash!", "Success", JOptionPane.DEFAULT_OPTION);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Rename fail!!!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        myMenu.add(renameItem);

     // Listen to the tree
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3){
                    myMenu.show(e.getComponent(), e.getX(), e.getY());

                }
            }
        });
        
        // JFrame layout
        setSize(800, 600);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (int)(toolkit.getScreenSize().getWidth()-getWidth())/2;
        int y = (int)(toolkit.getScreenSize().getHeight()-getHeight())/2;
        setLocation(x, y);
        setVisible(true);
    }

    public static void main(String args[]) throws IOException {
        new UI();
    }
}

