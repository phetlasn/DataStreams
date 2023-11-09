import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.nio.file.StandardOpenOption.CREATE;

public class DataStreamsMain extends JFrame {

    JPanel mainPanel, displayPanel, buttonPanel, searchPanel;
    JLabel searchLabel;
    JButton loadButton, filterButton, quitButton;
    JScrollPane leftPane, rightPane;
    JTextArea leftArea, rightArea;
    JTextField searchText;

    private File selectedFile;
    private Path filePath;

    public Set set = new HashSet();

    DataStreamsMain() {

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        createDisplayPanel();
        createButtonPanel();
        createSearchPanel();

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(displayPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setSize(5*(screenWidth / 6), 5*(screenHeight / 6));
        setLocationRelativeTo(null);
        setTitle("Data Stream Search");

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void createSearchPanel() {

        searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(1,2));
        searchText = new JTextField();
        searchLabel = new JLabel("Enter Your Search: ");
        searchLabel.setFont(new Font("Monospaced", Font.PLAIN, 24));
        searchLabel.setHorizontalAlignment(JLabel.CENTER);
        searchPanel.add(searchLabel);
        searchPanel.add(searchText);
    }

    public void createDisplayPanel() {

        displayPanel = new JPanel();
        displayPanel.setLayout(new GridLayout(1,2));
        displayPanel.setBorder(new TitledBorder(new EtchedBorder(), ""));

        leftArea = new JTextArea();
        rightArea = new JTextArea();

        leftArea.setEditable(false);
        rightArea.setEditable(false);

        leftArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        rightArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));

        leftPane = new JScrollPane(leftArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPane = new JScrollPane(rightArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        leftArea.setBorder(new TitledBorder("Original File"));
        rightPane.setBorder(new TitledBorder("Filtered File"));

        displayPanel.add(leftPane);
        displayPanel.add(rightPane);
    }

    public void createButtonPanel() {

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));
        buttonPanel.setBorder(new TitledBorder(new EtchedBorder(), ""));

        loadButton = new JButton("Load");
        filterButton = new JButton("Filter");
        quitButton = new JButton("Quit");

        filterButton.setEnabled(false);
        filterButton.setBackground(new Color(255, 255, 235));

        loadButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        filterButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        quitButton.setFont(new Font("Monospaced", Font.BOLD, 20));

        loadButton.addActionListener((ActionEvent e) -> load());
        filterButton.addActionListener((ActionEvent e) -> filter());
        quitButton.addActionListener((ActionEvent e) -> {

            int res = JOptionPane.showOptionDialog(null, "Are You Sure You Want To Quit?", "Message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "No"}, JOptionPane.YES_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
            else if (res == JOptionPane.NO_OPTION) {
                JOptionPane.showMessageDialog(null, "Quit Request Canceled", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
            else if (res == JOptionPane.CLOSED_OPTION) {
                JOptionPane.showMessageDialog(null, "Quit Request Canceled", "Message", JOptionPane.INFORMATION_MESSAGE);
            }});

        buttonPanel.add(loadButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(quitButton);
        }

        public void load() {

        JFileChooser chooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            filePath = selectedFile.toPath();
        }
        filterButton.setEnabled(true);
        filterButton.setBackground(null);
        JOptionPane.showMessageDialog(mainPanel, "File Loaded!", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void filter() {

        leftArea.setText("");
        rightArea.setText("");
        String wordFilter = searchText.getText();
        String rec;
        try (Stream<String> lines = Files.lines(Paths.get(selectedFile.getPath()))){

            Set<String> set = lines.filter(w -> w.contains(wordFilter)).collect(Collectors.toSet());
            set.forEach(w -> rightArea.append(w + "\n"));
        }

        catch (FileNotFoundException e) {
            System.out.println("File Not Found!");
            e.printStackTrace();
        }

        catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            InputStream in = new BufferedInputStream(Files.newInputStream(filePath, CREATE));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            int line = 0;
            while (reader.ready()) {
                rec = reader.readLine();
                leftArea.append(rec + "\n");
                line++;
            }
            reader.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println("File Not Found!");
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
