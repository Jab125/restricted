package com.jab125.forge.restricted.mixin;

import java.io.IOException;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen{
   private static final LevelSettings WORLD_SETTINGS = new LevelSettings("World", GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), DataPackConfig.DEFAULT);

    @Nullable
    private String splash;
    private int copyrightWidth;
   private int copyrightX;
   private Screen realmsNotificationsScreen;
   private static final ResourceLocation ACCESSIBILITY_TEXTURE = new ResourceLocation("textures/gui/accessibility.png");
   private net.minecraftforge.client.gui.NotificationModUpdateScreen modUpdateNotification;

    protected TitleScreenMixin(Component p_96550_) {
        super(p_96550_);
        //TODO Auto-generated constructor stub
    }

    @Overwrite
    public void createNormalMenuOptions(int p_96764_, int p_96765_) {
      boolean flag = this.checkWorldPresence();
      this.addRenderableWidget(new Button(this.width / 2 - 100, p_96764_, 200, 20, new TranslatableComponent("menu.playworld"), (p_169444_) -> {
         if (flag) {
            this.minecraft.loadLevel("World");
         } else {
            RegistryAccess.RegistryHolder registryaccess$registryholder = RegistryAccess.builtin();
            this.minecraft.createLevel("World", WORLD_SETTINGS, registryaccess$registryholder, WorldGenSettings.demoSettings(registryaccess$registryholder));
         }

      }));
      //boolean flag = this.minecraft.allowsMultiplayer();
      Button.OnTooltip button$ontooltip = flag ? Button.NO_TOOLTIP : new Button.OnTooltip() {
         private final Component text = new TranslatableComponent("title.multiplayer.disabled.restricted");

         public void onTooltip(Button p_169458_, PoseStack p_169459_, int p_169460_, int p_169461_) {
            if (!p_169458_.active) {
               renderTooltip(p_169459_, minecraft.font.split(this.text, Math.max(width / 2 - 43, 170)), p_169460_, p_169461_);
            }

         }

         public void narrateTooltip(Consumer<Component> p_169456_) {
            p_169456_.accept(this.text);
         }
      };
      (this.addRenderableWidget(new Button(this.width / 2 - 100, p_96764_ + p_96765_ * 1, 200, 20, new TranslatableComponent("menu.multiplayer"), (p_169450_) -> {
         Screen screen = (Screen)(this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this));
         this.minecraft.setScreen(screen);
      }, button$ontooltip))).active = false;
      (this.addRenderableWidget(new Button(this.width / 2 + 2, p_96764_ + p_96765_ * 2, 98, 20, new TranslatableComponent("menu.online"), (p_96771_) -> {
        // this.realmsButtonClicked();
      }, button$ontooltip))).active = false;
   }

   private boolean checkWorldPresence() {
      try {
         LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft.getLevelSource().createAccess("World");

         boolean flag;
         try {
            flag = levelstoragesource$levelstorageaccess.getSummary() != null;
         } catch (Throwable throwable1) {
            if (levelstoragesource$levelstorageaccess != null) {
               try {
                  levelstoragesource$levelstorageaccess.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (levelstoragesource$levelstorageaccess != null) {
            levelstoragesource$levelstorageaccess.close();
         }

         return flag;
      } catch (IOException ioexception) {
         SystemToast.onWorldAccessFailure(this.minecraft, "World");
         //LOGGER.warn("Failed to read demo world data", (Throwable)ioexception);
         return false;
      }
   }
    
}
