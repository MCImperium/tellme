package fi.dy.masa.tellme;

import net.minecraftforge.client.ClientCommandHandler;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fi.dy.masa.tellme.command.CommandTellme;
import fi.dy.masa.tellme.reference.Reference;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION)
public class TellMe
{
    @Instance(Reference.MOD_ID)
    public static TellMe instance;

    //@SidedProxy(clientSide = Reference.PROXY_CLASS_CLIENT, serverSide = Reference.PROXY_CLASS_SERVER)
    //public static IProxy proxy;
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        logger = event.getModLog();
        //MinecraftForge.TERRAIN_GEN_BUS.register(new BiomeEvents());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ClientCommandHandler.instance.registerCommand(new CommandTellme());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //logger.info("WorldType.worldTypes.length: " + WorldType.worldTypes.length);
        //BiomeInfo.printBiomeList();
    }
}
