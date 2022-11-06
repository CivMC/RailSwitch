package sh.okx.railswitch.switches;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import sh.okx.railswitch.RailSwitchPlugin;
import sh.okx.railswitch.glue.CitadelGlue;
import sh.okx.railswitch.settings.SettingsManager;
import vg.civcraft.mc.civmodcore.world.WorldUtils;

/**
 * Switch listener that implements switch functionality.
 */
public class SwitchListener implements Listener {

    public static final CitadelGlue CITADEL_GLUE = new CitadelGlue(RailSwitchPlugin.getPlugin(RailSwitchPlugin.class));

    /**
     * Event handler for rail switches. Will determine if a switch exists at the target location, and if so will process
     * it accordingly, allowing it to trigger or not trigger depending on the rider's set destination, the listed
     * destinations on the switch, and the switch type.
     *
     * @param event The block redstone event to base the switch's existence on.
     */
    @EventHandler
    public void onSwitchTrigger(BlockRedstoneEvent event) {
        Block rail = event.getBlock();
        // Block must be a detector rail being triggered
        if (!WorldUtils.isValidBlock(rail)
                || rail.getType() != Material.DETECTOR_RAIL
                || event.getNewCurrent() != 15) {
            return;
        }

        // Search for a rail sign and use the first one found

        boolean sign_found = false;
        Block sign = null;
        String[] lines = null;
        SwitchLogic logic = null;

        Block[] sign_locations = new Block[] {
            //Above the rail
            rail.getRelative(BlockFace.UP),
            //On the sides of the rail
            rail.getRelative(BlockFace.NORTH),
            rail.getRelative(BlockFace.WEST),
            rail.getRelative(BlockFace.EAST),
            rail.getRelative(BlockFace.SOUTH)
        };

        for (Block block : sign_locations) {
            if (block == null) continue;

            // Check that the block is a sign
            if (!Tag.SIGNS.isTagged(block.getType())
                    || !(block.getState() instanceof Sign)) {
                continue;
            }

            // Check that the sign has a valid switch type
            lines = ((Sign) block.getState()).getLines();
            /*type = SwitchType.find(lines[0]);
            if (type == null) {
                continue;
            }*/
            try {
                logic = SwitchLogic.try_create(lines);
                sign = block;
                sign_found = true;
                break;
            }
            catch (Exception e) {
                emit_failure_smoke(block.getLocation());
                continue;
            }
        }

        if (!sign_found) return;

        // Check that a player is triggering the switch
        // NOTE: The event doesn't provide the information and so the next best thing is searching for a
        //       player who is nearby and riding a minecart.
        Player player = null; {
            double searchDistance = Double.MAX_VALUE;
            for (Entity entity : rail.getWorld().getNearbyEntities(rail.getLocation(), 3, 3, 3)) {
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
                double distance = rail.getLocation().distanceSquared(entity.getLocation());
                if (distance < searchDistance) {
                    searchDistance = distance;
                    player = (Player) entity;
                }
            }
        }
        if (player == null) {
            return;
        }
        // If Citadel is enabled, check that the sign and the rail are on the same group
        if (CITADEL_GLUE.isSafeToUse()) {
            if (!CITADEL_GLUE.doSignAndRailHaveSameReinforcement(sign, rail)) {
                return;
            }
        }
        
        //Do the rail switching
        String dest_string = SettingsManager.getDestination(player);
        try {
            if (logic.decide(player)) event.setNewCurrent(15);
            else event.setNewCurrent(0);
        } catch (Exception e) {
            emit_failure_smoke(sign.getLocation());
            event.setNewCurrent(0);
        }
    }

    /** Emit a campfire particle at the location of the sign to indicate failure */
    public void emit_failure_smoke(Location block_coordinates) {
        Location location = block_coordinates.add(0.5, 0.5, 0.5);
        location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location, 0, 0.0, 0.0, 0.0, 3.0);
    }

}
