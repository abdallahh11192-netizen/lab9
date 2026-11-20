import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductMaker extends JFrame
{
    public static final String FILE_NAME = "ProductTestData.bin";

    private final JTextField nameField = new JTextField(Product.NAME_LEN);
    private final JTextField descField = new JTextField(30);
    private final JTextField idField = new JTextField(Product.ID_LEN);
    private final JTextField costField = new JTextField(10);
    private final JTextField countField = new JTextField(5);
    private int recordCount = 0;

    public RandProductMaker()
    {
        super("Random Access Product Maker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        form.add(new JLabel("Name (max 35):"));
        form.add(nameField);
        form.add(new JLabel("Description (max 75):"));
        form.add(descField);
        form.add(new JLabel("ID (max 6):"));
        form.add(idField);
        form.add(new JLabel("Cost:"));
        form.add(costField);
        form.add(new JLabel("Record count:"));
        countField.setEditable(false);
        form.add(countField);
        add(form, BorderLayout.CENTER);

        JButton addBtn = new JButton("Add");
        JButton quitBtn = new JButton("Quit");
        addBtn.addActionListener(e -> addRecord());
        quitBtn.addActionListener(e -> System.exit(0));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttons.add(addBtn);
        buttons.add(quitBtn);
        add(buttons, BorderLayout.SOUTH);

        // Pick up where the file left off so counts stay correct if the program is rerun
        File f = new File(FILE_NAME);
        if (f.exists())
        {
            recordCount = (int) (f.length() / Product.RECORD_SIZE);
        }
        countField.setText(String.valueOf(recordCount));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addRecord()
    {
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        String id = idField.getText().trim();
        String costText = costField.getText().trim();

        if (name.isEmpty() || desc.isEmpty() || id.isEmpty() || costText.isEmpty())
        {
            error("Please fill in all of the fields.");
            return;
        }
        if (name.length() > Product.NAME_LEN)
        {
            error("Name must be at most " + Product.NAME_LEN + " characters.");
            return;
        }
        if (desc.length() > Product.DESC_LEN)
        {
            error("Description must be at most " + Product.DESC_LEN + " characters.");
            return;
        }
        if (id.length() > Product.ID_LEN)
        {
            error("ID must be at most " + Product.ID_LEN + " characters.");
            return;
        }
        double cost;
        try
        {
            cost = Double.parseDouble(costText);
            if (cost < 0)
            {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException ex)
        {
            error("Cost must be a valid non-negative number.");
            return;
        }

        Product p = new Product(name, desc, id, cost);
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw"))
        {
            file.seek(file.length()); // append
            p.writeRecord(file);
        }
        catch (IOException ex)
        {
            error("Could not write to " + FILE_NAME + ": " + ex.getMessage());
            return;
        }

        recordCount++;
        countField.setText(String.valueOf(recordCount));
        nameField.setText("");
        descField.setText("");
        idField.setText("");
        costField.setText("");
        nameField.requestFocus();
    }

    private void error(String msg)
    {
        JOptionPane.showMessageDialog(this, msg, "Input problem", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(RandProductMaker::new);
    }
}
