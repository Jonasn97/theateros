package de.hsos.swa.jonas.theater.dataprovider.entity;

public enum PerformanceType {
    LAST_PERFORMANCE("Letzte Vorstellung"),
    REVIVAL("Wiederaufnahme"),
    OPEN_REHEARSAL("Offene Probe"),
    PREMIERE("Premiere"),
    ADDITIONAL_PERFORMANCE("Zusatzvorstellung");

    private final String displayName;

    PerformanceType(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
