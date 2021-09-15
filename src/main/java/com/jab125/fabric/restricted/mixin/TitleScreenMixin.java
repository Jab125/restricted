package com.jab125.fabric.restricted.mixin;

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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
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
		//	this.initWidgetsNormal(j, 24);
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
}
