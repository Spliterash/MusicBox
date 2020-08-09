package ru.spliterash.musicbox.utils;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

@UtilityClass
public class ComponentUtils {
    public BaseComponent[] join(List<String> list, String character) {
        ComponentBuilder hoverBuilder = new ComponentBuilder("");
        for (int i = 0; i < list.size(); i++) {
            String current = list.get(i);
            hoverBuilder.append(TextComponent.fromLegacyText(current), ComponentBuilder.FormatRetention.NONE);
            if (i < list.size() - 1) {
                hoverBuilder.append(character);
            }
        }
        return hoverBuilder.create();
    }


    private boolean needToDelete(BaseComponent component) {
        if (component instanceof TextComponent) {
            TextComponent text = (TextComponent) component;
            if (text.getText() != null && !text.getText().isEmpty())
                return false;
        }
        return component.getExtra() == null || component.getExtra().size() == 0;
    }

    public List<BaseComponent> removeEmpty(List<BaseComponent> list) {
        for (int i = 0; i < list.size(); i++) {
            BaseComponent baseComponent = list.get(i);
            if (needToDelete(baseComponent)) {
                list.remove(i);
                i--;
            } else if (baseComponent.getExtra() != null && baseComponent.getExtra().size() > 0) {
                baseComponent.setExtra(removeEmpty(baseComponent.getExtra()));
            }
        }
        return list;
    }
}
