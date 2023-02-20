package sh.okx.railswitch;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftBlock;
import vg.civcraft.mc.civmodcore.utilities.MoreMath;

public class RedstoneUtils {

    public static void setRedstonePower(Block block, int power){
        if(block == null){
            return;
        }

        power = MoreMath.clamp(power, 0, 15);

        CraftBlock nmsBlock = (CraftBlock) block;
        net.minecraft.world.level.Level world = nmsBlock.getHandle().getMinecraftWorld();
        net.minecraft.world.level.block.state.BlockState iblockdata = world.getBlockState(new BlockPos(nmsBlock.getX(), nmsBlock.getY(), nmsBlock.getZ()));
        if (!iblockdata.is(Blocks.REDSTONE_WIRE)) {
            return;
        }

        iblockdata.setValue(RedStoneWireBlock.POWER, power);
        block.setBlockData(iblockdata.createCraftBlockData());
    }
}
