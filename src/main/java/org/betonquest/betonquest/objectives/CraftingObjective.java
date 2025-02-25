package org.betonquest.betonquest.objectives;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.InventoryUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Player has to craft specified amount of items.
 */
@SuppressWarnings({"PMD.CommentRequired"})
@CustomLog
public class CraftingObjective extends CountingObjective implements Listener {

    private final QuestItem item;

    public CraftingObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "items_to_craft");
        item = instruction.getQuestItem();
        targetAmount = instruction.getInt();
        if (targetAmount <= 0) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
    }

    private static int calculateCraftAmount(final CraftItemEvent event) {
        final ItemStack result = event.getRecipe().getResult();
        final PlayerInventory inventory = event.getWhoClicked().getInventory();
        final ItemStack[] ingredients = event.getInventory().getMatrix();
        switch (event.getClick()) {
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                return InventoryUtils.calculateShiftCraftAmount(result, inventory, ingredients);
            case CONTROL_DROP:
                return InventoryUtils.calculateMaximumCraftAmount(result, ingredients);
            case NUMBER_KEY:
                return InventoryUtils.calculateSwapCraftAmount(result, inventory.getItem(event.getHotbarButton()));
            case SWAP_OFFHAND:
                return InventoryUtils.calculateSwapCraftAmount(result, inventory.getItemInOffHand());
            case DROP:
                return result.getAmount();
            case LEFT:
            case RIGHT:
                return InventoryUtils.calculateSimpleCraftAmount(result, event.getCursor());
            default:
                return 0;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCrafting(final CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            final Profile profile = PlayerConverter.getID((Player) event.getWhoClicked());
            if (containsPlayer(profile) && item.compare(event.getRecipe().getResult()) && checkConditions(profile)) {
                getCountingData(profile).progress(calculateCraftAmount(event));
                completeIfDoneOrNotify(profile);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }
}
