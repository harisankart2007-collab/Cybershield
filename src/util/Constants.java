package util;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared constants. Hari owns this file — merge these into his version,
 * don't create a second Constants.java.
 */
public class Constants {

    // Report status
    public static final String REPORT_PENDING = "PENDING";
    public static final String REPORT_VERIFIED = "VERIFIED";
    public static final String REPORT_REJECTED = "REJECTED";

    // Identifier status
    public static final String IDENTIFIER_UNCONFIRMED = "UNCONFIRMED";
    public static final String IDENTIFIER_FLAGGED = "FLAGGED";
    public static final String IDENTIFIER_NEEDS_REVIEW = "NEEDS_REVIEW";

    // Verification thresholds — tune these for the demo
    public static final int MIN_UNIQUE_REPORTERS = 3;
    public static final int MIN_ACCOUNT_AGE_DAYS = 1;   // small on purpose so demo accounts qualify
    public static final int BURST_WINDOW_MINUTES = 30;

    // Category severity weights — extend as needed
    public static final Map<String, Integer> SEVERITY_WEIGHTS = new HashMap<>();
    static {
        SEVERITY_WEIGHTS.put("PHISHING", 3);
        SEVERITY_WEIGHTS.put("FAKE_JOB_OFFER", 3);
        SEVERITY_WEIGHTS.put("INVESTMENT_SCAM", 5);
        SEVERITY_WEIGHTS.put("PAYMENT_FRAUD", 4);
        SEVERITY_WEIGHTS.put("OTHER", 2);
    }
    public static final int DEFAULT_SEVERITY = 2;
}
