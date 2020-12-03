package no.ntnu.mobapp20g6.app1.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 * @author nilsjha
 */
class LoggedInUserView {
    private String displayName;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}