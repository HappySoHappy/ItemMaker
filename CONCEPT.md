add LocaleHolder class/interface which will allow to store locale for anything under uuid's:
console,
player etc...
need a item-updater utility

add custom gui themes like shopgui+

ItemProperty is a value which is stored in item's file,
it is used to construct a bukkit item, such properties would be:
Material, Enchantments, Display name, Lore and similar.
ItemProperties should be parsed for every player, example:
Items Display name: %player%< translate:items.players_sword >
which would result in: %player%'s Sword
and so on for every player (excluding chat display and similar)
notes:
    how do we handle itemframe displays? perhaps we should create LastOwner context
    which would hold last owners display name

ItemAttribute is a value that is stored in item's NBT,
it is used to attach generic code to any item, it may be used in following ways:
ItemUnrepairableAttribute - makes the item unrepairable in the anvil
ItemUnenchantableAttribute - makes the item unenchantable in the enchanting table
ItemRainbowAttribute - which would make the item change its color (armor / potion and similar)
ItemUnstackableAttribute - makes the item unstackable by comparing inventory clicks,
  could also be made into a context by storing it like this:
  ItemMaker: {
    Context: {
      "Unstackable":
        [0]: 123
        [1]: 456
        [2]: 789
        [3]: 1011
    }
  }
  then this would mean that all items gotten from registry could return unique items

ItemIfAtrributes - changes item's state based on pre-programed conditions,
for example IfLoreAttribute would change items lore to display player stats,
and IfDisplayNameAttribute would change items display name to dark red when
players health is less than 5.

item attributes may change values that properties originally set:
for example ItemRarityAttribute which would set 1st line of lore to a specific string:
normal, rare, epic, legendary etc... any arbitrary value which would be guaranteed to be 1st line

notes:
  attributes and contexts could be used interchangably as shown by unstackable attribute,
  but generally context will  be reserved for external plugins,
  attributes are generally "unchangable" meaning that all items with the same id's
  will have the same attributes

ItemContext is a key:value store in the item's NBT,
it may be used by external plugins to hold relevant data for the item.
such example would be custom enchantments:
ItemMaker: {
  Context: {
    "Supercharged": 4
  }
}
and once again, both attribute and a context could be used to achieve this:
ItemDisabledContext/Attribute - marks item as unusable / not retrieviable from registry
item state cannot be changed - lore display name and similar stay the same, player cannot
break blocks with it, attack other players or interact with the world in other ways

item maker items rely on a item provider:
an itemprovider provides a base item with all placeholders in place ready to be replaced.

there are several types of items itemmaker provides:
Item's - most basic form, they dont do anything, basically bukkit itemstacks
UpdatingItem's - these items may be updated on a periodic timer

note:
  a context may change for each item, meaning that there may exist 2 items under the same id
  but with different contexts.
