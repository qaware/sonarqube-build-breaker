package de.qaware.tools.sqbb.library.api.connector;

/**
 * Status of a quality gate.
 */
public enum QualityGateStatus {
    OK,
    WARNING,
    ERROR,
    NONE;

    /**
     * Converts the value from the SonarQube API to the enum value.
     *
     * @param value value from the SonarQube API
     * @return enum value
     */
    public static QualityGateStatus fromSonar(String value) {
        switch (value) {
            case "OK":
                return OK;
            case "WARN":
                return WARNING;
            case "ERROR":
                return ERROR;
            case "NONE":
                return NONE;
            default:
                throw new IllegalArgumentException("Unknown sonar status '" + value + "'");
        }
    }
}
