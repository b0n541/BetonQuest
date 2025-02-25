package org.betonquest.betonquest;

import org.betonquest.betonquest.api.MobKillNotifier;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Listens to standard kills and adds them to MobKillNotifier.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MobKillListener implements Listener {

    public MobKillListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(final EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Profile killer = PlayerConverter.getID(entity.getKiller());
        MobKillNotifier.addKill(killer, entity);
    }
}
