package org.xjcraft.senkosan.bluemap.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jim.bukkit.audit.Status;
import org.jim.bukkit.audit.apply.StatusChangeEvent;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.manager.DefaultBlueMapManager;
import org.xjcraft.senkosan.bluemap.manager.XJCraftBlueMapContext;


import static org.jim.bukkit.audit.Status.*;

/**
 * 认证状态改变监听器，即时渲染新的标记
 *
 * @author senko
 * @date 2022/8/15 21:37
 */
public class AuditStatusChangeListener implements Listener {

    /**
     * priority: HIGHEST，最后触发，此时可以获取PlayerMeta信息
     * ignoreCancelled: true，取消事件的话不会触发
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAuditStatusChange(StatusChangeEvent event) {
        Status status = event.getStatus();
        if (APPLIED.equals(status) || UNAPPLIED.equals(status)) {
            return;
        }

        String name = event.getPlayer().getName();

        DefaultBlueMapManager manager = XJCraftBlueMapContext.getBlueMapManager();
        manager.renderMarkerAsynchronously(name);

    }

}
