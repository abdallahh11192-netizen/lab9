import javax.swing.*;
import java.awt.*;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductSearch extends JFrame
{
    private final JTextField searchField = new JTextField(20);
    private final JTextArea resultsArea = new JTextArea(15, 70);

    public RandProductSearch()
    {
        super("Random Access Product Search");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("Partial product name:"));
        top.add(searchField);
        JButton searchBtn = new JButton("Search");
        JButton quitBtn = new JButton("Quit");
        top.add(searchBtn);
        top.add(quitBtn);
        add(top, BorderLayout.NORTH);

        resultsArea.setEditable(false);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(new JScrollPane(resultsArea), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> search());
        searchField.addActionListener(e -> search());
        quitBtn.addActionListener(e -> System.exit(0));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void search()
    {
        String target = searchField.getText().trim().toLowerCase();
        if (target.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Enter part of a product name.");
            return;
        }
        File f = new File(RandProductMaker.FILE_NAME);
        if (!f.exists())
        {
            JOptionPane.showMessageDialog(this, RandProductMaker.FILE_NAME + " not found. Run RandProductMaker first.");
            return;
        }

        resultsArea.setText("");
        int matches = 0;
        try (RandomAccessFile file = new RandomAccessFile(f, "r"))
        {
            long records = file.length() / Product.RECORD_SIZE;
            for (long i = 0; i < records; i++)
            {
                Product p = Product.readRecord(file);
                if (p.getName().toLowerCase().contains(target))
                {
                    resultsArea.append(p + "\n");
                    matches++;
                }
            }
        }
        catch (EOFException ex)
        {
            // fell off the end of the file; nothing more to read
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
            return;
        }
        if (matches == 0)
        {
            resultsArea.setText("No products match \"" + searchField.getText().trim() + "\".");
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(RandProductSearch::new);
    }
}
