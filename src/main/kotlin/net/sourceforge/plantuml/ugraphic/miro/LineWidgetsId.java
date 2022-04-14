package net.sourceforge.plantuml.ugraphic.miro;

public class LineWidgetsId {

    private final String startWidgetId;
    private final String endWidgetId;

    public LineWidgetsId(String startWidgetId, String endWidgetId) {
        this.startWidgetId = startWidgetId;
        this.endWidgetId = endWidgetId;
    }

    public String getStartWidgetId() {
        return startWidgetId;
    }

    public String getEndWidgetId() {
        return endWidgetId;
    }
}
