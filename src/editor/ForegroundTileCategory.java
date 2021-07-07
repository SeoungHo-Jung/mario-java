package editor;

public enum ForegroundTileCategory {
    PLATFORM("Platforms"),
    INTERACTIVE("Interactive");

    private final String displayName;

    ForegroundTileCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
