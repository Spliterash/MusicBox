package ru.spliterash.musicbox;


import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.spliterash.musicbox.utils.ComponentUtils;
import ru.spliterash.musicbox.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings({"ArraysAsListWithZeroOrOneArgument", "SpellCheckingInspection", "unused", "RedundantSuppression"})
public enum Lang {
    NO_PEX(
            "&6Sry no perms",
            "&6Похоже у тебя нет разрешения на это действие"),
    // В консоли русский вариант будет смотреться... да никак не будет, UTF-8 👍👍👍
    ONLY_PLAYERS("Sry, but only players can execute this command"),
    SONG_NAME("&6{song}"),
    SONG_LORE(
            Arrays.asList(
                    "&7Duration: &b{length}",
                    "&7Author: &b{author}",
                    "&7Original author: &b{original_author}"
            ),
            Arrays.asList(
                    "&7Продолжительность: &b{length}",
                    "&7Автор: &b{author}",
                    "&7Оригинальный автор: &b{original_author}"
            )
    ),
    NEW_INSTRUMENT(
            "&cThis song contains instrument, available in 1.14",
            "&cЭта музыка содержит инструменты, доступные только в 1.14"),
    GUI_TITLE("&l&3MusicBox &8{container} &0{page}&7/&0{last_page}"),
    FOLDER_FORMAT("&e{folder}"),
    CURRENT_PLAYNING(
            "&eNow playing &b{song}",
            "&eСейчас играет &b{song}"),

