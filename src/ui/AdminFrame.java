package ui;

import dao.AdminDAO;
import dao.ReportDAO;
import service.VerifyService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminFrame extends JFrame {

    private final AdminDAO adminDAO = new AdminDAO();
    private final VerifyService verifyService = new VerifyService(new ReportDAO(), adminDAO);

    private DefaultTableModel tableModel;
    private JTable table;

    public AdminFrame() {
        setTitle("CyberShield — Admin Review Queue");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        tableModel = new DefaultTableModel(
                new Object[]{"Identifier", "Type", "Reports", "First Reported", "Last Reported"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");
        JButton refreshBtn = new JButton("Refresh");
        buttons.add(approveBtn);
        buttons.add(rejectBtn);
        buttons.add(refreshBtn);
        add(buttons, BorderLayout.SOUTH);

        approveBtn.addActionListener(e -> handleAction(true));
        rejectBtn.addActionListener(e -> handleAction(false));
        refreshBtn.addActionListener(e -> loadData());

        loadData();
    }

    private List<AdminDAO.ReviewItem> currentItems;

    private void loadData() {
        tableModel.setRowCount(0);
        currentItems = adminDAO.getNeedsReview();
        for (AdminDAO.ReviewItem item : currentItems) {
            tableModel.addRow(new Object[]{
                    item.maskedValue, item.type, item.reportCount,
                    item.firstReported, item.lastReported
            });
        }
    }

    private void handleAction(boolean approve) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }
        AdminDAO.ReviewItem item = currentItems.get(row);
        if (approve) {
            verifyService.approve(item.identifierId);
        } else {
            verifyService.reject(item.identifierId);
        }
        loadData();
    }
}
