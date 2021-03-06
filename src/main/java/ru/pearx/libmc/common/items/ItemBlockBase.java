package ru.pearx.libmc.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.libmc.client.ClientUtils;
import ru.pearx.libmc.client.models.IModelProvider;

/**
 * Created by mrAppleXZ on 14.05.17 18:12.
 */
public class ItemBlockBase extends ItemBlock implements IModelProvider
{
    public ItemBlockBase(Block block)
    {
        super(block);
        setRegistryName(block.getRegistryName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setupModels()
    {
        ClientUtils.setModelLocation(this);
    }

    //Don't remove this! It will break everything just 'cos Item#getMetadata returns zero by default!
    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }
}
