package samj.mario.editor;

public enum ForegroundTileCategory {
    PLATFORM("Platforms"),
    INTERACTIVE("Interactive"),
    UNKNOWN("Unknown");

    private final String displayName;

    ForegroundTileCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
