package github.shinyoki.bluemap.xjcraft.marker.strategy;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.marker.HtmlMarker;
import de.bluecolored.bluemap.api.marker.Marker;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import github.shinyoki.bluemap.xjcraft.enums.MarkerType;
import github.shinyoki.bluemap.xjcraft.utils.BlueMapUtil;
import org.bukkit.Location;

import java.util.Objects;

/**
 * 创建Marker的策略：HTML实现
 * @author senko
 * @date 2022/8/14 8:27
 */
public class HtmlMarkerCreator implements ICreator {

    private String defaultHtml = "<span style=\"text-align: center\">%s</span>";
    private String html = null;
    private StringBuilder sb;

    public HtmlMarkerCreator() {
    }
    public HtmlMarkerCreator(Object html) {
        this.html = this.defaultHtml.replace("%s", html.toString());
    }

    private String getResult() {
        return this.defaultHtml.replace("%s", sb.toString());
    }

    @Override
    public Marker createMarker(String markerId, String markerLabel, MarkerSet markerSet, BlueMapMap map, Location location, MarkerType markerType) {
        if (Objects.nonNull(html)) {
            HtmlMarker htmlMarker = markerSet.createHtmlMarker(markerId, map, new Vector3d(location.getX(), location.getY(), location.getZ()), html);
            htmlMarker.setLabel(markerLabel);
            return basicEditHtmlMarker(htmlMarker);
        }

        appendIcon(markerType);

        HtmlMarker htmlMarker = markerSet.createHtmlMarker(markerId, map, new Vector3d(location.getX(), location.getY(), location.getZ()), getResult());
        htmlMarker.setLabel(markerLabel);
        return basicEditHtmlMarker(htmlMarker);
    }

    private HtmlMarker basicEditHtmlMarker(HtmlMarker htmlMarker) {
        htmlMarker.setAnchor(0, 0);
        htmlMarker.setMinDistance(5);
        htmlMarker.setMaxDistance(600);
        return htmlMarker;
    }

    protected void appendIcon(MarkerType markerType) {
        sb = new StringBuilder();
        switch (markerType) {
            case BASE:
                sb.append("<svg t=\"1660370020332\" class=\"icon\" viewBox=\"0 0 1024 1024\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" p-id=\"2899\" width=\"15\" height=\"15\"><path d=\"M512 160v832H160V288H64V32h128v128h128V32h128v128z\" fill=\"#E0EBFE\" p-id=\"2900\"></path><path d=\"M400 718.208v-190.08c0-54.08 50.4-98.336 112-98.336s112 44.256 112 98.368v190.048h-224z\" fill=\"#E0EBFE\" p-id=\"2901\"></path><path d=\"M64 320h64v672a32 32 0 0 0 32 32h704a32 32 0 0 0 32-32V320h64a32 32 0 0 0 32-32V32a32 32 0 0 0-32-32h-128a32 32 0 0 0-32 32v96h-64V32a32 32 0 0 0-32-32h-128a32 32 0 0 0-32 32v96h-64V32a32 32 0 0 0-32-32h-128a32 32 0 0 0-32 32v96H224V32a32 32 0 0 0-32-32H64a32 32 0 0 0-32 32v256a32 32 0 0 0 32 32z m32-256h64v96a32 32 0 0 0 32 32h128a32 32 0 0 0 32-32V64h64v96a32 32 0 0 0 32 32h128a32 32 0 0 0 32-32V64h64v96a32 32 0 0 0 32 32h128a32 32 0 0 0 32-32V64h64v192h-64a32 32 0 0 0-32 32v672H192V288a32 32 0 0 0-32-32H96V64z\" fill=\"#5465CF\" p-id=\"2902\"></path><path d=\"M400 734.208h224a16 16 0 0 0 16-16v-190.08c0-63.04-57.408-114.336-128-114.336s-128 51.296-128 114.336v190.08a16 16 0 0 0 16 16z m16-206.08c0-45.408 43.072-82.336 96-82.336s96 36.928 96 82.336v174.08h-192v-174.08z\" fill=\"#5465CF\" p-id=\"2903\"></path></svg>");
                break;
            case HOME:
                sb.append("<svg t=\"1660369846448\" class=\"icon\" viewBox=\"0 0 1024 1024\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" p-id=\"2753\" width=\"15\" height=\"15\"><path d=\"M128 320l384-288 384 288zM272 592h482.976V992H272z\" fill=\"#E0EBFE\" p-id=\"2754\"></path><path d=\"M924.352 305.856c-0.896-1.792-2.112-3.296-3.328-4.928a31.104 31.104 0 0 0-4.352-4.864c-0.576-0.512-0.864-1.184-1.472-1.664l-384-288a32 32 0 0 0-38.4 0l-384 288c-0.64 0.48-0.896 1.152-1.472 1.664a31.104 31.104 0 0 0-4.352 4.896c-1.216 1.6-2.432 3.104-3.328 4.896-0.864 1.76-1.344 3.616-1.92 5.536a31.136 31.136 0 0 0-1.28 6.432C96.384 318.592 96 319.232 96 320v672a32 32 0 0 0 32 32h768a32 32 0 0 0 32-32V320c0-0.768-0.384-1.408-0.448-2.176a31.136 31.136 0 0 0-1.28-6.432c-0.576-1.92-1.056-3.776-1.92-5.536zM512 72L800 288H224l288-216zM736 960H288V608h448v352z m128 0h-96V592a16 16 0 0 0-16-16h-480a16 16 0 0 0-16 16V960H160V352h704v608z\" fill=\"#5465CF\" p-id=\"2755\"></path><path d=\"M368 704h288a16 16 0 1 0 0-32h-288a16 16 0 1 0 0 32zM368 800h288a16 16 0 1 0 0-32h-288a16 16 0 1 0 0 32zM368 896h288a16 16 0 1 0 0-32h-288a16 16 0 1 0 0 32z\" fill=\"#5465CF\" p-id=\"2756\"></path></svg>");
                break;
            case DEFAULT:
                sb.append("<img src=\"")
                        .append(BlueMapUtil.getBlueMapIcon(BlueMapUtil.BaseIcon.DEFAULT))
                        .append("\"/>");
                break;
        }
    }

}
