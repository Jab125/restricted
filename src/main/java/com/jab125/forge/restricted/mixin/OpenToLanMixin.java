package com.jab125.forge.restricted.mixin;


import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ShareToLanScreen.class)
public class OpenToLanMixin extends Screen{
    private static final Component ALLOW_COMMANDS_LABEL = new TranslatableComponent("selectWorld.allowCommands");
    private static final Component GAME_MODE_LABEL = new TranslatableComponent("selectWorld.gameMode");
    private static final Component INFO_TEXT = new TranslatableComponent("lanServer.otherPlayers");
    private final Screen lastScreen;
    private GameType gameMode = GameType.SURVIVAL;
    private boolean commands;
    protected OpenToLanMixin(Component p_96550_, Screen lastScreen, boolean commands) {
        super(p_96550_);
        this.lastScreen = lastScreen;
        this.commands = commands;
    }

    @Overwrite
    public void init() {
        Button buttonWidget = (Button)this.addRenderableWidget(new Button(this.width / 2 - 155, 100, 150, 20, GAME_MODE_LABEL, (button) -> {}));
        buttonWidget.active = false;

        Button buttonWidget2 = this.addRenderableWidget(new Button(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_LABEL, (button) -> {}));
        buttonWidget2.active = false;
        this.commands = false;
        this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableComponent("lanServer.start"), (p_96660_) -> {
            this.minecraft.setScreen((Screen)null);
            int i = HttpUtil.getAvailablePort();
            Component component;
            if (this.minecraft.getSingleplayerServer().publishServer(this.gameMode, this.commands, i)) {
                component = new TranslatableComponent("commands.publish.started", i);
            } else {
                component = new TranslatableComponent("commands.publish.failed");
            }

            this.minecraft.gui.getChat().addMessage(component);
            this.minecraft.updateTitle();
        }));
        this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, (p_96657_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }));
    }
}
