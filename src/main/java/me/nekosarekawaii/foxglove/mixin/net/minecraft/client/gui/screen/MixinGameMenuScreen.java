package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.gui.screen;

import me.nekosarekawaii.foxglove.util.ServerUtils;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen {

	@Shadow
	@Final
	private static Text SEND_FEEDBACK_TEXT;

	@Shadow
	@Final
	private static Text REPORT_BUGS_TEXT;

	@Shadow
	protected abstract void disconnect();

	protected MixinGameMenuScreen(final Text ignored) {
		super(ignored);
	}

	@Inject(method = "createUrlButton", at = @At(value = "HEAD"), cancellable = true)
	private void injectCreateUrlButton(final Text text, final String url, final CallbackInfoReturnable<ButtonWidget> cir) {
		if (this.client == null) return;
		if (text == SEND_FEEDBACK_TEXT) {
			cir.setReturnValue(ButtonWidget.builder(
					Text.translatable("selectServer.direct"),
					b -> {
						final ServerInfo serverInfo = new ServerInfo("", "", false);
                        this.client.setScreen(
                                new DirectConnectScreen(this, connect -> {
                                    if (connect) {
                                        this.disconnect();
                                        ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), this.client, ServerAddress.parse(serverInfo.address), serverInfo, false);
                                    } else this.client.setScreen(this);
                                }, serverInfo)
                        );
                    }
            ).width(98).build());
        } else if (text == REPORT_BUGS_TEXT && !this.client.isInSingleplayer()) {
            final ButtonWidget button = ButtonWidget.builder(
                    Text.literal("Reconnect"),
                    b -> {
                        if (ServerUtils.lastServerExists()) {
                            this.disconnect();
                            ServerUtils.connectToLastServer();
                        }
                    }
            ).width(98).build();
            cir.setReturnValue(button);
		}
	}

}
