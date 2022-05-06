package GUI;

import Logic.SimpleNote;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SwingUI {
    private JTextArea textArea;
    private JList<String> jList;
    private javax.swing.JPanel JPanel;
    private JButton deleteButton;

    private List<SimpleNote> simpleNoteList = new ArrayList<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private SimpleNote currentNote;

    public SwingUI() {
        JFrame frame = new JFrame("Заметки");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        jListListener();
        deleteButton.addActionListener(new ListenerActionDelete());
        frame.setJMenuBar(menuBar);
        frame.setContentPane(JPanel);
        frame.setVisible(true);
    }

    private JMenu createFileMenu() {
        JMenu menu = new JMenu("Файл");
        JMenuItem newM = new JMenuItem("Создать заметку");
        JMenuItem openM = new JMenuItem("Открыть");
        JMenuItem saveM = new JMenuItem("Сохранить как");
        JMenuItem exitM = new JMenuItem(new ExitAction());
        menu.add(newM);
        menu.add(openM);
        menu.add(saveM);
        menu.addSeparator();
        menu.add(exitM);

        createNewNote();

        newM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                createNewNote();
            }
        });
        openM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser openFile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                openFile.addChoosableFileFilter(new CustomFilter());
                int returnVal = openFile.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = openFile.getSelectedFile();
                    try {
                        if (!listModel.contains(file.getName())) {
                            textArea.read(new FileReader(file.getAbsolutePath()), null);
                            SimpleNote simpleNote = new SimpleNote(file.getName(), textArea.getText());
                            simpleNoteList.add(simpleNote);
                            listModel.addElement(simpleNote.getName());
                            currentNote = simpleNote;
                        }
                    } catch (IOException ex) {
                        System.out.println("Проблема с доступом к файлу" + file.getAbsolutePath());
                    }
                } else {
                    System.out.println("Открытие файла отменено пользователем.");
                }
            }
        });
        saveM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser saveFile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                saveFile.setFileFilter(new CustomFilter());
                if (saveFile.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try (FileWriter fileWriter = new FileWriter(saveFile.getSelectedFile())) {
                        SimpleNote simpleNote = new SimpleNote(saveFile.getSelectedFile().getName(), textArea.getText());
                        fileWriter.write(simpleNote.getText());
                        if (!listModel.contains(saveFile.getSelectedFile().getName())) {
                            new ListenerActionDelete().delete();
                            simpleNoteList.add(simpleNote);
                            listModel.addElement(simpleNote.getName());
                        } else {
                            simpleNoteList.get(noteSearch(simpleNote)).setText(textArea.getText());
                        }
                        textArea.setText(simpleNote.getText());
                    } catch (IOException e) {
                        System.out.println("Проблема с сохранением файла");
                    }
                }
            }
        });
        return menu;
    }

    private void createNewNote() {
        String nameFile = "NewNote" + simpleNoteList.size();
        while (listModel.contains(nameFile)) {
            nameFile = "NewNote" + (simpleNoteList.size() + 1);
        }
        SimpleNote simpleNote = new SimpleNote(nameFile, "");
        simpleNoteList.add(simpleNote);
        if (currentNote != null) {
            currentNote.setText(textArea.getText());
        }
        textArea.setText(simpleNote.getText());
        listModel.addElement(simpleNote.getName());
        jList.setModel(listModel);
        currentNote = simpleNote;
    }

    private void jListListener() {
        jList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    if (currentNote != null) {
                        currentNote.setText(textArea.getText());
                    }
                    int selected = jList.locationToIndex(e.getPoint());
                    currentNote = simpleNoteList.get(selected);
                    textArea.setText(currentNote.getText());
                }
            }
        });
    }

    private int noteSearch(SimpleNote simpleNote) {
        int i = 0;
        for (SimpleNote o : simpleNoteList) {
            if (o.getName().equals(simpleNote.getName())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    class ListenerActionDelete implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            delete();
        }

        public void delete() {
            int selected = jList.getSelectedIndex();
            if (selected != -1) {
                simpleNoteList.remove(selected);
                listModel.remove(selected);
                textArea.setText("");
            } else if (!listModel.isEmpty()) {
                simpleNoteList.remove(0);
                listModel.remove(0);
                textArea.setText("");
            }
        }
    }


    static class CustomFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File file) {
            return file.isDirectory() || file.getAbsolutePath().endsWith(".txt");
        }

        @Override
        public String getDescription() {
            return "Text documents (*.txt)";
        }
    }

    static class ExitAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        ExitAction() {
            putValue(NAME, "Выход");
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new SwingUI();
    }

}
