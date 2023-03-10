package me.zeroX150.atomic.feature.gui.clickgui;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.helper.Client;
import me.zeroX150.atomic.helper.Renderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class Clickable {
    final Module parent;
    final double margin = 4;
    final double width = 100;
    double animProg = 0;
    double animProg1 = 0;

    public Clickable(Module parent) {
        this.parent = parent;
    }

    public void render(double x, double y, MatrixStack stack, double animProgress, double actualX, double actualY, float delta) {
        Color fillColor = ClickGUI.currentActiveTheme.inactive();
        Color fontColor = ClickGUI.currentActiveTheme.fontColor();
        if (!ClickGUI.INSTANCE.searchTerm.isEmpty()) {
            boolean isGood = true;
            for (char c : ClickGUI.INSTANCE.searchTerm.toLowerCase().toCharArray()) {
                if (!parent.getName().toLowerCase().contains(c + "")) {
                    isGood = false;
                    break;
                }
            }
            if (!isGood) {
                //fillColor = fillColor.darker().darker();
                fontColor = Renderer.modify(fontColor, -1, -1, -1, 60);
            }
        }
        boolean isHovered = (actualX != -1 && actualY != -1 && isHovered(actualX, actualY));
        if (isHovered) {
            ClickGUI.INSTANCE.renderDescription(parent.getDescription());
            animProg += 0.03 * (delta + 0.5);
        } else animProg -= 0.03 * (delta + 0.5);
        if (parent.isEnabled()) {
            animProg1 += 0.03 * (delta + 0.5);
        } else animProg1 -= 0.03 * (delta + 0.5);
        animProg1 = MathHelper.clamp(animProg1, 0, 1);
        animProg = MathHelper.clamp(animProg, 0, 1);
        double animProg1Inter = easeOutBounce(animProg1);
        double animProgInter = easeOutBounce(animProg);
        double floor = Math.floor(y + (margin + 9) * animProgress);
        DrawableHelper.fill(stack, (int) (x - margin), (int) Math.floor(y - margin), (int) (x + width + margin), (int) floor, fillColor.getRGB());
        DrawableHelper.fill(stack, (int) (x - margin), (int) Math.floor(y - margin), (int) (x - margin + (width + margin * 2) * animProgInter), (int) floor, ClickGUI.currentActiveTheme.active().getRGB());
        //DrawableHelper.fill(stack, (int) (x - margin), (int) Math.floor(y - margin), (int) (x - margin + 1.5), (int) Math.floor(y - margin + ((margin * 2 + 9) * animProg1Inter) * animProgress), ClickGUI.currentActiveTheme.l_highlight().getRGB());
        Renderer.fill(stack, ClickGUI.currentActiveTheme.l_highlight(), x - margin, y - margin, x - margin + 1.5, y - margin + ((margin * 2 + 9) * animProg1Inter) * animProgress);
        if (ClickGUI.currentActiveTheme.centerText())
            Atomic.fontRenderer.drawCenteredString(stack, parent.getName(), (float) (x + (width / 2f)), (float) y, fontColor.getRGB());
        else
            Atomic.fontRenderer.drawString(stack, parent.getName(), (float) (x), (float) y, fontColor.getRGB());
    }

    double easeOutBounce(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }

    public void clicked(boolean isLeft) {
        if (isLeft) parent.toggle();
        else if (parent.config.getAll().size() != 0) ClickGUI.INSTANCE.showModuleConfig(parent);
    }

    boolean isHovered(double x, double y) {
        double mx = Client.getMouseX();
        double my = Client.getMouseY();
        return mx < x + width + margin && mx > x - margin && my < y + 9 + margin && my > y - margin;
    }
}
