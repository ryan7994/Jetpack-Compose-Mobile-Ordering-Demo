package com.ryanjames.jetpackmobileordering.constants

import com.ryanjames.jetpackmobileordering.domain.*


val mockVenueList = listOf(
    Venue(
        "A",
        "Jollibee",
        "15518 Cantrece Ln Cerritos CA 90623",
        0f,
        0f,
        4.5f,
        100,
        20,
        30,
        "$",
        listOf("Filipino", "Fast Food", "Non-Traditional", "Fusion"),
        ""
    ),
    Venue(
        "B",
        "Thai BBQ",
        "118 Mutt Ln La Palma CA 90623",
        0f,
        0f,
        4.75f,
        211,
        30,
        45,
        "$$$",
        listOf("Thai", "Gourmet"),
        ""
    )

)


private val drinks = Category("DRK", "Drinks", listOf(PRODUCT_DR_PEPPER, PRODUCT_COKE, PRODUCT_PEPSI))
private val sides = Category("SDE", "Sides", listOf(PRODUCTS_TOTS, PRODUCT_FRIES))
private val burgers = Category("BRG", "Burgers", listOf(PRODUCT_CHEESE_BURGER))

private val NO_CHEESE
    get() = ModifierInfo("M1000", "No Cheese", 0f, "NCH")
private val AMERICAN_CHEESE
    get() = ModifierInfo("M1001", "American Cheese", 0f, "ACH")
private val LETTUCE
    get() = ModifierInfo("M2000", "Lettuce", 1f, "LTC")
private val MUSHROOM
    get() = ModifierInfo("M2001", "Mushroom", 2f, "MSH")
private val BACON
    get() = ModifierInfo("M2002", "Bacon", 3f, "BCN")
private val MODIFIER_GROUP_CHEESE
    get() = ModifierGroup("MG1000", "Cheese", ModifierGroupAction.Required, AMERICAN_CHEESE, listOf(NO_CHEESE, AMERICAN_CHEESE), 1, 1)
private val MODIFIER_GROUP_TOPPING
    get() = ModifierGroup("MG2000", "Topping", ModifierGroupAction.Optional, null, listOf(LETTUCE, MUSHROOM, BACON), 0, 5)
private val SMALL_FRIES
    get() = ModifierInfo("M3000", "Small Fries", 0f, "SMF")
private val LARGE_FRIES
    get() = ModifierInfo("M3001", "Large Fries", 0f, "LRF")
val PRODUCT_COKE
    get() = Product("D4000", "Coke", "Take a sip of this ice cold Coke and be refreshed.", 0f, "CKE", listOf(), listOf(),  "https://images.unsplash.com/photo-1591254467235-a82a70c915ee?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=2089&q=80")
private val PRODUCT_PEPSI
    get() = Product("D4001", "Pepsi", "", 0f, "PEP", listOf(), listOf(), null)
private val PRODUCT_DR_PEPPER
    get() = Product("D4002", "Dr. Pepper", "", 0f, "DRP", listOf(), listOf(), null)
private val MODIFIER_GROUP_FRIES
    get() = ModifierGroup("MG3000", "Size", ModifierGroupAction.Required, SMALL_FRIES, listOf(SMALL_FRIES, LARGE_FRIES), 1, 1)
private val PRODUCT_FRIES
    get() = Product("F1000", "Fries", "Description", 3f, "", listOf(), listOf(MODIFIER_GROUP_FRIES), "https://images.unsplash.com/photo-1526230427044-d092040d48dc?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1650&q=80")
private val PRODUCTS_TOTS
    get() = Product("T1000", "Tots", "Description", 3f, "", listOf(), listOf(), "https://media2.s-nbcnews.com/i/newscms/2017_08/1196437/tater-tot-today-170220-tease_80ee09e7dd2206c759e9cd903cc64ca8.jpg")
private val PRODUCT_GROUP_DRINKS
    get() = ProductGroup("PG1000", "Drinks", PRODUCT_COKE, listOf(PRODUCT_COKE, PRODUCT_PEPSI, PRODUCT_DR_PEPPER), 1, 1)
private val PRODUCT_GROUP_SIDES
    get() = ProductGroup("PG1001", "Sides", PRODUCT_FRIES, listOf(PRODUCT_FRIES, PRODUCTS_TOTS), 1, 1)
private val CHEESE_BURGER_MEAL
    get() = ProductBundle("B1000", "Cheese Burger Meal", 12f, "CBM", listOf(PRODUCT_GROUP_DRINKS, PRODUCT_GROUP_SIDES))

private val PRODUCT_CHEESE_BURGER
    get() = Product(
        "C1000",
        "Cheese Burger",
        "Description",
        6.5f,
        "CHB",
        listOf(CHEESE_BURGER_MEAL),
        listOf(MODIFIER_GROUP_CHEESE, MODIFIER_GROUP_TOPPING),
        "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1502&q=80"
    )


val mockMenu = Menu(listOf(drinks, sides, burgers))

private val UNKNOWN_MODIFIER
    get() = ModifierInfo("UNKNOWN", "Unknown Modifier", 100f, "")

private val PRODUCT_UNKNOWN_DRINK
    get() = Product("D11000", "Unknown", "", 0f, "CKE", listOf(), listOf(), null)

//    private val KEY_CHEESE
//        get() = ProductModifierGroupKey(PRODUCT_CHEESE_BURGER, MODIFIER_GROUP_CHEESE)
//
//    private val KEY_TOPPING
//        get() = ProductModifierGroupKey(PRODUCT_CHEESE_BURGER, MODIFIER_GROUP_TOPPING)
//
//    private val KEY_FRIES
//        get() = ProductModifierGroupKey(PRODUCT_FRIES, MODIFIER_GROUP_FRIES)

//    private val MEAL_SELECTIONS
//        get() = hashMapOf(PRODUCT_GROUP_SIDES to listOf(PRODUCT_FRIES), PRODUCT_GROUP_DRINKS to listOf(PRODUCT_PEPSI))

//    private val DEFAULT_MEAL_SELECTION
//        get() = hashMapOf(PRODUCT_GROUP_SIDES to listOf(PRODUCT_FRIES), PRODUCT_GROUP_DRINKS to listOf(PRODUCT_COKE))

//    private val MODIFIER_SELECTIONS
//        get() = hashMapOf(KEY_CHEESE to listOf(NO_CHEESE), KEY_TOPPING to listOf(LETTUCE, BACON), KEY_FRIES to listOf(LARGE_FRIES))

//    private val LINE_ITEM_MEAL: LineItem
//        get() = LineItem("aaa", PRODUCT_CHEESE_BURGER, CHEESE_BURGER_MEAL, MEAL_SELECTIONS, MODIFIER_SELECTIONS, 1)
