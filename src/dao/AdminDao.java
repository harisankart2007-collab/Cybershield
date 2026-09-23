package dao;
import util.DBConnection; import util.Constants;
import java.sql.*; import java.util.ArrayList; import java.util.List;
public class AdminDAO {
/** One row for the admin table: an identifier waiting on manual review. */
public static class ReviewItem {
    public int identifierId;
    public String type;          // PHONE / UPI / EMAIL / OTHER
    public String maskedValue;   // don't show the raw hash — see note below
    public int reportCount;
    public Timestamp firstReported;
    public Timestamp lastReported;
}

public List<ReviewItem> getNeedsReview() {
    String sql = """
        SELECT si.identifier_id, si.type,
               COUNT(r.report_id) AS report_count,
               MIN(r.report_date) AS first_reported,
               MAX(r.report_date) AS last_reported
        FROM scam_identifiers si
        JOIN reports r ON r.identifier_id = si.identifier_id
        WHERE si.status = ?
        GROUP BY si.identifier_id, si.type
        ORDER BY last_reported DESC
        """;

    List<ReviewItem> items = new ArrayList<>();
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, Constants.IDENTIFIER_NEEDS_REVIEW);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ReviewItem item = new ReviewItem();
                item.identifierId = rs.getInt("identifier_id");
                item.type = rs.getString("type");
                item.reportCount = rs.getInt("report_count");
                item.firstReported = rs.getTimestamp("first_reported");
                item.lastReported = rs.getTimestamp("last_reported");
                // NOTE: we only store a SHA-256 hash, not the raw value, so
                // there's nothing human-readable to show here by design.
                // If the admin needs to see the actual number/email/UPI ID,
                // ask Gokul to store it (encrypted, not hashed) alongside
                // the hash on the report row for admin-eyes-only display.
                item.maskedValue = "(hash: " + rs.getString("identifier_id") + ")";
                items.add(item);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Failed to load review queue", e);
    }
    return items;
}

public void updateIdentifierStatus(int identifierId, String status) {
    String sql = "UPDATE scam_identifiers SET status = ? WHERE identifier_id = ?";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, status);
        ps.setInt(2, identifierId);
        ps.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException("Failed to update identifier status", e);
    }
}

public void updateRiskScore(int identifierId, double score, int uniqueReporterCount) {
    String sql = """
        UPDATE scam_identifiers
        SET risk_score = ?, unique_reporter_count = ?, last_reported_date = NOW()
        WHERE identifier_id = ?
        """;
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setDouble(1, score);
        ps.setInt(2, uniqueReporterCount);
        ps.setInt(3, identifierId);
        ps.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException("Failed to update risk score", e);
    }
}
}
