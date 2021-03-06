package ru.pearx.libmc.client.gui.controls.common;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.lib.Color;
import ru.pearx.lib.Colors;
import ru.pearx.libmc.client.gui.DrawingTools;
import ru.pearx.libmc.client.gui.TexturePart;
import ru.pearx.libmc.client.gui.controls.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrAppleXZ on 02.05.17 8:53.
 */
@SideOnly(Side.CLIENT)
public class Button extends Control
{
    protected ResourceLocation textures;
    /* lu, u, ru,
       l, c, r,
       lb, b, rb */
    protected List<TexturePart> parts;

    private String text;
    public Runnable clickAction;
    private Color textColor = Colors.WHITE;

    public Button(ResourceLocation textures, String str, Runnable run)
    {
        this.textures = textures;
        this.parts = new ArrayList<>();
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 3; x++)
            {
                parts.add(new TexturePart(textures, x * 8, y * 8, 8, 8, 24, 24));
            }
        }
        setText(str);
        this.clickAction = run;
    }

    @Override
    public void mouseUp(int button, int x, int y)
    {
        clickAction.run();
    }

    @Override
    public void render()
    {
        int w = getWidth();
        int h = getHeight();
        if(isFocused())
            GlStateManager.color(.8f, .8f, .8f, 1);
        //upper and bottom
        for(int i = 1; i < (w - 8) / 8f; i++)
        {
            int x = i * 8;
            parts.get(1).draw(x, 0);
            parts.get(7).draw(x, h - 8);
        }
        //right and left
        for(int i = 1; i < (h - 8) / 8f; i++)
        {
            int y = i * 8;
            parts.get(3).draw(0, y);
            parts.get(5).draw(w - 8, y);
        }
        for (int xm = 1; xm < (w - 8) / 8f; xm++)
        {
            for(int ym = 1; ym < (h - 8) / 8f; ym++)
            {
                parts.get(4).draw(xm * 8, ym * 8);
            }
        }
        //corners
        parts.get(0).draw(0, 0);
        parts.get(2).draw(w - 8, 0);
        parts.get(6).draw(0, h - 8);
        parts.get(8).draw(w - 8, h - 8);

        GlStateManager.color(1, 1, 1, 1);
        String text = getText();
        DrawingTools.drawString(text, (w - DrawingTools.measureString(text)) / 2, (h - DrawingTools.getStringHeight(text)) / 2, getTextColor());
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Color getTextColor()
    {
        return textColor;
    }

    public void setTextColor(Color textColor)
    {
        this.textColor = textColor;
    }
}
