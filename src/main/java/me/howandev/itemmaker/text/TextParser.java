package me.howandev.itemmaker.text;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Map.entry;
import static net.md_5.bungee.api.ChatColor.*;

/**
 * Parser for converting text inputs to components.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextParser {

    /**
     * All default tag identifiers.
     */
    private static final @NotNull String
            COLOR_TAG = "color",
            DECORATION_TAG = "decoration",
            CLICK_TAG = "click",
            HOVER_TAG = "hover",
            KEY_TAG = "key",
            LANG_TAG = "lang",
            INSERTION_TAG = "insertion",
            RAINBOW_TAG = "rainbow",
            GRADIENT_TAG = "gradient",
            TRANSITION_TAG = "transition",
            FONT_TAG = "font",
            NEWLINE_TAG = "newline",
            SELECTOR_TAG = "selector";

    /**
     * Patterns used by the default tags.
     */
    private static final @NotNull Pattern
            HEX_COLOR_PATTEN = Pattern.compile("#[A-Fa-f0-9]{6}"),
            ITEM_PATTERN = Pattern.compile("([A-Za-z0-9_]+)(\\{.+})?\\s?(-?\\d*)"),
            ENTITY_PATTERN = Pattern.compile("([A-Za-z0-9_]+),([A-Za-z0-9-]+),?(.+)?");

    /**
     * Text parser with default settings.
     */
    static final TextParser DEFAULT = create();

    /**
     * Characters used to match tags.
     */
    @Setter
    @Getter
    @Accessors(fluent = true)
    private char
            TAG_START = '<',
            TAG_END = '>',
            TAG_REMOVE = '/',
            TAG_SPLIT = ':',
            TAG_ESCAPE = '\\';

    /**
     * Tags registered in the parser.
     */
    private final @NotNull Map<String, Tag> tags = new LinkedHashMap<>();

    /**
     * Constants registered in the parser.
     */
    private final @NotNull LinkedHashSet<Constant> constants = new LinkedHashSet<>();

    /**
     * Parses the text using default text parser and returns
     * the result as an array of base components.
     * @param input input text
     * @return parsed base components
     * @see TextParser#create()
     */
    public static BaseComponent @NotNull [] components(final @NotNull String input) {
        return DEFAULT.parse(input).create();
    }

    /**
     * Parses the text using default text parser and returns as json
     * @param input input text
     * @return parsed text as json
     * @see TextParser#create()
     */
    public static @NotNull String json(final @NotNull String input) {
        return ComponentSerializer.toString(components(input));
    }

    /**
     * Parses the text using default text parser and returns
     * the result as legacy text.
     * @param input input text
     * @return parsed legacy text
     * @see TextParser#create()
     */
    public static @NotNull String legacy(final @NotNull String input) {
        val builder = new StringBuilder();
        Arrays.stream(components(input)).forEach(component -> builder.append(component.toLegacyText()));
        return builder.toString();
    }

    /**
     * Parses the text using default text parser and returns
     * the result as plain text.
     * @param input input text
     * @return parsed plain text
     * @see TextParser#create()
     */
    public static @NotNull String plain(final @NotNull String input) {
        val builder = new StringBuilder();
        Arrays.stream(components(input)).forEach(component -> builder.append(component.toPlainText()));
        return builder.toString();
    }

    /**
     * Creates new text parser with default tags included.
     * @return new text parser
     */
    @Contract(value = "-> new", pure = true)
    public static @NotNull TextParser create() {
        val parser = empty();

        // Colors
        val colorAliases = Map.ofEntries(
                entry("grey", "gray"),
                entry("dark_gray", "dark_grey"));
        parser.registerTag(new Tag.SimpleTag(COLOR_TAG, false, false, false, true,
                (parsing, args) -> {
                    if(args.length == 0) return ((builder, integer) -> builder.color(WHITE));
                    val matcher = HEX_COLOR_PATTEN.matcher(args[0]);
                    if(matcher.matches())
                        return (builder, integer) -> builder.color(ChatColor.of(matcher.group().toUpperCase()));
                    final ChatColor color = switch (args[0]) {
                        case "black" -> BLACK;
                        case "dark_blue" -> DARK_BLUE;
                        case "dark_green" -> DARK_GREEN;
                        case "dark_aqua" -> DARK_AQUA;
                        case "dark_red" -> DARK_RED;
                        case "dark_purple" -> DARK_PURPLE;
                        case "gold" -> GOLD;
                        case "gray", "grey" -> GRAY;
                        case "dark_gray", "dark_grey" -> DARK_GRAY;
                        case "blue" -> BLUE;
                        case "green" -> GREEN;
                        case "aqua" -> AQUA;
                        case "red" -> RED;
                        case "light_purple" -> LIGHT_PURPLE;
                        case "yellow" -> YELLOW;
                        default -> WHITE;
                    };
                    return (builder, integer) -> builder.color(color);
                },
                (tag, args) -> {
                    if(args.length == 0) return true;
                    if(tag.args.length == 0) return false;
                    return Objects.equals(
                            colorAliases.getOrDefault(tag.args[0], tag.args[0]),
                            colorAliases.getOrDefault(args[0], args[0]));
                },
                "colour", "c")
        );
        for(val color : new String[]{"black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple",
                "gold", "gray", "grey", "dark_gray", "dark_grey", "blue", "green", "aqua", "red", "light_purple", "yellow", "white"})
            parser.registerConstant(Constant.simple(color, COLOR_TAG + ":" + color));
        parser.registerConstant(new Constant() {
            @Override
            public boolean match(@NotNull String input) {
                return HEX_COLOR_PATTEN.matcher(input).matches();
            }

            @Override
            public @NotNull String output(@NotNull String input) {
                if (!match(input)) throw new IllegalStateException();
                return COLOR_TAG + ":" + input;
            }
        });

        // Decorations
        val decorationAliases = Map.ofEntries(
                entry("b", "bold"),
                entry("em", "italic"),
                entry("i", "italic"),
                entry("u", "underlined"),
                entry("st", "strikethrough"),
                entry("obf", "obfuscated"),
                entry("magic", "obfuscated"));
        parser.registerTag(new Tag.SimpleTag(DECORATION_TAG, false, false, false, true,
                (parsing, args) -> {
                    if(args.length == 0) return ((builder, integer) -> { });
                    return switch (args[0]) {
                        case "bold", "b" -> (builder, integer) -> builder.bold(true);
                        case "italic", "em", "i" -> (builder, integer) -> builder.italic(true);
                        case "underlined", "u" -> (builder, integer) -> builder.underlined(true);
                        case "strikethrough", "st" -> (builder, integer) -> builder.strikethrough(true);
                        case "obfuscated", "obf", "magic" -> (builder, integer) -> builder.obfuscated(true);
                        default -> (builder, integer) -> { };
                    };
                },
                (tag, args) -> {
                    if(args.length == 0) return true;
                    if(tag.args.length == 0) return false;
                    return Objects.equals(
                            decorationAliases.getOrDefault(tag.args[0], tag.args[0]),
                            decorationAliases.getOrDefault(args[0], args[0]));
                },
                "deco", "d")
        );
        for(val decoration : new String[]{"bold", "b", "italic", "em", "i", "underlined", "u",
                "strikethrough", "st", "obfuscated", "obf", "magic"})
            parser.registerConstant(Constant.simple(decoration, DECORATION_TAG + ":" + decoration));

        // Click
        parser.registerTag(new Tag.SimpleTag(CLICK_TAG, false, false, false, true,
                (parsing, args) -> {
                    if(args.length < 2) return ((builder, integer) -> { });
                    val action = switch (args[0]) {
                        case "open_url", "url" -> ClickEvent.Action.OPEN_URL;
                        case "open_file", "file" -> ClickEvent.Action.OPEN_FILE;
                        case "run_command", "command", "cmd", "run" -> ClickEvent.Action.RUN_COMMAND;
                        case "suggest_command", "suggest" -> ClickEvent.Action.SUGGEST_COMMAND;
                        case "change_page", "page" -> ClickEvent.Action.CHANGE_PAGE;
                        case "copy_to_clipboard", "copy", "clipboard" -> ClickEvent.Action.COPY_TO_CLIPBOARD;
                        default -> null;
                    };
                    if(action == null) return ((builder, integer) -> { });
                    return (builder, integer) -> builder.event(new ClickEvent(action, args[1]));
                },
                (tag, args) -> true,
                "action")
        );

        // Hover
        parser.registerTag(new Tag.SimpleTag(HOVER_TAG, false, false, false, true,
                (parsing, args) -> {
                    if(args.length < 2) return ((builder, integer) -> { });
                    val action = switch (args[0]) {
                        case "show_text", "text" -> HoverEvent.Action.SHOW_TEXT;
                        case "show_item", "item" -> HoverEvent.Action.SHOW_ITEM;
                        case "show_entity", "entity" -> HoverEvent.Action.SHOW_ENTITY;
                        default -> null;
                    };
                    if(action == null) return ((builder, integer) -> { });
                    val contents = new ArrayList<Content>();
                    for (int i = 0; i < args.length; i++) {
                        if(i == 0) continue;
                        switch (action) {
                            case SHOW_TEXT -> contents.add(new Text(parsing.parse(args[i]).create()));
                            case SHOW_ITEM -> {
                                val matcher = ITEM_PATTERN.matcher(args[i]);
                                if(!matcher.matches()) continue;
                                String material, nbt = null;
                                int count = 1;
                                material = matcher.group(1);
                                if(matcher.groupCount() == 2) {
                                    try {
                                        count = Integer.parseInt(matcher.group(2));
                                    } catch (NumberFormatException exception) {
                                        nbt = matcher.group(2);
                                    }
                                } else if(matcher.groupCount() == 3) {
                                    nbt = matcher.group(2);
                                    try {
                                        count = Integer.parseInt(matcher.group(3));
                                    } catch (NumberFormatException ignored) { }
                                }
                                contents.add(new Item(material, count, nbt != null ? ItemTag.ofNbt(nbt) : null));
                            }
                            case SHOW_ENTITY -> {
                                val matcher = ENTITY_PATTERN.matcher(args[i]);
                                if(!matcher.matches()) continue;
                                if(matcher.groupCount() < 2) continue;
                                contents.add(new Entity(matcher.group(1), matcher.group(2),
                                        matcher.groupCount() == 3 ? parsing.parse(matcher.group(3)).create()[0] : null)
                                );
                            }
                        }
                    }
                    if(contents.size() == 0) return ((builder, integer) -> { });
                    return (builder, integer) -> builder.event(new HoverEvent(action, contents));
                },
                (tag, args) -> true,
                "show")
        );

        // Key
        parser.registerTag(new Tag.SimpleTag(KEY_TAG, true, false, false, false,
                (parsing, args) -> {
                    if(args.length == 0) return ((builder, integer) -> { });
                    return (builder, integer) -> builder.append(new KeybindComponent(args[0]));
                },
                null,
                "keybind")
        );

        // Lang
        parser.registerTag(new Tag.SimpleTag(LANG_TAG, true, false, false, false,
                (parsing, args) -> {
                    if(args.length == 0) return ((builder, integer) -> { });
                    val with = new ArrayList<BaseComponent>();
                    for(int i = 1; i < args.length; i++)
                        with.addAll(List.of(parsing.parse(args[i]).create()));
                    return (builder, integer) -> builder.append(new TranslatableComponent(args[0], (Object[]) with.toArray(new BaseComponent[0])));
                },
                null,
                "translatable", "translate")
        );

        // Insertion
        parser.registerTag(new Tag.SimpleTag(INSERTION_TAG, false, false, false, true,
                (parsing, args) -> {
                    if(args.length == 0) return ((builder, integer) -> { });
                    return (builder, integer) -> builder.insertion(args[0]);
                },
                (tag, args) -> true)
        );

        // Rainbow
        parser.registerTag(new Tag.SimpleTag(RAINBOW_TAG, false, true, true, true,
                (parsing, args) -> {
                    final boolean reversed;
                    int phase = 0, saturation = 100, brightness = 100;
                    if(args.length != 0) {
                        reversed = args[0].startsWith("!");
                        try {
                            phase = Integer.parseInt(reversed ? args[0].substring(1) : args[0]);
                            phase = Math.floorMod(phase, 100);
                        } catch (NumberFormatException ignored) { }
                        if(args.length > 1) {
                            try {
                                saturation = Integer.parseInt(args[1]);
                                if(saturation > 100) saturation = 100;
                                else if(saturation < 0) saturation = 0;
                            } catch (NumberFormatException ignored) { }
                        }
                        if(args.length > 2) {
                            try {
                                brightness = Integer.parseInt(args[2]);
                                if(brightness > 100) brightness = 100;
                                else if(brightness < 0) brightness = 0;
                            } catch (NumberFormatException ignored) { }
                        }
                    } else reversed = false;
                    val data = new int[]{phase, saturation, brightness};
                    return (builder, integer) -> {
                        final float step = ((float) Math.floorMod((int) ((((float) integer) / 99 + ((float) data[0]) / 100) * 1000), 1000)) / 1000;
                        builder.color(ChatColor.of(Color.getHSBColor(
                                !reversed ? step : 1 - step,
                                ((float) data[1]) / 100,
                                ((float) data[2]) / 100)));
                    };
                },
                (tag, args) -> true)
        );

        // Gradient
        parser.registerTag(new Tag.SimpleTag(GRADIENT_TAG, false, true, true, true,
                (parsing, args) -> {
                    if(args.length == 0) return ((builder, integer) -> { });
                    val colors = new LinkedList<ChatColor>();
                    int phase = 0;
                    for(int i = 0; i < args.length; i++) {
                        if(i == args.length - 1) {
                            try {
                                phase = Integer.parseInt(args[i]);
                                //phase = Math.floorMod(phase, 100);
                                break;
                            } catch (NumberFormatException ignored) { }
                        }
                        val matcher = HEX_COLOR_PATTEN.matcher(args[i]);
                        if(matcher.matches()) {
                            colors.add(ChatColor.of(matcher.group().toUpperCase()));
                            continue;
                        }
                        colors.add(switch (args[i]) {
                            case "black" -> BLACK;
                            case "dark_blue" -> DARK_BLUE;
                            case "dark_green" -> DARK_GREEN;
                            case "dark_aqua" -> DARK_AQUA;
                            case "dark_red" -> DARK_RED;
                            case "dark_purple" -> DARK_PURPLE;
                            case "gold" -> GOLD;
                            case "gray", "grey" -> GRAY;
                            case "dark_gray", "dark_grey" -> DARK_GRAY;
                            case "blue" -> BLUE;
                            case "green" -> GREEN;
                            case "aqua" -> AQUA;
                            case "red" -> RED;
                            case "light_purple" -> LIGHT_PURPLE;
                            case "yellow" -> YELLOW;
                            default -> WHITE;
                        });
                    }
                    if(colors.size() == 1)
                        colors.add(ChatColor.of(colors.get(0).getColor())); // clone of the single color

                    final int maxRange = (int) (100d / (colors.size() - 1) * colors.size());
                    final int range = phase >= 0 ? phase % maxRange : maxRange - (phase % maxRange);
                    return (builder, integer) -> {
                        final double segmentSize = 100d / (colors.size() - 1);
                        final int fullGradientSize = (int) (segmentSize * colors.size());
                        integer += range;
                        integer %= fullGradientSize;
                        final int index = (int) Math.floor((double) integer / segmentSize);
                        val color1 = colors.get(index).getColor();
                        val color2 = colors.get(index + 1 < colors.size() ? index + 1 : 0).getColor();
                        final double position = (integer % segmentSize) / segmentSize;
                        final double ratio1 = 1 - position;
                        final double ratio2 = 1 - ratio1;
                        builder.color(ChatColor.of(new Color(
                                (int) (color1.getRed() * ratio1 + color2.getRed() * ratio2),
                                (int) (color1.getGreen() * ratio1 + color2.getGreen() * ratio2),
                                (int) (color1.getBlue() * ratio1 + color2.getBlue() * ratio2)
                        )));
                    };
                },
                (tag, args) -> true)
        );

        // Transition
        parser.registerTag(new Tag.SimpleTag(TRANSITION_TAG, false, false, false, true,
                (parsing, args) -> {
                    if(args.length == 0) return ((builder, integer) -> { });
                    val colors = new LinkedList<ChatColor>();
                    int phase = 0;
                    for(int i = 0; i < args.length; i++) {
                        if(i == args.length - 1) {
                            try {
                                phase = Integer.parseInt(args[i]);
                                break;
                            } catch (NumberFormatException ignored) { }
                        }
                        val matcher = HEX_COLOR_PATTEN.matcher(args[i]);
                        if(matcher.matches()) {
                            colors.add(ChatColor.of(matcher.group().toUpperCase()));
                            continue;
                        }
                        colors.add(switch (args[i]) {
                            case "black" -> BLACK;
                            case "dark_blue" -> DARK_BLUE;
                            case "dark_green" -> DARK_GREEN;
                            case "dark_aqua" -> DARK_AQUA;
                            case "dark_red" -> DARK_RED;
                            case "dark_purple" -> DARK_PURPLE;
                            case "gold" -> GOLD;
                            case "gray", "grey" -> GRAY;
                            case "dark_gray", "dark_grey" -> DARK_GRAY;
                            case "blue" -> BLUE;
                            case "green" -> GREEN;
                            case "aqua" -> AQUA;
                            case "red" -> RED;
                            case "light_purple" -> LIGHT_PURPLE;
                            case "yellow" -> YELLOW;
                            default -> WHITE;
                        });
                    }
                    if(colors.size() == 1)
                        colors.add(ChatColor.of(colors.get(0).getColor())); // clone of the single color
                    final int maxRange = (int) (100d / (colors.size() - 1) * colors.size());
                    final int range = phase >= 0 ? phase % maxRange : maxRange - (phase % maxRange);
                    return (builder, integer) -> {
                        final double segmentSize = (double) maxRange / colors.size();
                        final int index = (int) Math.floor((double) range / segmentSize);
                        val color1 = colors.get(index).getColor();
                        val color2 = colors.get(index + 1 < colors.size() ? index + 1 : 0).getColor();
                        final double position = (range % segmentSize) / segmentSize;
                        final double ratio1 = 1 - position;
                        final double ratio2 = 1 - ratio1;
                        builder.color(ChatColor.of(new Color(
                                (int) (color1.getRed() * ratio1 + color2.getRed() * ratio2),
                                (int) (color1.getGreen() * ratio1 + color2.getGreen() * ratio2),
                                (int) (color1.getBlue() * ratio1 + color2.getBlue() * ratio2)
                        )));
                    };
                },
                (tag, args) -> true)
        );

        // Font
        parser.registerTag(new Tag.SimpleTag(FONT_TAG, false, false, false, true,
                (parsing, args) -> {
                    if(args.length == 0) return ((builder, integer) -> { });
                    return (builder, integer) -> builder.font(args[0]);
                },
                (tag, args) -> true)
        );

        // New line
        parser.registerTag(new Tag.SimpleTag(NEWLINE_TAG, true, false, false, false,
                (parsing, args) -> (builder, integer) -> builder.append("\n"),
                null,
                "nl", "br")
        );

        // Selector
        parser.registerTag(new Tag.SimpleTag(SELECTOR_TAG, true, false, false, false,
                (parsing, args) -> {
                    if(args.length == 0) return ((builder, integer) -> { });
                    return (builder, integer) -> builder.append(new SelectorComponent(args[0]));
                },
                null)
        );

        return parser;
    }

    /**
     * Creates new empty text parser.
     * @return new text parser
     */
    @Contract(value = "-> new", pure = true)
    public static @NotNull TextParser empty() {
        return new TextParser();
    }

    /**
     * Registers new tag to this manager if the tag with same name or aliases don't exist.
     * @param tag tag to register
     * @return if the operation was successful
     * @throws IllegalStateException if the tag has illegal properties
     */
    public boolean registerTag(@NotNull Tag tag) {
        if(tags.containsKey(tag.name()) || registeredTags().contains(tag)) return false;
        if((tag.isTextEntry() && tag.requiresEnd()) ||
                (tag.isTextEntry() && tag.dependsOnPosition()) ||
                (tag.isTextEntry() && tag.removable()) ||
                (tag.requiresEnd() && !tag.removable()) ||
                (tag.dependsOnPosition() && !tag.requiresEnd())
        ) {
            throw new IllegalStateException("Registered tag has illegal properties");
        }
        val aliases = List.of(tag.aliases());
        if(tags.values().stream()
                .anyMatch(registered ->
                        Arrays.stream(registered.aliases()).anyMatch(alias -> aliases.contains(alias) || alias.equals(tag.name())))) return false;
        return tags.put(tag.name(), tag) != null;
    }

    /**
     * Removes tag with given identifier from this text parser.
     * @param identifier identifier of the tag to remove
     * @return if the operation was successful
     */
    public boolean unregisterTag(@NotNull String identifier) {
        return tags.remove(identifier) != null;
    }

    /**
     * Returns all registered tags in this text parser.
     * @return all tags in this parser
     */
    @Contract(value = "-> new", pure = true)
    public @NotNull @Unmodifiable Collection<Tag> registeredTags() {
        return tags.values();
    }

    /**
     * Registers new constants to this manager.
     * @param constant constants to register
     * @return if the operation was successful
     */
    public boolean registerConstant(@NotNull Constant constant) {
        if(constants.contains(constant)) return false;
        return constants.add(constant);
    }

    /**
     * Removes given constants from this text parser.
     * @param constant constant to remove
     * @return if the operation was successful
     */
    public boolean unregisterConstant(@NotNull Constant constant) {
        return constants.remove(constant);
    }

    /**
     * Returns all registered constants in this text parser.
     * @return all constants in this parser
     */
    @Contract(value = "-> new", pure = true)
    public @NotNull @Unmodifiable Collection<Constant> registeredConstants() {
        return Collections.unmodifiableCollection(constants);
    }

    /**
     * Parses text input as component using parser's tags.
     * @param input text input
     * @return parsed components in form of build
     */
    @Contract(value = "_ -> new", pure = true)
    public @NotNull ComponentBuilder parse(final @NotNull String input) {
        return parse(input, new ComponentBuilder());
    }

    /**
     * Parses text input as component using parser's tags.
     * @param input text input
     * @param builder builder to append the parsed input to
     * @return parsed components in form of build
     */
    @Contract(value = "_, _ -> param2")
    @Synchronized
    public @NotNull ComponentBuilder parse(final @NotNull String input, final @NotNull ComponentBuilder builder) {
        val characters = input.toCharArray();

        class Searcher {

            int cursor = 0;
            int textCursor = 0;
            final List<Character> special = List.of(
                    TAG_START,
                    TAG_END,
                    TAG_REMOVE,
                    TAG_SPLIT,
                    TAG_ESCAPE);

            @Nullable String nextText() {
                val entry = new StringBuilder();
                boolean exit = false;
                boolean skip = false;
                while(characters.length > cursor && !exit) {
                    if(special.contains(characters[cursor]) && !skip) {
                        if(characters[cursor] == TAG_ESCAPE)
                            skip = true;
                        else if(characters[cursor] == TAG_START) {
                            exit = true;
                            continue; // we don't move the cursor because start tag isn't part of the text
                        } else {  // other characters don't need to be escaped
                            textCursor++;
                            entry.append(characters[cursor]);
                        }
                        cursor++;
                        continue;
                    }
                    skip = false;
                    textCursor++;
                    entry.append(characters[cursor++]);
                }
                return entry.length() != 0 ? entry.toString() : null;
            }

            @Nullable ParsedTag nextTag() {
                if(!(characters.length > cursor)) return null;
                if(characters[cursor] != TAG_START) return null;
                cursor++; // skip the START char
                val entry = new StringBuilder();
                boolean exit = false;
                boolean skip = false;
                while(characters.length > cursor && !exit) {
                    if(special.contains(characters[cursor]) && !skip) {
                        if(characters[cursor] == TAG_ESCAPE)
                            skip = true;
                        else if(characters[cursor] == TAG_END) {
                            cursor++; // skips over the end tag since it's still part of it
                            exit = true;
                            continue;
                        }
                        entry.append(characters[cursor++]);
                        continue;
                    }
                    skip = false;
                    entry.append(characters[cursor++]);
                }
                if(entry.length() == 0) return null;
                var raw = entry.toString();

                // If the tag is canceling
                boolean cancel = false;
                if(raw.charAt(0) == TAG_REMOVE) {
                    cancel = true;
                    raw = raw.substring(1);
                }

                // Applying constants
                for(val constant : registeredConstants()) {
                    if(!constant.match(raw)) continue;
                    raw = constant.output(raw);
                    break;
                }

                // Parsing
                val tags = new ArrayList<String>();
                var builder = new StringBuilder();
                val characters = raw.toCharArray();
                skip = false;
                for(char character : characters) {
                    if (special.contains(character) && !skip) {
                        if (character == TAG_ESCAPE) {
                            skip = true;
                            continue;
                        } else if (character == TAG_SPLIT) {
                            tags.add(builder.toString());
                            builder = new StringBuilder();
                            continue;
                        }
                    }
                    skip = false;
                    builder.append(character);
                }
                if(builder.length() > 0) tags.add(builder.toString());

                if(tags.size() == 0) return null;
                val tag = fromText(tags.get(0));
                if(tag == null) return null;
                if(!tag.removable() && cancel) return null;
                val args = new String[tags.size() - 1];
                if(args.length > 0) System.arraycopy(tags.toArray(new String[0]), 1, args, 0, args.length); // copying args
                return new ParsedTag(tag, cancel, args, textCursor);
            }

        }

        // Searches for all tags and texts
        val searcher = new Searcher();
        val tags = new LinkedList<ParsedTag>();
        val texts = new LinkedList<String>();
        boolean searching = true;
        while(searching) {
            int last = searcher.cursor;
            val parsedTag = searcher.nextTag();
            if(parsedTag != null) tags.add(parsedTag);
            val parsedText = searcher.nextText();
            if(parsedText != null) texts.add(parsedText);
            if(last == searcher.cursor) searching = false;
        }

        // Parsing
        tags.stream().filter(tag -> tag.cancel).forEach(cancelTag -> {
            if(!cancelTag.tag.removable() || cancelTag.tag.remover() == null) return;
            ParsedTag last = null;
            for(val tag : tags) {
                if(cancelTag == tag) break;
                if(tag.cancel) continue; // is different cancel tag
                if(cancelTag.tag != tag.tag) continue;
                if(tag.end != -1) continue; // has been already parsed
                //noinspection ConstantConditions (remover is never null in this case)
                if(!cancelTag.tag.remover().remove(tag, cancelTag.args)) continue;
                last = tag;
            }
            if(last == null) return;
            last.setEnd(cancelTag.start);
        });

        tags.removeAll(tags.stream().filter(tag -> tag.cancel).collect(Collectors.toSet())); // cancel tags are no longer needed
        tags.removeAll(tags.stream().filter(tag -> tag.tag.requiresEnd() && tag.end == -1)
                .collect(Collectors.toSet())); // tags that don't have end but require one should be removed

        final int length = texts.stream().mapToInt(String::length).sum();
        int converted = 0;
        val textIterator = texts.iterator();

        final Predicate<List<ParsedTag>> positionDepend = (parsed -> parsed.stream().anyMatch(tag -> tag.tag.dependsOnPosition()));

        do {
            val next = textIterator.hasNext() ? textIterator.next() : "";
            val applied = new LinkedList<ParsedTag>();
            val sortedTags = new LinkedList<ParsedTag>(); // all tags before the entry
            val textTags = new LinkedList<ParsedTag>(); // all tags with text entries

            int start = converted;
            int end = converted + next.length();
            for (final Iterator<ParsedTag> iterator = tags.iterator(); iterator.hasNext(); ) {
                val tag = iterator.next();
                if(start < tag.start) break; // this and next tags are applied after this text entry

                sortedTags.add(tag);

                if(tag.tag.isTextEntry()) {
                    iterator.remove();
                    textTags.add(tag);
                    continue;
                }

                if(tag.end != -1 && tag.end < end) {
                    iterator.remove();
                    continue;
                }

                applied.add(tag);
            }

            // Text entry tags have to to be processed first
            textTags.forEach(textTag -> {
                builder.append(""); // Empty component to carry the style for the text entries
                builder.reset();
                for(val tag : sortedTags) {
                    if(textTag == tag) break; // everything after shouldn't be processed
                    if(tag.tag.isTextEntry()) continue; // only style tags should be processed
                    tag.tag.processor().process(this, tag.args).accept(builder, -1);
                }
                textTag.tag.processor().process(this, textTag.args).accept(builder, -1);
            });

            if(!positionDepend.test(applied)) { // None of the tags depend on position in text
                builder.append(next);
                builder.reset();
                applied.forEach(tag -> tag.tag.processor().process(this, tag.args).accept(builder, -1));
            } else { // One or more of the tags depend on the text position
                val segments = next.toCharArray();
                val consumers = applied.stream()
                        .map(tag -> tag.tag.processor().process(this, tag.args))
                        .collect(Collectors.toList()); // pre-processing for optimization
                for (int i = 0; i < segments.length; i++) {
                    val iterator = consumers.iterator();
                    builder.append(String.valueOf(segments[i]));
                    builder.reset(); // each segment can be different and has to be reset
                    for(val tag : applied) {
                        if(!iterator.hasNext()) continue;
                        final int delta = tag.start > tag.end ? 0 : tag.end - tag.start; // check to prevent unexpected step values
                        iterator.next().accept(builder, delta != 0 ? ((int) ((99d / delta) * ((start + i) - tag.start))) : 0);
                    }
                }
            }

            converted = end;
        } while(converted != length);
        return builder;
    }

    /**
     * Returns tag in this parser from its identifier.
     * @param tag identifier of the tag
     * @return tag with given identifier
     */
    private @Nullable Tag fromText(@NotNull String tag) {
        return tags.getOrDefault(
                tag.toLowerCase(),
                tags.values().stream()
                        .filter(aTag -> Arrays.asList(aTag.aliases()).contains(tag.toLowerCase()))
                        .findAny()
                        .orElse(null));
    }

    /**
     * Represents a text tag that can be parsed by the text parser.
     */
    public interface Tag {

        /**
         * Returns name of the tag, has to be unique and lowercase.
         * <p>
         * e.g "color" for tag {@literal <color:red>}.
         * @return name of the tag
         */
        @NotNull @NonNls String name();

        /**
         * Whether the tag adds a component to the builder.
         * <p>
         * Is incompatible with {@link Tag#requiresEnd()}, {@link Tag#dependsOnPosition()} and {@link Tag#removable()}.
         * @return if the tag is text entry
         */
        boolean isTextEntry();

        /**
         * If the tag requires cancelling tag to work.
         * <p>
         * If true, {@link Tag#removable()} has to be true.
         * <p>
         * Is incompatible with {@link Tag#isTextEntry()}.
         * @return if cancelling tag is required
         */
        boolean requiresEnd();

        /**
         * If the tag results in different outputs depending on the position
         * in the text, e.g. gradients.
         * <p>
         * If true, {@link Tag#requiresEnd()} has to be true.
         * <p>
         * Is incompatible with {@link Tag#isTextEntry()}.
         * @return if the tag depends on position
         */
        boolean dependsOnPosition();

        /**
         * If the tag can be cancelled by cancelling tag.
         * <p>
         * Is incompatible with {@link Tag#isTextEntry()}.
         * @return if the tag can be cancelled
         * @see Tag#requiresEnd()
         */
        boolean removable();

        /**
         * Returns processor used by this tag.
         * @return processor used by this tag
         */
        @NotNull Processor processor();

        /**
         * Returns remover used by this tag.
         * @return remover used by this tag
         */
        @Nullable Remover remover();

        /**
         * Aliases used by this tag, used aliases in text are unknown
         * to the processor and can't be used to change the processor's result.
         * @return lowercase aliases used by this tag
         */
        String @NotNull [] aliases();

        /**
         * Default implementation of tag interface.
         */
        final record SimpleTag(@NotNull String name,
                               boolean isTextEntry,
                               boolean requiresEnd,
                               boolean dependsOnPosition,
                               boolean removable,
                               @NotNull Processor processor,
                               @Nullable Remover remover,
                               String @NotNull ... aliases) implements Tag {
        }

    }

    /**
     * Used by tags to process the input args and output two input arguments consumer for
     * modifying the component.
     */
    @FunctionalInterface
    public interface Processor {

        /**
         * Process the tag using given arguments.
         * @param parser parser used for processing
         * @param args arguments given in the parsing text, e.g. {@literal <color:red> -> [red]}
         * @return consumer with component builder used by the parser and current text position in percentages from
         * start to end for current tag, -1 if no position is set (means there is no tag depending on position in current section)
         */
        @NotNull BiConsumer<ComponentBuilder, @Range(from = -1, to = 99) Integer> process(final @NotNull TextParser parser, String @NotNull [] args);

    }

    /**
     * Tests if the cancelling tag can remove the tag with given arguments.
     */
    @FunctionalInterface
    public interface Remover {

        /**
         * Tests if the cancelling tag can remove the parsed tag with given arguments.
         * @param tag parsed tag that should or shouldn't be cancelled depending on the arguments
         * @param args arguments used by the cancelling tag
         * @return if the parsed tag should be cancelled
         */
        boolean remove(@NotNull ParsedTag tag, String @NotNull [] args);

    }


    /**
     * Represents tag parsed by the parser.
     */
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static final class ParsedTag {

        /**
         * Tag used by this parsed tag.
         */
        @NotNull Tag tag;

        /**
         * If the tag cancels the effect (starts with remove character).
         */
        boolean cancel;

        /**
         * Arguments used in this tag.
         */
        String @NotNull [] args;

        /**
         * Start position in the raw text without tags.
         */
        int start;

        /**
         * End position in the raw text without tags.
         * <p>
         * Has to be canceled by cancel tag otherwise -1 is returned.
         */
        @NonFinal private int end = -1;

    }

    /**
     * Represents constant tag replaceable with normal tag.
     * <p>
     * e.g. {@literal <red> -> <color:red>}
     */
    public interface Constant {

        /**
         * Returns new simple constant.
         * @param input input constant tag
         * @param output output normal tag
         * @return new simple constant from given arguments
         */
        static @NotNull Constant simple(@NotNull String input, @NotNull String output) {
            return new SimpleConstant(input, output);
        }

        /**
         * Checks if the input matches the constant tag pattern and can
         * be replaced with its output.
         * @param input input tag
         * @return if the input tag matches the constant
         */
        boolean match(@NotNull String input);

        /**
         * Returns full tag from constant tag input.
         * <p>
         * e.g. {@literal <red> -> <color:red>}
         * @param input input constant tag
         * @return constant converted to normal tag
         */
        @NotNull @NonNls String output(@NotNull String input);

        /**
         * Simple implementation for constant interface.
         */
        record SimpleConstant(@NotNull String input, @NotNull String output) implements Constant {

            @Override
            public boolean match(@NotNull String input) {
                return this.input.equals(input);
            }

            @Override
            public @NotNull String output(@NotNull String input) {
                if (!match(input)) throw new IllegalStateException();
                return output;
            }
        }

    }

}
