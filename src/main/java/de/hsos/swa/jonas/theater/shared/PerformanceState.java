package de.hsos.swa.jonas.theater.shared;

public enum PerformanceState {
    UNSURE("Unsicher"),
    BOUGHT_TICKET("Ticket gekauft"),
    PLAN_TO_VISIT("Besuch geplant"),
    MISS_OUT("Verpasst"),
    SEEN("Gesehen"),
    NONE("Nicht gesetzt");

    private final String germanLabel;

    PerformanceState(String germanLabel) {
        this.germanLabel = germanLabel;
    }

    public String getLabel() {
        return germanLabel;
    }

    public String getCssClass() {
        switch (this) {
            case SEEN:
                return "seen-state";
            case BOUGHT_TICKET:
            case PLAN_TO_VISIT:
                return "interested-state";
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