package ru.pearx.libmc.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.libmc.client.models.IModelProvider;

/**
 * Created by mrAppleXZ on 08.04.17 19:01.
 */
public class BlockBase extends Block implements IModelProvider
{
    public BlockBase(Material materialIn)
    {
        super(materialIn);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setupModels()
    {
        StateMap.Builder bld = new StateMap.Builder();
        for(IProperty prop : getBlockState().getProperties())
        {
            bld.ignore(prop);
        }
        ModelLoader.setCustomStateMapper(this, bld.build());
    }
}
