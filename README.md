# ItemMaker
ItemMaker is a set of powerful tools for server owners and developers designed to make
creation of custom items quick and easy.

# Installation
- Download latest ItemMaker plugin from [link]
- Place the ItemMaker.jar in the /plugins folder.
- Restart the server, and you're good to go :)

# Usage
general usage etc... + examples
skript usage:
<data type> property named <string>

## Text Parser
https://docs.advntr.dev/minimessage/format.html
Generally works with every string in configuration.
Text Parser is used to format strings to proper representation.
### Tag
A parser tag consists of 5 parts:
- `<` - Tag start character
- `>` - Tag end character
- `/` - Tag close character
- `:` - Tag split Key:Value character
- `\\` - Tag escape character

### Color Tag
Defines a color
- `<color>...` - Defines a color
- `\<color:value>...` - Explicitly defines tag as a color
- `<color>...</color>` - Explicitly starts and closes color tag

| ChatColor      | Parser                      | Usage                                      |
|----------------|-----------------------------|--------------------------------------------|
| `BLACK`        | `black`                     | `<black>text`                              |
| `DARK_BLUE`    | `dark_blue`                 | `\<color:dark_blue>text`                   |
| `DARK_GREEN`   | `dark_green`                | `<dark_green>text</color>`                 |
| `DARK_AQUA`    | `dark_aqua`                 | `<dark_aqua>text`                          |
| `DARK_RED`     | `dark_red`                  | `<dark_red>text`                           |
| `DARK_PURPLE`  | `dark_purple`               | `<dark_purple>text`                        |
| `GOLD`         | `gold`                      | `<gold>text`                               |
| `GRAY`         | `gray`<br/>`grey`           | `<gray>text`                               |
| `DARK_GRAY`    | `dark_gray`<br/>`dark_grey` | `<dark_gray>text`                          |
| `BLUE`         | `blue`                      | `<blue>text`                               |
| `GREEN`        | `green`                     | `<green>text`                              |
| `AQUA`         | `aqua`                      | `<aqua>text`                               |
| `RED`          | `red`                       | `<red>text`                                |
| `LIGHT_PURPLE` | `light_purple`              | `<light_purple>text`                       |
| `YELLOW`       | `yellow`                    | `<yellow>text`                             |
| `WHITE`        | default                     | `<white>text`<br/>`text`                   |
| Not Supported  | `#RRGGBB`                   | `\<color:#RRGGBB>text`<br/>`<#RRGGBB>text` |
*Technically HEX colors are supported, but only through Bungee ChatColor.

### Decoration Tag
Defines formatting
- `<decoration>...` - Defines a decoration
- `\<decoration:value>...` - Explicitly defines tag as a decoration
- `<decoration>...</decoration>` - Explicitly starts and closes decoration tag

| ChatColor       | Parser          | Aliases           | Usage                         |
|-----------------|-----------------|-------------------|-------------------------------|
| `BOLD`          | `bold`          | `b`               | `<bold>text`                  |
| `ITALIC`        | `italic`        | `em`<br/>`i`      | `\<decoration:italic>text`    |
| `UNDERLINE`     | `underlined`    | `u`               | `<underline>text</underline>` |
| `STRIKETHROUGH` | `strikethrough` | `st`              | `<strikethrough>text`         |
| `MAGIC`         | `obfuscated`    | `obf`<br/>`magic` | `<obfuscated>text`            |

### Click Tag

### Hover Tag

### Key Tag

### Lang Tag

### Insertion Tag

### Rainbow Tag

### Gradient Tag

### Transition Tag

### Font Tag

### New Line Tag

### Selector Tag



# Contributing
All contributions are always welcome! :D

# Concepts
## Item

## RegisteredItem

## Properties, Attributes & Contexts

### ItemProperty
ItemProperty is a value that is stored in Item's file and is used to create a Bukkit ItemStack,
it represents the most basic, yet essential information about the Item.

Let's see some examples:

- IdentifierProperty,
Since all Items MUST have a unique identifier, and we must know an identifier to register an Item.
We will use a Property for that purpose since identifier won't change after registration.


- MaterialProperty,
All Items MUST have a valid Material property - without it, we cannot create a Bukkit ItemStack.


- DisplayNameProperty,

- LoreProperty,

### ItemAttribute
ItemAttribute is a value that is stored in Item's NBT data and is used to attach generic behaviour (code)
to the Item.

### ItemContext
ItemContext is a key:value that is stored in Item's NBT data, and represents additional data attached to the Item.

# Quirks
### Attributes (Minecraft)
because of how attributes impact stackability* of a item,
their UUID is generated in the following way:
- Get name of attribute, for example "generic.movement_speed"
- Get byte[] representation of the name in UTF-8 charset
- Pass byte[] representation to UUID.nameUUIDFromBytes(byte[])
- Assign that UUID to the attribute