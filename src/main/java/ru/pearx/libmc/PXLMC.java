package ru.pearx.libmc;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.logging.log4j.Logger;
import ru.pearx.libmc.common.CommonProxy;
import ru.pearx.libmc.common.PXLCapabilities;
import ru.pearx.libmc.common.networking.packets.CPacketOpenStructureCreationGui;
import ru.pearx.libmc.common.networking.packets.CPacketSyncASMState;
import ru.pearx.libmc.common.networking.packets.SPacketCreateStructure;
import ru.pearx.libmc.common.structure.CommandStructure;
import ru.pearx.libmc.common.structure.processors.LootProcessor;
import ru.pearx.libmc.common.structure.processors.StructureProcessor;
import ru.pearx.libmc.common.tiles.PXLTiles;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*
 * Created by mrAppleXZ on 10.07.17 21:39.
 */
@Mod(modid = PXLMC.MODID, version = PXLMC.VERSION, acceptedMinecraftVersions = "", name = PXLMC.NAME)
public class PXLMC
{
    public static final String NAME = "PearXLib MC";
    public static final String MODID = "pxlmc";
    public static final String VERSION = "@VERSION@";

    @SidedProxy(clientSide = "ru.pearx.libmc.client.ClientProxy", serverSide = "ru.pearx.libmc.server.ServerProxy")
    public static CommonProxy PROXY;

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    private static Logger log;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent e)
    {
        ModMetadata data = e.getModMetadata();
        data.url = "https://minecraft.curseforge.com/projects/pearxlib-mc";
        data.authorList = Collections.singletonList("mrAppleXZ");
        data.autogenerated = false;
        data.description = "A common library for all MC mods by PearX Team.";
        data.version = VERSION;
        data.modId = MODID;
        data.name = NAME;

        log = e.getModLog();

        PXLCapabilities.register();
        PXLTiles.setup();

        StructureProcessor.REGISTRY.register(new LootProcessor());
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent e)
    {
        NETWORK.registerMessage(CPacketSyncASMState.Handler.class, CPacketSyncASMState.class, 0, Side.CLIENT);
        NETWORK.registerMessage(CPacketOpenStructureCreationGui.Handler.class, CPacketOpenStructureCreationGui.class, 1, Side.CLIENT);
        NETWORK.registerMessage(SPacketCreateStructure.Handler.class, SPacketCreateStructure.class, 2, Side.SERVER);
    }

    @Mod.EventHandler
    public static void onServerStart(FMLServerStartingEvent e)
    {
        e.registerServerCommand(new CommandStructure());
    }

    public static Logger getLog()
    {
        return log;
    }

    public static BlockPos parseCoords(String s)
    {
        try
        {
            String[] from = s.split(" ");
            if (from.length == 3)
            {
                return new BlockPos(Integer.parseInt(from[0]), Integer.parseInt(from[1]), Integer.parseInt(from[2]));
            }
        }
        catch(NumberFormatException ex)
        {
            return null;
        }
        return null;
    }

    public static void fillBlockWithLoot(WorldServer world, Random rand, BlockPos pos, EnumFacing facing, ResourceLocation loot_table, float luck)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing) instanceof IItemHandlerModifiable)
        {
            IItemHandlerModifiable hand = (IItemHandlerModifiable) te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
            LootTable table = world.getLootTableManager().getLootTableFromLocation(loot_table);
            List<ItemStack> items = table.generateLootForPools(rand, new LootContext(luck, world, world.getLootTableManager(), null, null, null));
            for (int i = 0; i < hand.getSlots(); i++)
            {
                if (items.size() <= 0)
                    break;
                int index = rand.nextInt(items.size());
                hand.setStackInSlot(i, items.get(index));
                items.remove(index);
            }
        }
    }

    public static int getHorizontalRotation(EnumFacing face)
    {
        switch (face)
        {
            case NORTH: return 0;
            case WEST: return 90;
            case SOUTH: return 180;
            case EAST: return 270;
            default: return 0;
        }
    }

    public static int getHorizontalRotation(Rotation rot)
    {
        switch (rot)
        {
            case NONE: return 180;
            case CLOCKWISE_90: return 90;
            case CLOCKWISE_180: return 0;
            case COUNTERCLOCKWISE_90: return 270;
            default: return 0;
        }
    }

    public static BlockPos.MutableBlockPos transformPos(BlockPos.MutableBlockPos pos, @Nullable Mirror mir, @Nullable Rotation rot)
    {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        boolean flag = true;
        if(mir != null)
        {
            switch (mir)
            {
                case LEFT_RIGHT:
                    pos.setPos(x, y, -z);
                    break;
                case FRONT_BACK:
                    pos.setPos(-x, y, z);
                    break;
                default:
                    flag = false;
                    break;
            }
        }
        if(flag)
        {
            x = pos.getX();
            y = pos.getY();
            z = pos.getZ();
        }
        if(rot != null)
        {
            switch (rot)
            {
                case CLOCKWISE_90:
                    pos.setPos(-z, y, x);
                    break;
                case CLOCKWISE_180:
                    pos.setPos(-x, y, -z);
                    break;
                case COUNTERCLOCKWISE_90:
                    pos.setPos(z, y, -x);
                    break;
            }
        }
        return pos;
    }

    public static BlockPos transformPos(BlockPos pos, @Nullable Mirror mir, @Nullable Rotation rot)
    {
        return transformPos(new BlockPos.MutableBlockPos(pos), mir, rot);
    }

    public static Vector3d transformVec(Vector3d vec, @Nullable Mirror mir, @Nullable Rotation rot)
    {
        double x = vec.getX(), y = vec.getY(), z = vec.getZ();
        boolean flag = true;
        if(mir != null)
        {
            switch (mir)
            {
                case LEFT_RIGHT:
                    vec.setZ(-z);
                    break;
                case FRONT_BACK:
                    vec.setX(-x);
                    break;
                default:
                    flag = false;
                    break;
            }
        }
        if(flag)
        {
            x = vec.getX();
            y = vec.getY();
            z = vec.getZ();
        }
        if(rot != null)
        {
            switch (rot)
            {
                case CLOCKWISE_90:
                    vec.set(-z, y, x);
                    break;
                case CLOCKWISE_180:
                    vec.set(-x, y, -z);
                    break;
                case COUNTERCLOCKWISE_90:
                    vec.set(z, y, -x);
                    break;
            }
        }
        return vec;
    }

    public static EnumFacing.Axis rotateAxis(EnumFacing.Axis ax, Rotation rot)
    {
        switch (rot)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (ax)
                {
                    case X:
                        return EnumFacing.Axis.Z;
                    case Z:
                        return EnumFacing.Axis.X;
                }
                break;
        }
        return ax;
    }

    public static Rotation getIdentityRotation(Rotation rot)
    {
        switch (rot)
        {
            case CLOCKWISE_90:
                return Rotation.COUNTERCLOCKWISE_90;
            case COUNTERCLOCKWISE_90:
                return Rotation.CLOCKWISE_90;
            case CLOCKWISE_180:
                return Rotation.CLOCKWISE_180;
            default:
                return rot;
        }
    }

    public static Rotation getRotation(EnumFacing facing)
    {
        switch (facing)
        {
            case NORTH:
                return Rotation.NONE;
            case EAST:
                return Rotation.CLOCKWISE_90;
            case SOUTH:
                return Rotation.CLOCKWISE_180;
            case WEST:
                return Rotation.COUNTERCLOCKWISE_90;
        }
        return Rotation.NONE;
    }
}
