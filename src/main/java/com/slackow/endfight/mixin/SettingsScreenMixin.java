package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.gui.config.ConfigGUI;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsScreen.class)
public class SettingsScreenMixin extends Screen {
    @SuppressWarnings("unchecked")
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        buttons.add(new ButtonWidget(6_22_2019, width / 2 - 152, height / 6 + 144 - 6, 150, 20, "End Fight Settings..."));
    }

    @Inject(method = "buttonClicked", at = @At("TAIL"))
    private void buttonClicked(ButtonWidget par1, CallbackInfo ci) {
        if (par1.id == 6_22_2019) {
            field_1229.openScreen(new ConfigGUI(this, BigConfig.getSelectedConfig(), false));
        }
    }
}
