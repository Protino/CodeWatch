package io.github.protino.codewatch.model;

import java.util.List;

/**
 * @author Gurupad Mamadapur
 */

public class TopperItem {
    private List<DefaultLeaderItem> defaultLeaderItems;

    public TopperItem(List<DefaultLeaderItem> defaultLeaderItems) {
        this.defaultLeaderItems = defaultLeaderItems;
    }

    public List<DefaultLeaderItem> getDefaultLeaderItems() {
        return defaultLeaderItems;
    }

    public void setDefaultLeaderItems(List<DefaultLeaderItem> defaultLeaderItems) {
        this.defaultLeaderItems = defaultLeaderItems;
    }
}
