package sh.okx.railswitch.switches;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.level.block.DetectorRailBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Lectern;
import org.bukkit.block.Sign;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.Redstone;
import sh.okx.railswitch.RailSwitchPlugin;
import sh.okx.railswitch.RedstoneUtils;
import sh.okx.railswitch.glue.CitadelGlue;
import sh.okx.railswitch.settings.SettingsManager;
import sh.okx.railswitch.switches.rules.SwitchExpression;
import vg.civcraft.mc.civmodcore.world.WorldUtils;

/**
 * Switch listener that implements switch functionality.
 */
public class SwitchListener implements Listener {

    public static final String WILDCARD = "*";

    public static final CitadelGlue CITADEL_GLUE = new CitadelGlue(RailSwitchPlugin.getPlugin(RailSwitchPlugin.class));

    /**
     * Event handler for rail switches. Will determine if a switch exists at the target location, and if so will process
     * it accordingly, allowing it to trigger or not trigger depending on the rider's set destination, the listed
     * destinations on the switch, and the switch type.
     *
     * @param event The block redstone event to base the switch's existence on.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwitchTrigger(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        // Block must be a detector rail being triggered
        if (!WorldUtils.isValidBlock(block)
                || block.getType() != Material.DETECTOR_RAIL
                || event.getNewCurrent() != 15) {
            return;
        }

        List<String> dests;
        Player player = this.getPlayerInMinecartAt(block);
        if(player != null){
            dests = getPlayerDestinations(player);
        }else{
            Entity entity = this.getVehicle(block);
            if(entity == null){
                return;
            }

            String customName = entity.getCustomName();
            if(customName == null){
                return;
            }

            dests = Arrays.stream(customName.split(" ")).toList();
        }

        for(BlockFace face : WorldUtils.ALL_SIDES){
            int dist = face == BlockFace.DOWN ? 2 : 1;
            Block checkBlock = block.getRelative(face, dist);

            // If Citadel is enabled, check that the sign and the rail are on the same group
            if (CITADEL_GLUE.isSafeToUse()) {
                if (!CITADEL_GLUE.doSignAndRailHaveSameReinforcement(checkBlock, block)) {
                    continue;
                }
            }

            if(Tag.SIGNS.isTagged(checkBlock.getType())){
                Sign sign = (Sign) checkBlock.getState();
                List<String> lines = Arrays.stream(sign.getLines()).filter(str -> !Strings.isNullOrEmpty(str))
                        .toList();
                if(lines == null || lines.size() == 0){
                    return;
                }

                SwitchType type = SwitchType.find(lines.remove(0));
                if (type == null) {
                    continue;
                }

                SwitchExpression exp = SwitchExpression.compile(lines, type == SwitchType.INVERTED);
                RailSwitchPlugin.getInstance(RailSwitchPlugin.class).info(exp.toString());
                int result = exp.test(dests);
                RailSwitchPlugin.getInstance(RailSwitchPlugin.class).info("Test output: " + result);
                event.setNewCurrent(result);
                Bukkit.getScheduler().runTaskLater(RailSwitchPlugin.getInstance(RailSwitchPlugin.class), () -> {
                    for(BlockFace bf : WorldUtils.ALL_SIDES) {
                        int blockDist = face == BlockFace.DOWN ? 2 : 1;
                        Block rsBlock = block.getRelative(bf, blockDist);
                        DetectorRailBlock b = (DetectorRailBlock) rsBlock;
                        if(rsBlock.getType() == Material.REDSTONE_WIRE){
                            //AnaloguePowerable powerable = (AnaloguePowerable) rsBlock.getBlockData();
                            //powerable.setPower(result);
                            //rsBlock.setBlockData(powerable);
                            RedstoneUtils.setRedstonePower(rsBlock, result);
                        }
                    }
                }, 1L);

                /*
                event.setNewCurrent(type == SwitchType.NORMAL ?
                        (this.hasDestination(lines.toArray(new String[0]), player) ? 15 : 0) :
                        (this.hasDestination(lines.toArray(new String[0]), player) ? 0 : 15));
                 */
                return;
            }else if(checkBlock.getType() == Material.LECTERN){
                Lectern lectern = (Lectern) checkBlock.getState();
                ItemStack item = lectern.getInventory().getItem(0);
                if(item == null || (item.getType() != Material.WRITTEN_BOOK && item.getType() != Material.WRITABLE_BOOK)){
                    RailSwitchPlugin.getInstance(RailSwitchPlugin.class).info("Failed to get book from lectern");
                    continue;
                }

                BookMeta bookMeta = (BookMeta) item.getItemMeta();
                List<String> text = new ArrayList<>();
                bookMeta.getPages().stream().filter(str -> !Strings.isNullOrEmpty(str))
                        .map(page -> page.split("\n"))
                        .forEach(page -> text.addAll(List.of(page)));

                if(text.size() == 0){
                    continue;
                }

                String destType = text.remove(0);

                SwitchType type = SwitchType.find(destType);
                if (type == null) {
                    continue;
                }

                //String[] lines = text.toArray(new String[0]);
                SwitchExpression exp = SwitchExpression.compile(text, type == SwitchType.INVERTED);
                RailSwitchPlugin.getInstance(RailSwitchPlugin.class).info(exp.toString());
                int result = exp.test(dests);
                RailSwitchPlugin.getInstance(RailSwitchPlugin.class).info("Test output: " + result);
                event.setNewCurrent(result);
                Bukkit.getScheduler().runTaskLater(RailSwitchPlugin.getInstance(RailSwitchPlugin.class), () -> {
                    for(BlockFace bf : WorldUtils.ALL_SIDES) {
                        int blockDist = face == BlockFace.DOWN ? 2 : 1;
                        Block rsBlock = block.getRelative(bf, blockDist);

                        if(rsBlock.getType() == Material.REDSTONE_WIRE){
                            //AnaloguePowerable powerable = (AnaloguePowerable) rsBlock.getBlockData();
                            //powerable.setPower(result);
                            //rsBlock.setBlockData(powerable);
                            RedstoneUtils.setRedstonePower(rsBlock, result);
                        }
                    }
                }, 1L);

                /*
                boolean hasDest = this.hasDestination(lines, player);

                event.setNewCurrent(type == SwitchType.NORMAL ?
                        (hasDest ? 15 : 0) :
                        (hasDest ? 0 : 15));
                 */
                return;
            }
        }
    }

    private Entity getVehicle(Block block) {
        Entity ent = null; {
            double searchDistance = Double.MAX_VALUE;
            for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(), 3, 3, 3)) {
                if (!(entity instanceof Minecart)) {
                    continue;
                }
                Entity vehicle = entity;
                // TODO: This should be abstracted into CivModCore
                if (vehicle == null
                        || (vehicle.getType() != EntityType.MINECART
                        && vehicle.getType() != EntityType.MINECART_CHEST
                        && vehicle.getType() != EntityType.MINECART_FURNACE
                        && vehicle.getType() != EntityType.MINECART_HOPPER)) {
                    continue;
                }
                double distance = block.getLocation().distanceSquared(entity.getLocation());
                if (distance < searchDistance) {
                    searchDistance = distance;
                    ent = entity;
                }
            }
        }

        return ent;
    }

    // Check that a player is triggering the switch
    // NOTE: The event doesn't provide the information and so the next best thing is searching for a
    //       player who is nearby and riding a minecart.
    private Player getPlayerInMinecartAt(Block block){
        Player player = null; {
            double searchDistance = Double.MAX_VALUE;
            for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(), 3, 3, 3)) {
                if (!(entity instanceof Player)) {
                    continue;
                }
                Entity vehicle = entity.getVehicle();
                // TODO: This should be abstracted into CivModCore
                if (vehicle == null
                        || vehicle.getType() != EntityType.MINECART
                        || !(vehicle instanceof Minecart)) {
                    continue;
                }
                double distance = block.getLocation().distanceSquared(entity.getLocation());
                if (distance < searchDistance) {
                    searchDistance = distance;
                    player = (Player) entity;
                }
            }
        }

        return player;
    }

    private List<String> getPlayerDestinations(Player player){
        String setDest = SettingsManager.getDestination(player);
        if (!Strings.isNullOrEmpty(setDest)) {
            return Arrays.stream(setDest.split(" ")).toList();
        }

        return new ArrayList<>();
    }

}
