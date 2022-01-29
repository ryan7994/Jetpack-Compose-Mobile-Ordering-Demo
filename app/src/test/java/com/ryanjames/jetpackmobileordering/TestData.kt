package com.ryanjames.jetpackmobileordering

import com.ryanjames.jetpackmobileordering.domain.*

val NO_CHEESE
    get() = ModifierInfo("M1000", "No Cheese", 0f, "NCH")
val AMERICAN_CHEESE
    get() = ModifierInfo("M1001", "American Cheese", 0f, "ACH")
val LETTUCE
    get() = ModifierInfo("M2000", "Lettuce", 1f, "LTC")
val MUSHROOM
    get() = ModifierInfo("M2001", "Mushroom", 2f, "MSH")
val BACON
    get() = ModifierInfo("M2002", "Bacon", 3f, "BCN")
val MODIFIER_GROUP_CHEESE
    get() = ModifierGroup("MG1000", "Cheese", ModifierGroupAction.Required, AMERICAN_CHEESE, listOf(NO_CHEESE, AMERICAN_CHEESE), 1, 1)
val MODIFIER_GROUP_TOPPING
    get() = ModifierGroup("MG2000", "Topping", ModifierGroupAction.Optional, null, listOf(LETTUCE, MUSHROOM, BACON), 0, 5)
val SMALL_FRIES
    get() = ModifierInfo("M3000", "Small Fries", 0f, "SMF")
val LARGE_FRIES
    get() = ModifierInfo("M3001", "Large Fries", 0f, "LRF")
val PRODUCT_COKE
    get() = Product("D4000", "Coke", "", 10f, "CKE", listOf(), listOf(), null)
val PRODUCT_PEPSI
    get() = Product("D4001", "Pepsi", "", 0f, "PEP", listOf(), listOf(), null)
val PRODUCT_DR_PEPPER
    get() = Product("D4002", "Dr. Pepper", "", 0f, "DRP", listOf(), listOf(), null)
val MODIFIER_GROUP_FRIES
    get() = ModifierGroup("MG3000", "Size", ModifierGroupAction.Required, SMALL_FRIES, listOf(SMALL_FRIES, LARGE_FRIES), 1, 1)
val PRODUCT_FRIES
    get() = Product("F1000", "Fries", "Description", 3f, "", listOf(), listOf(MODIFIER_GROUP_FRIES), null)
val PRODUCTS_TOTS
    get() = Product("T1000", "Tots", "Description", 3f, "", listOf(), listOf(), null)
val PRODUCT_GROUP_DRINKS
    get() = ProductGroup("PG1000", "Drinks", PRODUCT_COKE, listOf(PRODUCT_COKE, PRODUCT_PEPSI, PRODUCT_DR_PEPPER), 1, 1)
val PRODUCT_GROUP_SIDES
    get() = ProductGroup("PG1001", "Sides", PRODUCT_FRIES, listOf(PRODUCT_FRIES, PRODUCTS_TOTS), 1, 1)
val CHEESE_BURGER_MEAL
    get() = ProductBundle("B1000", "Cheese Burger Meal", 12f, "CBM", listOf(PRODUCT_GROUP_DRINKS, PRODUCT_GROUP_SIDES))
val PRODUCT_CHEESE_BURGER
    get() = Product("C1000", "Cheese Burger", "Description", 6.5f, "CHB", listOf(CHEESE_BURGER_MEAL), listOf(MODIFIER_GROUP_CHEESE, MODIFIER_GROUP_TOPPING), null)


val UNKNOWN_MODIFIER
    get() = ModifierInfo("UNKNOWN", "Unknown Modifier", 100f, "")

val PRODUCT_UNKNOWN_DRINK
    get() = Product("D11000", "Unknown", "", 0f, "CKE", listOf(), listOf(), null)

val KEY_CHEESE
    get() = ProductModifierGroupKey(PRODUCT_CHEESE_BURGER, MODIFIER_GROUP_CHEESE)

val KEY_TOPPING
    get() = ProductModifierGroupKey(PRODUCT_CHEESE_BURGER, MODIFIER_GROUP_TOPPING)

val KEY_FRIES
    get() = ProductModifierGroupKey(PRODUCT_FRIES, MODIFIER_GROUP_FRIES)

val MEAL_SELECTIONS
    get() = hashMapOf(PRODUCT_GROUP_SIDES to listOf(PRODUCT_FRIES), PRODUCT_GROUP_DRINKS to listOf(PRODUCT_PEPSI))

val DEFAULT_MEAL_SELECTION
    get() = hashMapOf(PRODUCT_GROUP_SIDES to listOf(PRODUCT_FRIES), PRODUCT_GROUP_DRINKS to listOf(PRODUCT_COKE))

val MODIFIER_SELECTIONS
    get() = hashMapOf(KEY_CHEESE to listOf(NO_CHEESE), KEY_TOPPING to listOf(LETTUCE, BACON), KEY_FRIES to listOf(LARGE_FRIES))

val LINE_ITEM_MEAL: LineItem
    get() = LineItem("aaa", PRODUCT_CHEESE_BURGER, CHEESE_BURGER_MEAL, MEAL_SELECTIONS, MODIFIER_SELECTIONS, 1)
