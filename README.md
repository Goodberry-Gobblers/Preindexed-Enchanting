# Preindexed Enchanting
Preindexed Enchanting is a mod made for the [REDACTED] modpack, and as such has no plans on moving to fabric or versions other than 1.20.1. Feel free to port it yourself, or see if [Indexed Enchanting](https://modrinth.com/mod/indexed-enchanting) by Astrazoey works for you.

- [Features](#features)
  - [Enchantment Slots](#enchantment-slots)
  - [Level Limits](#level-limits)
  - [Edge Cases](#edge-cases)
- [Vanilla Changes](#vanilla-changes)
  - [Enchantment Incompatibility](#enchantment-incompatibility)
- [Configuration / Modpack Development](#configuration--modpack-creation)
  - [Change Default Values](#change-default-values)
  - [Configure Modded Items](#configure-modded-items)
  - [Make Enchantments Incomptatible](#make-enchantments-incomptatible)


## Features

### Enchantment Slots
<details open>

The flagship feature of the mod, enchantment slots. Some items might have enchantment slots. Any enchantments can be applied to these items as long as the sum of their levels is equal to or less than the item's max enchantment slots.

<img width="604" height="472" alt="image" src="https://github.com/user-attachments/assets/97ca6338-d4f6-4c8c-b160-2d368bfc39d5" />

Curses increase the amount of slots an item has (default is +2 per curse), which can allow you to squeeze more out of your gear at a price.

<img width="590" height="538" alt="image" src="https://github.com/user-attachments/assets/66c8223f-1782-4662-b1a8-116208ff6d15" />

</details>

### Level Limits
<details open>
Other items might have a max limit for their enchantments. Any number of enchantments can be applied to these items as long as the enchantments' levels are equal to or less than the item's max limit.

<img width="604" height="670" alt="image" src="https://github.com/user-attachments/assets/e100b627-040f-4d83-ade1-b9c4fe8eedfc" />

Curses do not effect an item's max limit.

<details>

  <summary>(P.S. if you don't like roman numerals, resourcepacks that relpace them will work with max limits)</summary>

  <img width="602" height="662" alt="image" src="https://github.com/user-attachments/assets/e70e60a3-462e-4e57-90df-5bd5e6d11229" />

  screenshot taken with the [Enchant Display: Arabic numerals](https://modrinth.com/resourcepack/enchant-display-arabic) resourcepack.

</details>
</details>

### Edge Cases
<details open>
  
There are special edge cases for when an item is set to have either -32,768 or 32,767 enchantment slots. Respectively, these create unenchantable and infintiely enchantable items.

| Unenchantable | Infinitely Enchantable |
| --- | --- |
| <img width="606" height="368" alt="image" src="https://github.com/user-attachments/assets/43dadd02-5c94-4e2a-980d-503bd0ca9e55" /> | <img width="612" height="666" alt="image" src="https://github.com/user-attachments/assets/b3ea9b97-1ab8-4e34-9b97-504761ae93b5" /> |

</details>

## Vanilla Changes

### Enchantment Incompatibility
<details open>

Incompatibility between enchantments has been removed. If an item has enough enchantment slots, it can have any and all applicable enchantments.

<img width="536" height="466" alt="image" src="https://github.com/user-attachments/assets/9e9f176b-7375-49fa-9223-56ca03c65a90" />

For enchantments that do not function well together (e.g. Fortune and Silk Touch), only one will be active at a time. Inactive enchantments appear crossed out at the bottom of the enchantment list, and can be swapped with the active enchantment by right clicking the item in your inventory.

| Before right clicking | After right clicking |
| --- | --- |
| <img width="454" height="380" alt="image" src="https://github.com/user-attachments/assets/9b360fec-5050-4139-835d-b0634cad3732" /> | <img width="482" height="380" alt="image" src="https://github.com/user-attachments/assets/7ba57eed-760e-4eb9-95c1-bc1662063910" /> |

</details>


## Configuration / Modpack Creation

The default configuration of this mod is meant to showcase the features of the mod, and as such it is recommended to change the default values; they may not be balanced for gameplay.

### Change Default Values
<details open>

The enchantment slots of an item type is obtained from a datapack. In this repository, you can find [a datapack with the default values](example-datapack) to function as a template. Each entry represents and item and has an associated number. Reference the table below for the function of the number.

| Value | Effect |
| --- | --- |
| 0...32766 | Enchantment Slots: 0...32766 |
| -32767...-1 | Max Level: 32767...1 |
| 32767 | Infinitely Enchantable |
| -32768 | Unenchantable |

#### Examples

This sets the turtle helmet to have 3 enchantment slots.
```json
"minecraft:turtle_helmet": 3
```

This sets it to have a max limit of 3.
```json
"minecraft:turtle_helmet": -3
```

This will have 0 slots, which is not the same as unenchantable. Items with 0 slots may be enchanted if they are first cursed (if curses are configured to give a positive amount of slots).
```json
"minecraft:turtle_helmet": 0
```

This is how you would make an unenchantable item.
```json
"minecraft:turtle_helmet": -32678
```

</details>

### Configure Modded items
<details open>

To add enchantment slots or a max limit to a modded item, you may add a new line in the `data/preindexedenchanting/preindexedenchanting/maxslots/maxslots.json` file in a datapack using the modded item's mod ID and name. From there, configure as you would [a vanilla item](#change-default-values).

#### Examples
```json
"create:copper_backtank": 100
```
```json
"spartanweaponry:diamond_longsword": 20
```

</details>

### Make Enchantments Incomptatible
<details open>

Enchantment incompatibility has been removed, but since some enchantments do not work when others are present, [a workaround has been implemented](#enchantment-incompatibility). To mark a set of enchantments as mutaully exclusive with each other, create a new list in the `data/preindexedenchanting/preindexedenchanting/incompatible/incompatible.json` file in the datapack you're using for [configuring the slots and limits of items](#change-default-values) containing the enchantment's IDs.

#### Examples

Sets Silk Touch and Fortune to be incompatible with each other, in addition to Sharpness, Smite, and Bane of Arthropods being incompatible with each other.
```json
[
  [
    "minecraft:fortune",
    "minecraft:silk_touch"
  ],
  [
    "minecraft:sharpness",
    "minecraft:smite",
    "minecraft:bane_of_arthropods"
  ]
]
```

It is recommended to always keep Fortune and Silk Touch, and Riptide and Loyalty incompatible because if both enchantments are on an item, it will overwrite the function of the other.

</details>
