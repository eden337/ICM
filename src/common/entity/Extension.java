package common.entity;

import java.io.Serializable;

public class Extension implements Serializable {
    private int requestID;
    private String Stage;
    private int days;
    private String reason;
    private String decision;

    @Override
    public String toString() {
        return "Extension{" +
                "requestID=" + requestID +
                ", Stage='" + Stage + '\'' +
                ", days=" + days +
                ", reason='" + reason + '\'' +
                ", decision='" + decision + '\'' +
                '}';
    }

    public Extension(int requestID, String stage, int days, String reason, String decision) {
        this.requestID = requestID;
        Stage = stage;
        this.days = days;
        this.reason = reason;
        this.decision = decision;
    }

    public int getRequestID() {
        return requestID;
    }

    public String getStage() {
        return Stage;
    }

    public int getDays() {
        return days;
    }

    public String getReason() {
        return reason;
    }

    public String getDecision() {
        return decision;
    }


}
