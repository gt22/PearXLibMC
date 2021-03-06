package ru.pearx.libmc.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.libmc.PXLMC;
import ru.pearx.libmc.client.ClientUtils;
import ru.pearx.libmc.common.structure.multiblock.Multiblock;

/*
 * Created by mrAppleXZ on 24.12.17 10:45.
 */
public class ItemMultiblock extends ItemBase
{
    public ItemMultiblock()
    {
        setRegistryName(new ResourceLocation(PXLMC.MODID, "multiblock"));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("multiblock", Constants.NBT.TAG_STRING) ? Multiblock.REGISTRY.getValue(new ResourceLocation(stack.getTagCompound().getString("multiblock"))).getName() : super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setupModels()
    {
        ClientUtils.setModelLocation(this);
    }

    public ItemStack newStack(ResourceLocation multiblockId)
    {
        return newStack(multiblockId.toString());
    }

    public ItemStack newStack(String multiblockId)
    {
        ItemStack stack = new ItemStack(this);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("multiblock", multiblockId);
        stack.setTagCompound(tag);
        return stack;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey("multiblock", Constants.NBT.TAG_STRING))
        {
            ResourceLocation id = new ResourceLocation(stack.getTagCompound().getString("multiblock"));
            Multiblock mb = Multiblock.REGISTRY.getValue(id);
            if(mb != null)
            {
                mb.form(worldIn, pos.up(), PXLMC.getRotation(player.getHorizontalFacing()), player);
            }
        }
        return EnumActionResult.FAIL;
    }
}
