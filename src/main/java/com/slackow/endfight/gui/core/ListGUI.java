package com.slackow.endfight.gui.core;

import com.slackow.endfight.util.Renameable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Language;

import java.util.*;
import java.util.function.*;

public class ListGUI<T extends Renameable> extends Screen {
    final String title;
    private final List<T> data;
    private final Supplier<T> getNewItem;
    private final Screen from;
    private final BiConsumer<ViewGUI<T>, T> editObj;
    private final BiConsumer<List<T>, Integer> save;
    int selected;
    private int page = 0;
    private int y;
    private int lastTick = 0;
    private int tick = 0;

    public ListGUI(Screen from, List<T> data, int selected, Supplier<T> getNew, BiConsumer<ViewGUI<T>, T> editObj, BiConsumer<List<T>, Integer> save, String title) {
        this.from = from;
        this.editObj = editObj;
        this.data = new ArrayList<>(data);
        this.selected = selected;
        this.getNewItem = getNew;
        this.save = save;
        this.title = title;
    }

    @SuppressWarnings("unchecked")
    public void init() {
        this.buttons.clear();
        boolean hasPages = data.size() > 5;
        List<T> displayed = hasPages ? data.subList(page * 5, Math.min(page * 5 + 5, data.size())) : data;
        for (int i = 0; i < displayed.size(); i++) {
            String buttonMsg = displayed.get(i).getName();
            if (isSelectable() && i + page * 5 == selected) {
                buttonMsg = "> " + buttonMsg + " <";
            }
            int textWidth = Math.max(100, textRenderer.getStringWidth(buttonMsg) + 20);
            buttons.add(new ButtonWidget(i, width / 2 - textWidth / 2, height / 6 + 80 + i * 24 - displayed.size() * 12, textWidth, 20, buttonMsg));
        }
        y = height / 6 + 30 - 24 - displayed.size() * 12;
        int homeRow = height / 6 + 80 + displayed.size() * 12;
        buttons.add(new ButtonWidget(5, width / 2 - 10, homeRow, 20, 20, "+"));
        ButtonWidget left = new ButtonWidget(6, width / 2 - 32, homeRow, 20, 20, "<");
        left.active = page > 0;
        buttons.add(left);
        ButtonWidget right = new ButtonWidget(7, width / 2 + 12, homeRow, 20, 20, ">");
        right.active = (data.size() - 1) / 5 > page;
        buttons.add(right);
        buttons.add(new ButtonWidget(8, width / 2 - 100, height / 6 + 150, 200, 20, Language.getInstance().translate("gui.done")));
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        renderBackground();
        drawCenteredString(textRenderer, title, width / 2, y, 0xFFFFFF);
        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (lastTick == tick) {
            return;
        }
        lastTick = tick;
        if (button.id >= 0 && button.id < 5) {
            int index = button.id + page * 5;
            if (!isSelectable() || index == selected) {
                field_1229.openScreen(new ViewGUI<>(this, data.get(index)));
            } else {
                selected = index;
                reinit();
            }
        } else if (button.id == 6) {
            page--;
            reinit();
        } else if (button.id == 7) {
            page++;
            reinit();
        } else if (button.id == 5) {
            T obj = getNewItem.get();
            if (obj != null) {
                obj.setName("");
                data.add(obj);
                if (isSelectable()) {
                    selected = data.size() - 1;
                }
                field_1229.openScreen(new ViewGUI<>(this, obj));
            }
        } else if (button.id == 8) {
            save();
            field_1229.openScreen(from);
        }
        super.buttonClicked(button);
    }

    private void reinit() {
        ListGUI<T> screen = new ListGUI<>(from, data, selected, getNewItem, editObj, save, title);
        screen.page = page;
        field_1229.openScreen(screen);
    }

    @Override
    public void tick() {
        tick++;
        super.tick();
    }

    public void save() {
        save.accept(data, selected);
    }

    public void remove(T obj) {
        data.remove(obj);
        page = Math.min(page, (data.size() - 1) / 5);
        if (data.isEmpty()) {
            selected = -1;
        } else {
            selected = Math.min(selected, data.size() - 1);
        }
        save();
    }


    public BiConsumer<ViewGUI<T>, T> getEditObj() {
        return editObj;
    }

    public boolean isSelectable() {
        return true;
    }
}
