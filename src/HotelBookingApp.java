import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class HotelBookingApp extends JFrame {

    // Database Connection Info
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BOOKING_DB";
    private static final String DB_USER = "root";
    private static final String DB_PASS = ""; 

    // GUI Components
    JTextField txtClientId = new JTextField();
    JTextField txtFirstName = new JTextField();
    JTextField txtLastName = new JTextField();
    JTextField txtMobile = new JTextField();
    JComboBox<String> cbRoomType = new JComboBox<>(new String[]{"-- Please Select --","Single", "Double", "Suite"});
    JTextField txtRoomRate = new JTextField();
    JTextField txtCheckIn = new JTextField();
    JTextField txtCheckOut = new JTextField();
    JTextField txtNumNights = new JTextField();
    JTextField txtTax = new JTextField();
    JTextField txtDiscount = new JTextField();
    JTextField txtTotalPrice = new JTextField();
    JButton btnSave = new JButton("SAVE");

    public HotelBookingApp() {

        setTitle("Hotel Booking System");
        setSize(450, 500);
        setLayout(new GridLayout(14, 2, 6, 6));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add Labels & Fields
        add(new JLabel("Client ID:")); add(txtClientId);
        add(new JLabel("First Name:")); add(txtFirstName);
        add(new JLabel("Last Name:")); add(txtLastName);
        add(new JLabel("Mobile Phone:")); add(txtMobile);
        add(new JLabel("Room Type:")); add(cbRoomType);
        add(new JLabel("Room Rate:")); add(txtRoomRate);
        add(new JLabel("Check-in Date (YYYY-MM-DD):")); add(txtCheckIn);
        add(new JLabel("Check-out Date (YYYY-MM-DD):")); add(txtCheckOut);
        add(new JLabel("Number of Nights:")); add(txtNumNights);
        add(new JLabel("Tax:")); add(txtTax);
        add(new JLabel("Discount:")); add(txtDiscount);
        add(new JLabel("Total Price:")); add(txtTotalPrice);
        add(new JLabel("")); add(btnSave);

        // Set computed fields readonly
        txtRoomRate.setEditable(false);
        txtNumNights.setEditable(false);
        txtTax.setEditable(false);
        txtDiscount.setEditable(false);
        txtTotalPrice.setEditable(false);

        // Event Listeners
        cbRoomType.addActionListener(e -> compute());

        // ✅ Validate when either date loses focus
        txtCheckIn.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (validateDates()) compute();
            }
        });
        txtCheckOut.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (validateDates()) compute();
            }
        });

        // ✅ Validation listeners for Client ID and Mobile
        txtClientId.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                validateClientId();
            }
        });

        txtMobile.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                validateMobile();
            }
        });

        btnSave.addActionListener(e -> saveBooking());

        setVisible(true);
    }

    // ✅ Validation methods
    private boolean validateClientId() {
        String id = txtClientId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Client ID is required.");
            txtClientId.requestFocus();
            return false;
        }
        try {
            Integer.parseInt(id);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Client ID must be a number.");
            txtClientId.requestFocus();
            return false;
        }
    }

    private boolean validateMobile() {
        String mobile = txtMobile.getText().trim();
        if (mobile.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mobile number is required.");
            txtMobile.requestFocus();
            return false;
        }
        if (!mobile.matches("^07\\d{8}$")) {
            JOptionPane.showMessageDialog(this, "Mobile must match format 07########");
            txtMobile.requestFocus();
            return false;
        }
        return true;
    }

    // ✅ New robust validation for check-in / check-out dates
    private boolean validateDates() {
        String inStr = txtCheckIn.getText().trim();
        String outStr = txtCheckOut.getText().trim();

        // Don’t validate if both are empty
        if (inStr.isEmpty() && outStr.isEmpty()) return false;

        LocalDate in = null;
        LocalDate out = null;

        // Validate format first
        try {
            if (!inStr.isEmpty()) in = LocalDate.parse(inStr);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Check-in date format. Use YYYY-MM-DD");
            txtCheckIn.requestFocus();
            return false;
        }

        try {
            if (!outStr.isEmpty()) out = LocalDate.parse(outStr);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Check-out date format. Use YYYY-MM-DD");
            txtCheckOut.requestFocus();
            return false;
        }

        // If both dates are filled, check logic
        if (in != null && out != null) {
            if (!out.isAfter(in)) {
                JOptionPane.showMessageDialog(this, "Check-out date must be after Check-in date.");
                txtCheckOut.requestFocus();
                return false;
            }
        }

        return true;
    }

    void compute() {

        String room = cbRoomType.getSelectedItem().toString();

        // If placeholder selected → clear all computed fields & stop
        if (room.equals("-- Please Select --")) {
            txtRoomRate.setText("");
            txtNumNights.setText("");
            txtTax.setText("");
            txtDiscount.setText("");
            txtTotalPrice.setText("");
            return;
        }

        long rate = switch (room) {
            case "Single" -> 70000;
            case "Double" -> 120000;
            case "Suite" -> 190000;
            default -> 0;
        };

        txtRoomRate.setText(String.valueOf(rate));

        try {
            LocalDate in = LocalDate.parse(txtCheckIn.getText().trim());
            LocalDate out = LocalDate.parse(txtCheckOut.getText().trim());
            long nights = ChronoUnit.DAYS.between(in, out);

            if (nights <= 0) return;

            txtNumNights.setText(String.valueOf(nights));

            double tax = rate * nights * 0.05;
            double discount = (nights >= 7) ? rate * nights * 0.10 : 0;
            double total = rate * nights + tax - discount;

            txtTax.setText(String.format("%.2f", tax));
            txtDiscount.setText(String.format("%.2f", discount));
            txtTotalPrice.setText(String.format("%.2f", total));

        } catch (Exception ignored) {}
    }

    void saveBooking() {

        // ✅ Validation before saving
        if (!validateClientId()) return;
        if (txtFirstName.getText().trim().isEmpty() || txtLastName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "First and Last Name are required.");
            return;
        }
        if (!validateMobile()) return;
        if (!validateDates()) return;

        if (cbRoomType.getSelectedItem().toString().equals("-- Please Select --")) {
            JOptionPane.showMessageDialog(this, "Please select a valid room type.");
            return;
        }

        if (txtTotalPrice.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Price not calculated. Check room type and dates.");
            return;
        }

        // ✅ Now save to DB
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO BOOKING_DATA VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"
            );

            ps.setInt(1, Integer.parseInt(txtClientId.getText()));
            ps.setString(2, txtFirstName.getText());
            ps.setString(3, txtLastName.getText());
            ps.setString(4, txtMobile.getText());
            ps.setString(5, cbRoomType.getSelectedItem().toString());
            ps.setDouble(6, Double.parseDouble(txtRoomRate.getText()));
            ps.setDate(7, java.sql.Date.valueOf(txtCheckIn.getText()));
            ps.setDate(8, java.sql.Date.valueOf(txtCheckOut.getText()));
            ps.setInt(9, Integer.parseInt(txtNumNights.getText()));
            ps.setDouble(10, Double.parseDouble(txtTax.getText()));
            ps.setDouble(11, Double.parseDouble(txtDiscount.getText()));
            ps.setDouble(12, Double.parseDouble(txtTotalPrice.getText()));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Booking Saved Successfully!");
            conn.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelBookingApp::new);
    }
}
