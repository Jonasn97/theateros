package de.hsos.swa.jonas.theater.userdata.entity;

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
    public String getLabel() {
        return germanLabel;
    }
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
