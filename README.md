# Preindexed Enchanting
Preindexed Enchanting is a mod made for the [REDACTED] modpack, and as such has no plans on moving to fabric or versions other than 1.20.1. Feel free to port it yourself, or see if [Indexed Enchanting](https://modrinth.com/mod/indexed-enchanting) by Astrazoey works for you.

- [Features](#features)
  - [Enchantment Slots](#enchantment-slots)
  - [Level Limits](#level-limits)
  - [Edge Cases](#edge-cases)
- [Vanilla Changes](#vanilla-changes)
  - [Enchantment Incompatibility](#enchantment-incompatibility)

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

For enchantments that do not function well together (e.g. Fortune and Silk Touch), [TODO]

</details>
