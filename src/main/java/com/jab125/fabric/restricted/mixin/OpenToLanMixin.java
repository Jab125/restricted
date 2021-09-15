package com.jab125.fabric.restricted.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;



@Mixin(OpenToLanScreen.class)
public class OpenToLanMixin extends Screen {
	private static final Text ALLOW_COMMANDS_TEXT = new TranslatableText("selectWorld.allowCommands");
	private static final Text SURVIVAL_MODE_TEXT = new TranslatableText("gameMode.survival");
	private static final Text GAME_MODE_TEXT = new TranslatableText("selectWorld.gameMode");
//	private static final Text GAMEMODE_DISPLAY = GAME_MODE_TEXT.getString() + ": " + SURVIVAL_MODE_TEXT.asString();
	private static final Text OTHER_PLAYERS_TEXT = new TranslatableText("lanServer.otherPlayers");
	private  Screen parent;
	private GameMode gameMode;
	private boolean allowCommands;
    protected OpenToLanMixin(Text title) {
		super(title);
		//TODO Auto-generated constructor stub
	}

	public void init() {
		ButtonWidget buttonWidget = (ButtonWidget)this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, GAME_MODE_TEXT, (button) -> {}));
		buttonWidget.active = false;
			this.gameMode = GameMode.SURVIVAL;
		
		ButtonWidget buttonWidget2 = this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_TEXT, (button) -> {}));
		buttonWidget2.active = false;
		this.allowCommands = false;
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("lanServer.start"), (button) -> {
			this.client.setScreen((Screen)null);
			int i = NetworkUtils.findLocalPort();
			TranslatableText text2;
			if (this.client.getServer().openToLan(this.gameMode, this.allowCommands, i)) {
				text2 = new TranslatableText("commands.publish.started", new Object[]{i});
			} else {
				text2 = new TranslatableText("commands.publish.failed");
			}

			this.client.inGameHud.getChatHud().addMessage(text2);
			this.client.updateWindowTitle();
		}));
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, (button) -> {
			this.client.setScreen(this.parent);
		}));
	}
}
