package com.neep.neepmeat.client.screen.plc;

import com.neep.neepmeat.client.screen.ScreenSubElement;
import com.neep.neepmeat.client.screen.plc.edit.EditBoxWidget;
import com.neep.neepmeat.client.screen.plc.edit.InstructionBrowserWidget;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.network.plc.PLCSyncProgram;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

public class PLCScreenEditorState extends ScreenSubElement implements Drawable, Element, Selectable, PLCScreenState
{
    private final PLCProgramScreen parent;
    private EditBoxWidget textField;
    private final InstructionBrowserWidget browser;
    private boolean changed;

    private final Parser parser = new Parser();

    public PLCScreenEditorState(PLCProgramScreen parent)
    {
        this.parent = parent;
        browser = new InstructionBrowserWidget(this.parent, () -> null, p -> true, this::selectProvider);
    }

    private void selectProvider(InstructionProvider provider)
    {
        // TODO: add text to thingy
    }

    @Override
    protected void init()
    {
        super.init();
        if (textField == null)
        {
            textField = new EditBoxWidget(client.textRenderer, x, y, 300, height, Text.of("Write your program here.\n\nClick a block in the world to insert its coordinates as a target."), Text.of("gle"))
            {
                @Override
                protected void setFocused(boolean focused)
                {
                    super.setFocused(focused);
                    updateEditorWidth();
                }
            };
            textField.setText(parent.getScreenHandler().getInitialText());
            textField.setChangeListener(s -> this.changed = true);

        }

        updateEditorWidth();

        browser.init(screenWidth, screenHeight);

        addDrawableChild(textField);
        addDrawableChild(browser);
    }

    private void updateEditorWidth()
    {
        if (textField.isFocused())
            textField.setWidth(300);
        else
            textField.setWidth(100);

        textField.setHeight(height);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (client.world.getTime() % 10 == 0 && changed)
        {
            try
            {
                ParsedSource parsedSource = parser.parse(textField.getText());
                setCompileMessage("Parsed Successfully", true, -1);
            }
            catch (NeepASM.ProgramBuildException e)
            {
                setCompileMessage(e.getMessage(), false, e.line());
            }

            PLCSyncProgram.Client.sendText(parent.getScreenHandler().getPlc(), textField.getText());
            changed = false;
        }

        textField.setDebugLine(parent.getScreenHandler().debugLine());
    }

    public void setCompileMessage(String message, boolean success, int line)
    {
        textField.setError(message, success ? 0x44AA00 : 0xFF0000);
        textField.setErrorLine(line);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        try
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        try
        {
            return super.charTyped(chr, modifiers);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder)
    {

    }

    @Override
    public SelectionType getType()
    {
        return SelectionType.FOCUSED;
    }

    public boolean isSelected()
    {
        return textField.isFocused();
    }

    public void argument(Argument argument)
    {
        textField.insert("@(" + argument.pos().getX() + " " + argument.pos().getY() + " " + argument.pos().getZ() + " " + argument.face().name().toUpperCase().charAt(0) + ")");
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) { keyPressed(keyCode, scanCode, modifiers); }
}
