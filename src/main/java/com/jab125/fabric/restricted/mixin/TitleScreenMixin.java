package com.jab125.fabric.restricted.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;

import java.io.IOException;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

	private static final Logger LOGGER = LogManager.getLogger();
	LevelInfo LEVEL_INFO = new LevelInfo("World", GameMode.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), DataPackSettings.SAFE_MODE);
	private Screen realmsNotificationGui;
	private String splashText;
	private int copyrightTextWidth;
	private int copyrightTextX;
	private static final Identifier ACCESSIBILITY_ICON_TEXTURE = new Identifier("textures/gui/accessibility.png");
	

	protected TitleScreenMixin(Text title) {
		super(title);
		//TODO Auto-generated constructor stub
	}

	@Overwrite
	public void init() {
		if (this.splashText == null) {
			this.splashText = this.client.getSplashTextLoader().get();
		}

		this.copyrightTextWidth = this.textRenderer.getWidth("Copyright Mojang AB. Do not distribute!");
		this.copyrightTextX = this.width - this.copyrightTextWidth - 2;
		//int i = true;
		int j = this.height / 4 + 48;
		if (this.client.isDemo()) {
		//	this.initWidgetsDemo(j, 24);
		} else {
			this.initWidgetsNormal(j, 24);
		}

		this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_TEXTURE, 256, 256, (button) -> {
			this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
		}, new TranslatableText("narrator.button.language")));
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, j + 72 + 12, 98, 20, new TranslatableText("menu.options"), (button) -> {
			this.client.setScreen(new OptionsScreen(this, this.client.options));
		}));
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, j + 72 + 12, 98, 20, new TranslatableText("menu.quit"), (button) -> {
			this.client.scheduleStop();
		}));
		this.addDrawableChild(new TexturedButtonWidget(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_ICON_TEXTURE, 32, 64, (button) -> {
			this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options));
		}, new TranslatableText("narrator.button.accessibility")));
		this.client.setConnectedToRealms(false);
		if (this.client.options.realmsNotifications && this.realmsNotificationGui == null) {
			this.realmsNotificationGui = new RealmsNotificationsScreen();
		}

		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationGui.init(this.client, this.width, this.height);
		}

	}

	private boolean areRealmsNotificationsEnabled() {
		return this.client.options.realmsNotifications && this.realmsNotificationGui != null;
	}



	private void initWidgetsNormal(int y, int spacingY) {
		ButtonWidget.TooltipSupplier tooltipSupplier = false ? ButtonWidget.EMPTY : new ButtonWidget.TooltipSupplier() {
			private final Text MULTIPLAYER_DISABLED_TEXT = new TranslatableText("title.multiplayer.disabled.restricted");

			public void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int i, int j) {
				if (!buttonWidget.active) {
					renderOrderedTooltip(matrixStack, client.textRenderer.wrapLines(this.MULTIPLAYER_DISABLED_TEXT, Math.max(width / 2 - 43, 170)), i, j);
				}

			}

			public void supply(Consumer<Text> consumer) {
				consumer.accept(this.MULTIPLAYER_DISABLED_TEXT);
			}
		};
		boolean bl = this.canReadWorldData();
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y, 200, 20, new TranslatableText("menu.playworld"), (button) -> {
			if (bl) {
				this.client.startIntegratedServer("World");
			} else {
				DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
				this.client.createWorld("World", LEVEL_INFO, impl, GeneratorOptions.createDemo(impl));
			}

		}));
		((ButtonWidget)this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacingY * 1, 200, 20, new TranslatableText("menu.multiplayer"), (button) -> {
			Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
			this.client.setScreen((Screen)screen);
		}, tooltipSupplier))).active = false;
		((ButtonWidget)this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacingY * 2, 200, 20, new TranslatableText("menu.online"), (button) -> {
		//	this.switchToRealms();
		}, tooltipSupplier))).active = false;
	}
	private boolean canReadWorldData() {
		try {
			LevelStorage.Session session = this.client.getLevelStorage().createSession("World");

			boolean var2;
			try {
				var2 = session.getLevelSummary() != null;
			} catch (Throwable var5) {
				if (session != null) {
					try {
						session.close();
					} catch (Throwable var4) {
						var5.addSuppressed(var4);
					}
				}

				throw var5;
			}

			if (session != null) {
				session.close();
			}

			return var2;
		} catch (IOException var6) {
			SystemToast.addWorldAccessFailureToast(this.client, "World");
			LOGGER.warn((String)"Failed to read world data", (Throwable)var6);
			return false;
		}
	}
}
