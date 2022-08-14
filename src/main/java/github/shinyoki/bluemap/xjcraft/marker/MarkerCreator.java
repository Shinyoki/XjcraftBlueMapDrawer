package github.shinyoki.bluemap.xjcraft.marker;

import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.marker.Marker;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import github.shinyoki.bluemap.xjcraft.enums.MarkerType;
import github.shinyoki.bluemap.xjcraft.marker.strategy.HtmlMarkerCreator;
import github.shinyoki.bluemap.xjcraft.marker.strategy.ICreator;
import org.bukkit.Location;

/**
 * Marker的创建者
 *
 * @author senko
 * @date 2022/8/14 8:17
 */
public enum MarkerCreator {

    HTML("html", new HtmlMarkerCreator());

    MarkerCreator(String creatorName,ICreator creator) {
        this.creatorName = creatorName;
        this.creator = creator;
    }

    public ICreator getCreator() {
        return creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    private String creatorName;
    private ICreator creator;

    public Marker createMarker(String markerId, String markerLabel, MarkerSet markerSet, BlueMapMap map, Location location, MarkerType markerType) {
        return creator.createMarker(markerId, markerLabel, markerSet, map, location, markerType);
    }

}


