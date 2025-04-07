import javax.swing.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;

public class BuchungGUI extends JFrame {
    private JTextField txtBetrag;
    private JTextField txtDatum;
    private JComboBox<Kategorie> ComboboxKategorieId;
    private JTextField txtBeschreibung;
    private JButton btnSpeichern;
    private JPanel panelMain;
    private JLabel LabelBetrag;
    private JLabel LabelDatum;
    private JLabel LabelKaregorieId;
    private JLabel LabelBeschreibung;

    public BuchungGUI() {
        setContentPane(panelMain);
        setTitle("Neue Buchung");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        ladeKategorienAusDatenbank();

        btnSpeichern.addActionListener(e -> {
            try {
                String betragStr = txtBetrag.getText();
                String datumStr = txtDatum.getText();
                Kategorie selected = (Kategorie) ComboboxKategorieId.getSelectedItem();
                String beschreibung = txtBeschreibung.getText();

                BigDecimal betrag = new BigDecimal(betragStr);
                java.util.Date utilDate = new SimpleDateFormat("dd.MM.yyyy").parse(datumStr);
                Date sqlDate = new Date(utilDate.getTime());
                int kategorieId = selected.getId();

                insertBuchung(betrag, sqlDate, kategorieId, beschreibung);
                JOptionPane.showMessageDialog(null, "Buchung erfolgreich gespeichert!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Fehler: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    private void ladeKategorienAusDatenbank() {
        String url = "jdbc:mysql://localhost:3306/itl12_buchhaltung";
        String user = "meinuser";
        String password = "geheim";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Treiber nicht gefunden: " + e.getMessage());
            return;
        }

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT id, name, typ FROM kategorie";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String typ = rs.getString("typ");
                    ComboboxKategorieId.addItem(new Kategorie(id, name, typ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Fehler beim Laden der Kategorien: " + e.getMessage());
        }
    }

    private void insertBuchung(BigDecimal betrag, Date datum, int kategorieId, String beschreibung) {
        String url = "jdbc:mysql://localhost:3306/itl12_buchhaltung";
        String user = "meinuser";
        String password = "geheim";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Treiber nicht gefunden: " + e.getMessage());
            return;
        }

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO buchung (betrag, datum, kategorie_id, beschreibung) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setBigDecimal(1, betrag);
                stmt.setDate(2, datum);
                stmt.setInt(3, kategorieId);
                stmt.setString(4, beschreibung);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Fehler beim Speichern: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new BuchungGUI();
    }

    static class Kategorie {
        private final int id;
        private final String name;
        private final String typ;

        public Kategorie(int id, String name, String typ) {
            this.id = id;
            this.name = name;
            this.typ = typ;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return id + " " + name + " (" + typ + ")";
        }
    }
}