    ADD_CONTAINER_TO_PLAYLIST(
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&bRight click&7 to add in your playlist"
            ),
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&bПравый клик&7 чтобы добавить в своей плейлист музыку отсюда"
            )
    ),
    CURRENT_IN_PLAYLIST(
            "&aIn playlist",
            "&aВ плейлисте"
    ),
    SONG_PANEL_NOW_PLAY(
            Arrays.asList(
                    "",
                    "&aNow playning"
            ),
            Arrays.asList(
                    "",
                    "&aСейчас играет"
            )
    ),
    SONG_PANEL_SWITH_TO(
            Arrays.asList(
                    "",
                    "&7Click to play"
            ),
            Arrays.asList(
                    "",
                    "&7Нажми чтобы проиграть"
            )
    ),
    ADD_MUSIC_TO_PLAYLIST(
            Arrays.asList("&bLeft click&7 to add this song to playlist"),
            Arrays.asList("&bЛевый клик&7 чтобы добавить эту мелодию в плейлист")
    ),
    COMMAND_HELP_SHOP(
            "&b/musicbox shop (name)&6- Open disc shop or buy disc with name",
            "&b/musicbox shop (name)&6- Открыть магазин дисков или купить пластинку с именем"),
    COMMAND_HELP_GET(
            "&b/musicbox get (name)&6- Open get disc GUI or get disc with name",
            "&b/musicbox get (name)&6- Открыть инвентарь получения дисков или получить диск с именем"
    ),
    COMMAND_HELP(
            Arrays.asList(
                    "&b/musicbox &6- Open music gui",
                    "&b/musicbox play [song_name] &6- Play song with name"
            ),
            Arrays.asList(
                    "&b/musicbox &6- Открыть инвентарь с музыкой",
                    "&b/musicbox play [имя] &6- Воспроизвести трек"
            )
    ),
    BUY_MUSIC_LORE(
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&eClick to buy this disc",
                    "&7Price: &6{price} $"
            ),
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&eНажми чтобы купить эту пластинку",
                    "&7Цена: &6{price} $"
            )
    ),
    CANT_SWITCH(
            "You cant switch play mode",
            "&6Вы не можете поменять режим проигрывания"),
    NEXT(
            "&6Next",
            "&6Вперёд"),
    BACK(
            "&6Back",
            "&6Назад"),
    BUY_CONTAINER_LORE(
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&ePress &cright&e to buy this box",
                    "&7Price: &6{price} $"
            ),
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&eНажми &cправой кнопкой&e чтобы купить весь этот сундук",
                    "&7Цена: &6{price} $"
            )),
    NO_HAS_MONEY(
            "&6You don't have enough &b{amount}&6 to buy this",
            "&6Вам не хватает &b{amount}&6 чтобы купить это"),
    NO_INVENTORY_SPACE(
            "&6You dont have space in your inventory",
            "&6У вас нет места в инвентаре"),
    DISC_BUYED(
            "&6You have successfully purchased a disc &b{disc}",
            "&6Вы успешно купили диск &b{disc}"),
    PARENT_CONTAINER(
            "&6Return to parent folder",
            "&6Вернуться на уровень выше"),
    HUMAN_TIME_MINUTE(
            "{value}m.",
            "{value}м."
    ),
    HUMAN_TIME_SECOND(
            "{value}s.",
            "{value}с."),
    SONG_STOP(
            "&cStop",
            "&cСтоп"),
    REWIND_BUTTON(
            "&6Rewind",
            "&6Перемотка"),
    NOT_PLAY(
            "&6Music currently does not play",
            "&6В данный момент вы не слушаете музыку"),
    BLOCK_NOT_PLAY(
            "&6This block currently don't play music",
            "&6Этот блок в данный момент не проигрывает музыку"
    ),
    CONTROL_GUI_TITLE(
            "Song panel - now play &0&n{song}",
            "Музыкальная панель - играет &0&n{song}"),
    REWIND_TO(
            "&6Rewind to &b{time}&e({percent}%)",
            "&6Перемотать на &b{time}&e({percent}%)"),
    REWINDED(
            "&6You are rewind song to &b{time}&e({percent}%)",
            "&6Вы перемотали проигрыватель на &b{time}&e({percent}%)"),
    CLOSE(
            "&cClose",
            "&cЗакрыть"),
    ENABLE(
            "&aEnable",
            "&aВключено"),
    DISABLE(
            "&cDisable",
            "&cВыключено"),

    SWITH_MODE_LORE(
            Arrays.asList(
                    "&7Status: {status}",
                    "&7Speaker mode allow nearby players hear you music"
            ),
            Arrays.asList(
                    "&7Статус: {status}",
                    "&7Режим колонки позволит игрокам рядом с вами слышать вашу музыку"
            )
    ),

    SWITH_MODE_NO_PEX_LORE(
            Arrays.asList(
                    "&7You need &bmusicbox.speaker to change mode"
            ),
            Arrays.asList(
                    "&7Вам необходимо иметь &bmusicbox.speaker чтобы включить колонку"
            )
    ),
    SPEAKER_MODE(
            "&6Speaker mode",
            "&6Режим колонки"),
    PLAYLIST_EDITOR(
            "&6Playlist list",
            "&6Список плейлистов"),
    PLAYLIST_NAME("&6{name}"),
    GO_BACK_TO_PLAYLIST(
            "&6Go back to playlist",
            "&6Вернуться к плейлисту"
    ),
    PLAYLIST_LORE(
            Arrays.asList(
                    "&7Track count: &b{count}",
                    "&7Duration: &b{duration}"
            ),
            Arrays.asList(
                    "&7Количество треков: &b{count}",
                    "&7Продолжительность: &b{duration}"
            )
    ),
    PLAYLIST_ITEM_LORE(
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&7Press &bthe left mouse button&7 to listen",
                    "&7To delete, press &bthe right mouse button"
            ),
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&7Чтобы прослушать нажми &bлевую кнопку",
                    "&7Чтобы удалить нажми &bправую кнопку"
            )
    ),
    PLAYLIST_LIST_TITLE(
            "&l&3Playlist list &0{page}&7/&0{last_page}",
            "&l&3Список плейлистов &0{page}&7/&0{last_page}"
    ),
    PLAYLIST_EDITOR_LIST_TITLE(
            "&l&3Edit {playlist}&f &0{page}&7/&0{last_page}",
            "&l&3Редактирование {playlist}&f &0{page}&7/&0{last_page}"
    ),
    MASTER_PLAYLIST(
            "&6Master playlist",
            "&6Главный плейлист"),
    MASTER_PLAYLIST_LORE(
            Arrays.asList(
                    "&7Include &cALL&7 tracks",
                    "&aAlways&7 random"
            ),
            Arrays.asList(
                    "&7Включает в себя &aВСЕ&7 треки",
                    "&cВсегда&7 рандомный"
            )
    ),
    CREATE_NEW_PLAYLIST(
            "&6Create new playlist",
            "&6Создать новый плейлист"),
    NEW_PLAYLIST_MESSAGE(
            "&6To create a new playlist write &b/musicbox playlist Playlist name ",
            "&6Чтобы создать новый плейлист напиши &b/musicbox playlist Имя плейлиста"),
    SAVE_PLAYLIST_CHANGE(
            "&6Save changes",
            "&6Сохранить изменения"),
    PLAYLIST_SAVED(
            "&6Playlist &b{playlist}&6 saved",
            "&6Плейлист &b{playlist}&6 успешно сохранён"),
    DELETE_PLAYLIST(
            "&6Delete playlist",
            "&6Удалить плейлист"),
    PLAYLIST_DELETED(
            "&6You delete playlist &b{playlist}",
            "&6Вы удалили плейлист &b{playlist}"
    ),
    SHUFFLE_PLAYLIST(
            "&6Shuffle playlist",
            "&6Перемешать плейлист"),
    RENAME_PLAYLIST(
            "&6Rename playlist",
            "&6Переименовать плейлист"),
    DONT_FORGET_TO_SAVE(
            Arrays.asList(
                    "&cDO NOT FORGET&7 to save"
            ),
            Arrays.asList(
                    "&cНЕ ЗАБУДЬ&7 сохранить"
            )
    ),
    ADD_MUSIC_TO_PLAYLIST_ITEM(
            "&6Add music to this playlist",
            "&6Добавить музыку в этот плейлист"),
    PLAYLIST_ZERO_SIZE(
            "&cYou can't save empty playlist",
            "&cВы не можете сохранить пустой плейлист"),
    CHILL_CHILL_MAN(
            "&cChill man, chill, save in progress",
            "&cОстынь чел, сохранение в процессе"),
    NEXT_PLAYLIST_SONG_TITLE(
            "&6Play next playlist song",
            "&6Следующая мелодия"),
    PLAYLIST_SONG_NUM("&9{num}) "),
    CURRENT_PLAYLIST_SONG("{num}&a{song}"),
    ANOTHER_PLAYLIST_SONG("{num}&8{song}"),
    DEFAULT_PLAYLIST_LORE(
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&bLeft click&7 to play",
                    "&bRight click&7 to edit"
            ),
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&bЛевый клик для прослушивания",
                    "&bПравый клик для редактирования"
            )
    ),
    SONG_NOT_FOUND(
            "&cSong not found",
            "&Мелодия не найдена"),
    YOU_GET_DISC(
            "&6You get disc &b{disc}",
            "&6Вы получили диск &b{disc}"),
    GET_DISC_LORE(
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&7Click to get this disc"
            ),
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&7Нажми чтобы получить этот диск"
            )
    ),
    GET_ALL_CONTAINER_LORE(
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&7Click right to get all container"
            ),
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&7Нажми правой кнопкой чтобы получить всё из этого сундука"
            )),
    INPUT_NAME(
            "&6Input name &b/musicbox playlist name",
            "&6Введи имя &b/musicbox playlist имя"),
    PLAYER_OFLLINE(
            "&6Player &b{player}&6 offline",
            "&6Игрок &b{player}&6 не в сети"),
    SHUT_UPPED(
            "&6Player &b{player}&6 has ben muted",
            "&6Игрок &b{player}&6 больше не воспроизводит музыку"),
    ADMIN_HELP(
            Arrays.asList(
                    "&b/musicbox admin shutup [player] &6- off player SongPlayer"
            ),
            Arrays.asList(
                    "&b/musicbox admin shutup [player] &6- выключить проигрыватель игрока"
            )
    ),
    SIGN_PLAYLIST_LORE(
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&eClick to setup sign playlist"
            ),
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&eНажми чтобы установить плейлист этой таблички"
            )),
    SIGN_SONG_LORE(
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&eClick to setup sign song"

            ),
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&eНажми чтобы выбрать музыку для таблички"

            )
    ),
    RANDOM_MODE_BUTTON(
            "&6Random mode {status}",
            "&6Рандомный режим {status}"),

    SIGN_CONTAINER_LORE(
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&eRight click&7 to setup this container on sign"

            ),
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&eПравый клик&7 чтобы установить этот контейнер на табличку"

            )
    ), WRONG_SIGN_FACE(
            "&cWrong side of the plate, but nothing, now we'll fix it",
            "&cНеправильная сторона таблички, но ничего, сейчас мы это исправим"),
    SEARCH_INFO_SIGN_HOVER(
            Arrays.asList(
                    "&7If enabled, it will display the current playlist",
                    "&7on the sign above or below",
                    "&7Looks for it from below and from above at a length of &e5&7 blocks from the current sign"

            ),
            Arrays.asList(
                    "&7Если включено, то будет выводить текущий список проигрывания",
                    "&7на табличку сверху или снизу",
                    "&7Ищет её снизу и сверху на длине &e5&7 блоков от текущей таблички"
            )
    ),
    ENDLESS_SIGN_MODE(
            "&6Endless sign mode {status}",
            "&6Бесконечное проигрывание {status}"
    ),
    SEARCH_INFO_SIGN_TITLE(
            "&6Search info sign {status}",
            "&6Искать информационную табличку {status}"),
    INFO_SIGN_OFF(
            Arrays.asList(
                    "It info sign",
                    "for songplayer",
                    "Currently SongPlayer",
                    "&cOFF"
            ),
            Arrays.asList(
                    "Это табличка нужна",
                    "для проигрывателя",
                    "Сейчас он",
                    "&cВЫКЛЮЧЕН"
            )
    ),
    CONTROL_PANEL_BUTTON(
            "&6Songplayer panel",
            "&6Панель проигрывателя"),
    PREVENT_DESTROY_TITLE(
            "&6Prevent sign destroy {status}",
            "&6Предотвратить автоуничтожение проигрывателя {status}"
    ),
    PREVENT_DESTROY_LORE(
            Arrays.asList(
                    "&7If enabled, the plugin will not destroy the sign",
                    "&7if no one hears it for more than &b60&7 seconds (configurable in the config)",
                    "&7also protects the songplayer to restart the server",
                    "",
                    "&7This button is visible only to those who have &cmusicbox.admin"
            ),
            Arrays.asList(
                    "&7Если включено, плагин не будет уничтожать проигрыватель таблички",
                    "&7в случае если её никто не слышит более &b60&7 секунд(настраивается в конфиге)",
                    "&7а так же это защищает табличку от рестарта сервера",
                    "",
                    "&7Эту кнопку видят только те, у кого есть &cmusicbox.admin"
            )
    ),
    CLICK_TO_PLAY_CONTAINER(
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&eRight click&7 to play this chest"
            ),
            Arrays.asList(
                    "",
                    "&7==========================",
                    "&eПравый клик&7 чтобы послушать всё что тут лежит"
            )
    ),
    LEGACY_DISC_NOT_FOUND(
            "&6Sorry, i can find sound with name &b{song}",
            "&6Извини, но я не могу найти звук для &b{song}"),
    LEGACY_DISC_REPLACE(
            "&6It looks like this is a disc from an old version of the plugin, " +
                    "but don't worry, we'll convert it to the new one. Click again, but now with a new disc",
            "&6Похоже это диск от старой версии плагина, но не волнуйся, " +
                    "мы переделаем его в новый. Кликни ещё раз, но теперь новым диском, он должен быть где то у тебя в инвентаре"
    ),
    SILENT_MODE_LORE(
            Arrays.asList(
                    "&7In silent mode you can hear &bonly self songs&7.",
                    "&7No any other songplayers from player speakers, jukeboxs or signs",
                    "&7Status: {status}"
            ),
            Arrays.asList(
                    "&7В тихом режиме вы можете слышать &bтолько свою музыку&7.",
                    "&7Никаких других проигрывателей от игроков с колокной, дисков или табличек",
                    "&7Статус: {status}"
            )
    ),
    SILENT_MODE(
            "&6Silent mode",
            "&6Тихий режим"
    );
    /**
     * Оригинальные переводы
     * 0 индекс - англиский
     * 1 индекс - русский
     */
    private final Object[] original = new Object[2];
    private Object selected;

    /**
     * Конструктор для простых строк
     *
     * @param en На англиском
     * @param ru На русском
     */
    Lang(String en, String ru) {
        original[0] = en;
        original[1] = ru;
    }

    /**
     * Конструктор для многострочных переводов
     *
     * @param en На англиском
     * @param ru На русском
     */
    Lang(List<String> en, List<String> ru) {
        original[0] = en;
        original[1] = ru;
    }

    Lang(List<String> en) {
        this(en, en);
    }

    Lang(String en) {
        this(en, en);
    }

    public static void reload(File folder, String lang) {
        File langFile = new File(folder, lang + ".yml");
        int index;
        if (lang.equals("ru"))
            index = 1;
        else
            index = 0;
        fill(langFile, index);
    }

    private static void fill(File langFile, int index) {
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(langFile);
        boolean saveNeed = false;
        for (Lang value : values()) {
            Object obj = conf.get(value.name());
            if (obj == null) {
                obj = value.original[index];
                conf.set(value.name(), obj);
                saveNeed = true;
            }
            if (obj instanceof String) {
                value.selected = StringUtils.t(obj.toString());
            } else {
                //noinspection unchecked
                List<String> list = (List<String>) obj;
                value.selected = StringUtils.t(list);
            }
        }
        if (saveNeed) {
            try {
                conf.save(langFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        if (isString()) {
            return selected.toString();
        } else {
            //noinspection unchecked
            List<String> list = (List<String>) selected;
            return String.join("\n", list);
        }
    }

    private boolean isString() {
        return selected instanceof String;
    }

    public List<String> toList(String... replace) {
        if (isString()) {
            ArrayList<String> list = new ArrayList<>(1);
            String text = StringUtils.replace(selected.toString(), replace);
            list.add(text);
            return list;
        } else if (replace.length > 0) {
            //noinspection unchecked
            return ((List<String>) selected)
                    .stream()
                    .map(s -> StringUtils.replace(s, replace))
                    .collect(Collectors.toList());
        } else
            //noinspection unchecked
            return new ArrayList<>(((List<String>) selected));
    }

    public BaseComponent[] toComponent(String... replace) {
        if (isString())
            return TextComponent.fromLegacyText(toString(replace));
        else {
            //noinspection unchecked
            return ComponentUtils.join(((List<String>) selected)
                    .stream()
                    .map(s -> StringUtils.replace(s, replace))
                    .collect(Collectors.toList()), "\n");
        }
    }

    public String toString(String... replace) {
        return StringUtils.replace(toString(), replace);
    }

    public String[] toArray() {
        return toList().toArray(new String[0]);
    }

    public String toPlainText(String... replace) {
        return ChatColor.stripColor(toString(replace));
    }
}
