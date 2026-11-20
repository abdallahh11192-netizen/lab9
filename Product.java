import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * Product record usable with text, Object and RandomAccess streams.
 * String fields are padded with spaces to fixed lengths for the random access file.
 */
public class Product implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final int NAME_LEN = 35;
    public static final int DESC_LEN = 75;
    public static final int ID_LEN = 6;
    // Each char is written with writeChars (2 bytes), plus 8 bytes for the double
    public static final int RECORD_SIZE = (NAME_LEN + DESC_LEN + ID_LEN) * 2 + 8;

    private String name;
    private String description;
    private final String ID; // should never change
    private double cost;

    public Product(String name, String description, String ID, double cost)
    {
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.cost = cost;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getID() { return ID; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    /** Pads with spaces or truncates so the String is exactly len characters. */
    public static String pad(String s, int len)
    {
        StringBuilder sb = new StringBuilder(s == null ? "" : s);
        if (sb.length() > len)
        {
            sb.setLength(len);
        }
        while (sb.length() < len)
        {
            sb.append(' ');
        }
        return sb.toString();
    }

    public String getPaddedName() { return pad(name, NAME_LEN); }
    public String getPaddedDescription() { return pad(description, DESC_LEN); }
    public String getPaddedID() { return pad(ID, ID_LEN); }

    /** Writes this record at the current position of the random access file. */
    public void writeRecord(RandomAccessFile file) throws IOException
    {
        file.writeChars(getPaddedName());
        file.writeChars(getPaddedDescription());
        file.writeChars(getPaddedID());
        file.writeDouble(cost);
    }

    /** Reads one fixed-length record from the current position of the file. */
    public static Product readRecord(RandomAccessFile file) throws IOException
    {
        String n = readFixed(file, NAME_LEN).trim();
        String d = readFixed(file, DESC_LEN).trim();
        String id = readFixed(file, ID_LEN).trim();
        double c = file.readDouble();
        return new Product(n, d, id, c);
    }

    private static String readFixed(RandomAccessFile file, int len) throws IOException
    {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
        {
            sb.append(file.readChar());
        }
        return sb.toString();
    }

    @Override
    public String toString()
    {
        return String.format("%-6s | %-35s | %-75s | $%.2f", ID, name, description, cost);
    }
}
