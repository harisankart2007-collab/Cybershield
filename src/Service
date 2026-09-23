package service;

import dao.ReportDAO;
import dao.AdminDAO;
import util.Constants;

import java.sql.Timestamp;
import java.util.List;

/**
 * Decides what happens to an identifier after a new report comes in.
 * Called by Gokul's ReportDAO.addReport() right after a report is inserted.
 *
 * NEEDS FROM TEAMMATES (confirm these exist with matching signatures):
 *   - Gokul's ReportDAO:
 *       List<ReportInfo> getReportsForIdentifier(int identifierId)
 *         -> each row needs: reporterId, hasEvidence, reportDate, reporterAccountCreatedAt
 *       void updateStatus(int reportId, String status)
 *   - Diva's identifier DAO (SearchDAO or a new ScamIdentifierDAO):
 *       void updateIdentifierStatus(int identifierId, String status)
 *       void updateRiskScore(int identifierId, double score, int uniqueReporterCount)
 *       String getCategory(int identifierId)   // or pull it off the reports instead
 *
 * If those exact method names don't exist yet, this is the spec to hand
 * Gokul/Diva so the signatures line up.
 */
public class VerifyService {

    private final ReportDAO reportDAO;
    private final AdminDAO adminDAO; // wraps identifier-status/risk updates for now

    public VerifyService(ReportDAO reportDAO, AdminDAO adminDAO) {
        this.reportDAO = reportDAO;
        this.adminDAO = adminDAO;
    }

    public void process(int identifierId) {
        List<ReportDAO.ReportInfo> reports = reportDAO.getReportsForIdentifier(identifierId);

        // Only reports with evidence, from accounts old enough, count toward verification
        List<ReportDAO.ReportInfo> eligible = reports.stream()
                .filter(r -> r.hasEvidence)
                .filter(r -> accountAgeDays(r.reporterAccountCreatedAt) >= Constants.MIN_ACCOUNT_AGE_DAYS)
                .toList();

        long uniqueReporters = eligible.stream()
                .map(r -> r.reporterId)
                .distinct()
                .count();

        if (uniqueReporters < Constants.MIN_UNIQUE_REPORTERS) {
            adminDAO.updateIdentifierStatus(identifierId, Constants.IDENTIFIER_UNCONFIRMED);
            return;
        }

        if (looksSuspicious(eligible)) {
            adminDAO.updateIdentifierStatus(identifierId, Constants.IDENTIFIER_NEEDS_REVIEW);
            return; // admin will approve/reject manually — see AdminDAO.approve()
        }

        // Clean case: auto-verify
        for (ReportDAO.ReportInfo r : eligible) {
            reportDAO.updateStatus(r.reportId, Constants.REPORT_VERIFIED);
        }
        adminDAO.updateIdentifierStatus(identifierId, Constants.IDENTIFIER_FLAGGED);
        recalculateRisk(identifierId, eligible);
    }

    /**
     * Flags a burst of reports arriving suspiciously close together, or a
     * cluster of very new accounts all reporting the same thing at once —
     * both classic signs of a coordinated fake report rather than real
     * independent complaints.
     */
    private boolean looksSuspicious(List<ReportDAO.ReportInfo> eligible) {
        Timestamp earliest = eligible.stream()
                .map(r -> r.reportDate)
                .min(Timestamp::compareTo)
                .orElseThrow();
        Timestamp latest = eligible.stream()
                .map(r -> r.reportDate)
                .max(Timestamp::compareTo)
                .orElseThrow();

        long gapMinutes = (latest.getTime() - earliest.getTime()) / (60 * 1000);
        if (gapMinutes < Constants.BURST_WINDOW_MINUTES) {
            return true;
        }

        // majority of reporters created their account within the last N days
        long freshAccounts = eligible.stream()
                .filter(r -> accountAgeDays(r.reporterAccountCreatedAt) <= 2 * Constants.MIN_ACCOUNT_AGE_DAYS)
                .count();
        return freshAccounts > eligible.size() / 2;
    }

    public void recalculateRisk(int identifierId, List<ReportDAO.ReportInfo> eligible) {
        long uniqueReporters = eligible.stream().map(r -> r.reporterId).distinct().count();

        int severity = eligible.stream()
                .map(r -> Constants.SEVERITY_WEIGHTS.getOrDefault(r.category, Constants.DEFAULT_SEVERITY))
                .max(Integer::compareTo)
                .orElse(Constants.DEFAULT_SEVERITY);

        long mostRecentDaysAgo = eligible.stream()
                .mapToLong(r -> daysSince(r.reportDate))
                .min()
                .orElse(0);
        double recencyDecay = Math.exp(-mostRecentDaysAgo / 30.0);

        double score = uniqueReporters * severity * recencyDecay;

        adminDAO.updateRiskScore(identifierId, score, (int) uniqueReporters);
    }

    // --- admin actions, called from AdminFrame buttons ---

    /** Admin clicks Approve on a NEEDS_REVIEW identifier. */
    public void approve(int identifierId) {
        List<ReportDAO.ReportInfo> eligible = reportDAO.getReportsForIdentifier(identifierId).stream()
                .filter(r -> r.hasEvidence)
                .toList();
        for (ReportDAO.ReportInfo r : eligible) {
            reportDAO.updateStatus(r.reportId, Constants.REPORT_VERIFIED);
        }
        adminDAO.updateIdentifierStatus(identifierId, Constants.IDENTIFIER_FLAGGED);
        recalculateRisk(identifierId, eligible);
    }

    /** Admin clicks Reject on a NEEDS_REVIEW identifier. */
    public void reject(int identifierId) {
        List<ReportDAO.ReportInfo> reports = reportDAO.getReportsForIdentifier(identifierId);
        for (ReportDAO.ReportInfo r : reports) {
            reportDAO.updateStatus(r.reportId, Constants.REPORT_REJECTED);
        }
        adminDAO.updateIdentifierStatus(identifierId, Constants.IDENTIFIER_UNCONFIRMED);
    }

    private long accountAgeDays(Timestamp createdAt) {
        return daysSince(createdAt);
    }

    private long daysSince(Timestamp t) {
        return (System.currentTimeMillis() - t.getTime()) / (1000L * 60 * 60 * 24);
    }
}
