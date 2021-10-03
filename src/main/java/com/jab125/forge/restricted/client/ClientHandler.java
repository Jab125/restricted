package com.jab125.forge.restricted.client;

//import com.mrcrayfish.catalogue.client.screen.CatalogueModListScreen;
import com.jab125.forge.restricted.screens.RestrictedPauseScreen;
import com.jab125.forge.restricted.screens.RestrictedTitleScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fmlclient.gui.screen.ModListScreen;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = "restricted", value = Dist.CLIENT)
public class ClientHandler
{
    @SubscribeEvent
    public static void onOpenScreen(GuiOpenEvent event)
    {
        if(event.getGui() instanceof TitleScreen)
        {
            event.setGui(new RestrictedTitleScreen());
        } else if (event.getGui() instanceof PauseScreen) {
            event.setGui(new RestrictedPauseScreen(((PauseScreen) event.getGui()).showPauseMenu));
        }
    }
}
