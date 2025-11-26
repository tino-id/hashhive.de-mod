package codes.tino.hashhive;

public class ModConfig {
    private static ModConfig instance;
    private boolean enabled = true; // Enabled by default
    private boolean autoSubmitEnabled = false; // Disabled by default

    private ModConfig() {}

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = new ModConfig();
        }
        return instance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public boolean isAutoSubmitEnabled() {
        return autoSubmitEnabled;
    }

    public void setAutoSubmitEnabled(boolean autoSubmitEnabled) {
        this.autoSubmitEnabled = autoSubmitEnabled;
    }

    public void toggleAutoSubmit() {
        this.autoSubmitEnabled = !this.autoSubmitEnabled;
    }
}
