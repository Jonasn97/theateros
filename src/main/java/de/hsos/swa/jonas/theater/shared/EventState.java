package de.hsos.swa.jonas.theater.shared;

/**
 * Enum for the different states of an userevent
 *
 */
public enum EventState {
    INTERESTED("Interessiert"),
    NOT_INTERESTED("Kein Interesse"),
    UNSURE("Unsicher"),
    MISS_OUT("Verpasst"),
    SEEN("Gesehen"),
    NONE("Nicht gesetzt");

    private final String germanLabel;

    EventState(String germanLabel) {
        this.germanLabel = germanLabel;
    }

    /**
     * @return the german label of the state
     */
    public String getLabel() {
        return germanLabel;
    }

    /**
     * @return the css class of the state
     */
    public String getCssClass() {
        switch (this) {
            case SEEN:
                return "seen-state";
            case INTERESTED:
                return "interested-state";
            case NOT_INTERESTED:
                return "not-interested-state";
            case UNSURE:
                return "unsure-state";
            case MISS_OUT:
                return "miss-out-state";
            case NONE:
            default:
                return "none-state";
        }
    }
}
