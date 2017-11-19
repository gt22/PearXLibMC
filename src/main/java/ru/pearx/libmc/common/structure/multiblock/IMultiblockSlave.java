package ru.pearx.libmc.common.structure.multiblock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/*
 * Created by mrAppleXZ on 12.11.17 16:00.
 */
public interface IMultiblockSlave extends IMultiblockPart
{
    BlockPos getMasterPos();
    void setMasterPos(BlockPos pos);
}
