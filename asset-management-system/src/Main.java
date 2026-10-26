import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/** Swing interface for maintaining assets. Records are saved in assets.txt. */
public class Main extends JFrame {
    private static final Path DATA_FILE = Paths.get("assets.txt");
    private static final String[] COLUMNS = {
        "Asset ID", "Name", "Type", "Purchase Price", "Purchase Year", "Status", "Assigned To"
    };

    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable assetTable = new JTable(tableModel);
    private final JTextField[] fields = new JTextField[COLUMNS.length];

    public Main() {
        super("Asset Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(COLUMNS.length, 2, 8, 6));
        form.setBorder(BorderFactory.createTitledBorder("Asset details"));
        for (int i = 0; i < COLUMNS.length; i++) {
            fields[i] = new JTextField(18);
            form.add(new JLabel(COLUMNS[i] + ":"));
            form.add(fields[i]);
        }

        JPanel buttons = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton saveButton = new JButton("Save");
        JButton clearButton = new JButton("Clear Fields");
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(saveButton);
        buttons.add(clearButton);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        assetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assetTable.setAutoCreateRowSorter(true);
        add(new JScrollPane(assetTable), BorderLayout.CENTER);

        addButton.addActionListener(event -> addAsset());
        updateButton.addActionListener(event -> updateAsset());
        deleteButton.addActionListener(event -> deleteAsset());
        saveButton.addActionListener(event -> saveAssets());
        clearButton.addActionListener(event -> clearFields());
        assetTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && assetTable.getSelectedRow() >= 0) {
                copySelectedRowToFields();
            }
        });

        loadAssets();
        setSize(1000, 580);
        setLocationRelativeTo(null);
    }

    private Object[] readForm() {
        String[] value = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            value[i] = fields[i].getText().trim();
            if (value[i].isEmpty()) {
                showError(COLUMNS[i] + " cannot be empty.");
                fields[i].requestFocusInWindow();
                return null;
            }
        }
        try {
            int id = Integer.parseInt(value[0]);
            double price = Double.parseDouble(value[3]);
            int year = Integer.parseInt(value[4]);
            if (id <= 0) throw new IllegalArgumentException("Asset ID must be a positive integer.");
            if (!Double.isFinite(price) || price < 0) throw new IllegalArgumentException("Purchase price must be a valid non-negative number.");
            if (year <= 0) throw new IllegalArgumentException("Purchase year must be a positive integer.");
            value[0] = Integer.toString(id);
            value[3] = Double.toString(price);
            value[4] = Integer.toString(year);
            return value;
        } catch (NumberFormatException ex) {
            showError("Asset ID and purchase year must be whole numbers; purchase price must be numeric.");
            return null;
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
            return null;
        }
    }

    private void addAsset() {
        Object[] record = readForm();
        if (record == null) return;
        if (findRowById((String) record[0]) >= 0) {
            showError("An asset with this ID already exists.");
            return;
        }
        tableModel.addRow(record);
        clearFields();
    }

    private void updateAsset() {
        int viewRow = assetTable.getSelectedRow();
        if (viewRow < 0) {
            showError("Select the asset row you want to update.");
            return;
        }
        int row = assetTable.convertRowIndexToModel(viewRow);
        Object[] record = readForm();
        if (record == null) return;
        int duplicate = findRowById((String) record[0]);
        if (duplicate >= 0 && duplicate != row) {
            showError("Another asset already uses this ID.");
            return;
        }
        for (int col = 0; col < record.length; col++) tableModel.setValueAt(record[col], row, col);
    }

    private void deleteAsset() {
        int viewRow = assetTable.getSelectedRow();
        if (viewRow < 0) {
            showError("Select an asset row to delete.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Delete the selected asset?", "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            tableModel.removeRow(assetTable.convertRowIndexToModel(viewRow));
            clearFields();
        }
    }

    private void saveAssets() {
        try (BufferedWriter writer = Files.newBufferedWriter(DATA_FILE, StandardCharsets.UTF_8)) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < COLUMNS.length; col++) {
                    if (col > 0) writer.write('\t');
                    writer.write(escape(tableModel.getValueAt(row, col).toString()));
                }
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Saved " + tableModel.getRowCount() + " asset(s) to " + DATA_FILE.toAbsolutePath(), "Save complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            showError("Could not save assets: " + ex.getMessage());
        }
    }

    private void loadAssets() {
        if (!Files.exists(DATA_FILE)) return;
        int lineNumber = 0;
        try (BufferedReader reader = Files.newBufferedReader(DATA_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] values = line.split("\\t", -1);
                if (values.length != COLUMNS.length) continue;
                for (int i = 0; i < values.length; i++) values[i] = unescape(values[i]);
                try {
                    int id = Integer.parseInt(values[0]);
                    double price = Double.parseDouble(values[3]);
                    int year = Integer.parseInt(values[4]);
                    if (id <= 0 || !Double.isFinite(price) || price < 0 || year <= 0 || findRowById(values[0]) >= 0) continue;
                    tableModel.addRow(new Object[] {id, values[1], values[2], price, year, values[5], values[6]});
                } catch (NumberFormatException ignored) {
                    // Skip malformed rows while continuing to load valid records.
                }
            }
        } catch (IOException ex) {
            showError("Could not load " + DATA_FILE + " (near line " + lineNumber + "): " + ex.getMessage());
        }
    }

    private int findRowById(String id) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (tableModel.getValueAt(row, 0).toString().equals(id)) return row;
        }
        return -1;
    }

    private void copySelectedRowToFields() {
        int modelRow = assetTable.convertRowIndexToModel(assetTable.getSelectedRow());
        for (int col = 0; col < fields.length; col++) fields[col].setText(tableModel.getValueAt(modelRow, col).toString());
    }

    private void clearFields() {
        for (JTextField field : fields) field.setText("");
        assetTable.clearSelection();
        fields[0].requestFocusInWindow();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String unescape(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == '\\' && i + 1 < value.length()) {
                char next = value.charAt(++i);
                switch (next) {
                    case 't': result.append('\t'); break;
                    case 'n': result.append('\n'); break;
                    case 'r': result.append('\r'); break;
                    case '\\': result.append('\\'); break;
                    default: result.append('\\').append(next);
                }
            } else result.append(ch);
        }
        return result.toString();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Asset Management", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
