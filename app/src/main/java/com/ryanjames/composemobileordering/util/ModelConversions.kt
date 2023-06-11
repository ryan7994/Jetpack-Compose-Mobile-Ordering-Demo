package com.ryanjames.composemobileordering.util

import com.ryanjames.composemobileordering.db.model.*
import com.ryanjames.composemobileordering.domain.*
import com.ryanjames.composemobileordering.features.bag.BagItemRowDisplayModel
import com.ryanjames.composemobileordering.features.home.FeaturedRestaurantCardState
import com.ryanjames.composemobileordering.features.home.RestaurantCardState
import com.ryanjames.composemobileordering.features.venuedetail.CategoryViewState
import com.ryanjames.composemobileordering.network.model.request.LineItemRequestBody
import com.ryanjames.composemobileordering.network.model.request.ModifierSelectionRequestBody
import com.ryanjames.composemobileordering.network.model.request.ProductInOrderRequestBody
import com.ryanjames.composemobileordering.network.model.response.*
import com.ryanjames.composemobileordering.toTwoDigitString
import com.ryanjames.composemobileordering.ui.widget.MenuItemCardDisplayModel
import com.ryanjames.composemobileordering.ui.widget.RestaurantDisplayModel
import com.ryanjames.composemobileordering.ui.widget.StoreInfoDisplayModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

fun HomeResponse.toDomain(): Pair<List<Venue>, List<Venue>> {
    val featuredStores = this.featuredStores.mapNotNull { it.toDomain() }
    val restaurants = this.restaurants.mapNotNull { it.toDomain() }
    return Pair(featuredStores, restaurants)
}

fun VenueResponse.toDomain(): Venue? {
    if (storeId == null || storeName == null) {
        return null
    }
    return Venue(
        id = storeId,
        name = storeName,
        address = storeAddress,
        lat = lat?.toFloat() ?: 0.0f,
        long = long?.toFloat() ?: 0.0f,
        rating = rating ?: 0f,
        numberOfRatings = numRating ?: 0,
        deliveryTimeInMinsHigh = prepMax ?: 0,
        deliveryTimeInMinsLow = prepMin ?: 0,
        priceIndicator = priceLevel ?: "",
        categories = categories.orEmpty(),
        featuredImage = featuredImage,
        storeHours = listOf()
    )
}

fun Venue.toRestaurantCardState(): RestaurantCardState = RestaurantCardState(
    venueId = id,
    venueName = name,
    venueCategories = categories.joinToString(", "),
    rating = rating.toTwoDigitString(),
    numberOfRatings = "(${numberOfRatings})",
    imageUrl = featuredImage
)

fun Venue.toFeaturedRestaurantCardState(): FeaturedRestaurantCardState =
    FeaturedRestaurantCardState(
        venueId = id,
        venueName = name,
        venueCategories = categories.joinToString(", "),
        rating = rating.toTwoDigitString(),
        numberOfRatings = "(${numberOfRatings})",
        priceLevel = priceIndicator,
        deliveryTime = "${deliveryTimeInMinsLow}-${deliveryTimeInMinsHigh} mins",
        imageUrl = featuredImage
    )

fun Venue.toRestaurantDisplayModel() = RestaurantDisplayModel(
    venueName = name,
    venueAddress = address ?: "",
    categories = categories,
    rating = rating.toTwoDigitString(),
    noOfReviews = "($numberOfRatings ratings)"
)

fun Venue.toStoreInfoDisplayModel() = StoreInfoDisplayModel(
    address = address,
    phone = "+500-000-0000"
)

fun BasicMenuResponse.toCategoryViewStateList() = this.categories.map {
    CategoryViewState(
        categoryName = it.categoryName ?: "",
        menuItems = it.products?.mapNotNull { product -> product.toMenuItemCardState() } ?: emptyList()
    )
}

fun BasicMenuWithCategories.toCategoryViewStateList(): List<CategoryViewState> {
    return this.categories.map {
        CategoryViewState(
            categoryName = it.category.categoryName,
            menuItems = it.products.map { productEntity ->
                MenuItemCardDisplayModel(
                    id = productEntity.productId,
                    name = productEntity.productName,
                    calories = "50 cal",
                    price = "$${productEntity.price.toTwoDigitString()}",
                    imageUrl = productEntity.imageUrl ?: ""
                )
            }
        )
    }
}

fun BasicProductResponse.toMenuItemCardState() = productName?.let {
    MenuItemCardDisplayModel(
        name = productName,
        calories = "50 cal",
        price = "$${price?.toTwoDigitString()}",
        imageUrl = imageUrl ?: "",
        id = productId ?: ""
    )
}

fun HomeResponse.toEntity(): List<Pair<VenueEntity, List<VenueCategoryEntity>>> {
    val list = mutableListOf<Pair<VenueEntity, List<VenueCategoryEntity>>>()
    list.addAll(this.featuredStores.map { it.toEntity(VenueEntityType.HOME_FEATURED) })
    list.addAll(restaurants.map { it.toEntity(VenueEntityType.HOME_RESTAURANT_LIST) })
    return list
}

fun VenueResponse.toStoreHoursEntity(): List<StoreHoursEntity> {
    return storeId?.let {
        storeHours?.map {
            it.toEntity(storeId)
        }
    } ?: listOf()
}

fun VenueResponse.toEntity(type: String): Pair<VenueEntity, List<VenueCategoryEntity>> {
    val venueEntity = VenueEntity(
        venueId = storeId ?: "",
        name = storeName ?: "",
        address = storeAddress,
        lat = lat?.toFloat() ?: 0.0f,
        longitude = long?.toFloat() ?: 0.0f,
        rating = rating ?: 0f,
        numberOfRatings = numRating ?: 0,
        deliveryTimeInMinsHigh = prepMax ?: 0,
        deliveryTimeInMinsLow = prepMin ?: 0,
        priceIndicator = priceLevel ?: "",
        featuredImage = featuredImage,
        type = type,
        creationTimeInMills = System.currentTimeMillis()
    )

    val categoryEntities = categories?.map { VenueCategoryEntity(it) } ?: listOf()
    return Pair(venueEntity, categoryEntities)
}

fun VenueDbModel.toDomain(): Venue {
    val venueEntity = this.venueEntity
    return Venue(
        id = venueEntity.venueId,
        name = venueEntity.name,
        address = venueEntity.address,
        lat = venueEntity.lat,
        long = venueEntity.longitude,
        rating = venueEntity.rating,
        numberOfRatings = venueEntity.numberOfRatings,
        deliveryTimeInMinsLow = venueEntity.deliveryTimeInMinsLow,
        deliveryTimeInMinsHigh = venueEntity.deliveryTimeInMinsHigh,
        priceIndicator = venueEntity.priceIndicator,
        categories = categories.map { it.categoryName },
        featuredImage = venueEntity.featuredImage,
        storeHours = this.storeHours.map { it.toDomain() }
    )
}

fun StoreHoursEntity.toDomain(): StoreHours {
    val day = getDay(day)
    val status = when {
        openingTime != null && closingTime != null -> {
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val opening = LocalTime.parse(openingTime, formatter)
            val closing = LocalTime.parse(closingTime, formatter)
            StoreStatus.Open(opening, closing)
        }
        else -> StoreStatus.Closed
    }
    return StoreHours(
        day = day,
        storeStatus = status
    )
}

fun VenueWithCategories.toDomain(): Venue {
    val venueEntity = venue
    val categoryEntity = categories
    return Venue(
        id = venueEntity.venueId,
        name = venueEntity.name,
        address = venueEntity.address,
        lat = venueEntity.lat,
        long = venueEntity.longitude,
        rating = venueEntity.rating,
        numberOfRatings = venueEntity.numberOfRatings,
        deliveryTimeInMinsLow = venueEntity.deliveryTimeInMinsLow,
        deliveryTimeInMinsHigh = venueEntity.deliveryTimeInMinsHigh,
        priceIndicator = venueEntity.priceIndicator,
        categories = categoryEntity.map { it.categoryName },
        featuredImage = venueEntity.featuredImage,
        storeHours = listOf()
    )
}

fun StoreHoursResponse.toEntity(venueId: String): StoreHoursEntity {
    return StoreHoursEntity(
        venueId = venueId,
        isClosed = false,
        day = day,
        openingTime = openingTime,
        closingTime = closingTime
    )
}

fun ProductDetailsResponse.toDomain(): Product {

    return Product(
        productId = productId,
        productName = productName,
        productDescription = productDescription ?: "",
        price = price,
        receiptText = receiptText,
        bundles = bundles.map { it.toDomain() },
        modifierGroups = modifierGroups.toDomain(),
        imageUrl = imageUrl
    )
}

private fun GetOrderMenuBundleResponse.toDomain(): ProductBundle {

    return ProductBundle(
        bundleId = bundleId ?: "",
        bundleName = bundleName ?: "",
        price = price ?: 0f,
        receiptText = receiptText ?: "",
        productGroups = productGroups?.map { it.toDomain() } ?: listOf()
    )
}

private fun GetOrderMenuProductGroupResponse.toDomain(): ProductGroup {

    val products = options?.map { it.toDomain() }
    val defaultProd = products?.find { it.productId == defaultProduct }

    return ProductGroup(
        productGroupId ?: "",
        productGroupName ?: "",
        defaultProduct = defaultProd ?: Product.EMPTY,
        min = min ?: 1,
        max = max ?: 1,
        options = products ?: listOf()
    )
}

private fun GetOrderProductGroupProductResponse.toDomain(): Product {
    return Product(productId, productName, "", 0f, "", bundles = listOf(), modifierGroups = this.modifierGroups.toDomain(), imageUrl = null)
}


private fun List<ModifierGroupResponse>.toDomain(): List<ModifierGroup> = map { response ->
    ModifierGroup(
        modifierGroupId = response.modifierGroupId ?: "",
        modifierGroupName = response.modifierGroupName ?: "",
        action = response.action?.toModifierGroupAction() ?: ModifierGroupAction.Optional,
        defaultSelection = response.options?.find { it.modifierId == response.defaultSelection }?.toDomain(),
        options = response.options?.map { it.toDomain() } ?: listOf(),
        min = response.min ?: 0,
        max = response.max ?: 0
    )
}

private fun ModifierInfoResponse.toDomain(): ModifierInfo = ModifierInfo(
    modifierId = this.modifierId ?: "",
    modifierName = this.modifierName ?: "",
    priceDelta = this.priceDelta ?: 0f,
    receiptText = this.receiptText ?: ""
)


private fun String?.toModifierGroupAction(): ModifierGroupAction {
    return when (this) {
        "on" -> ModifierGroupAction.Required
        "add" -> ModifierGroupAction.Optional
        else -> ModifierGroupAction.Required
    }
}

fun LineItemEntityWithProducts.toLineItemRequest(): LineItemRequestBody {
    val productInOrderRequestList = mutableListOf<ProductInOrderRequestBody>()

    // Base Product
    val baseProductModifierList = mutableListOf<ModifierSelectionRequestBody>()
    val baseProduct = this.products.find { it.product.productId == this.lineItem.productId }
    val baseProductModifiers = baseProduct?.modifiers ?: emptyList()

    for (productModifier in baseProductModifiers) {
        val modifierRequest = ModifierSelectionRequestBody(
            modifierGroupId = productModifier.modifierGroup.modifierGroupId,
            items = productModifier.modifierIds.map { it.modifierId }
        )
        baseProductModifierList.add(modifierRequest)
    }

    if (baseProduct != null) {
        val baseProductRequest = ProductInOrderRequestBody(
            productItemId = baseProduct.product.productItemId,
            productId = baseProduct.product.productId,
            productGroupId = baseProduct.product.productId,
            modifierSelections = baseProductModifierList
        )
        productInOrderRequestList.add(baseProductRequest)
    }

    // Bundle products
    val bundleProducts = this.products.minus(baseProduct)
    for (bundleProduct in bundleProducts.filterNotNull()) {
        val modifierList = mutableListOf<ModifierSelectionRequestBody>()
        for (productModifier in bundleProduct.modifiers) {
            val modifierRequest = ModifierSelectionRequestBody(
                modifierGroupId = productModifier.modifierGroup.modifierGroupId,
                items = productModifier.modifierIds.map { it.modifierId }
            )
            modifierList.add(modifierRequest)
        }

        val productRequest = ProductInOrderRequestBody(
            productItemId = bundleProduct.product.productItemId,
            productId = bundleProduct.product.productId,
            productGroupId = bundleProduct.product.productGroupId,
            modifierSelections = modifierList
        )
        productInOrderRequestList.add(productRequest)
    }

    return LineItemRequestBody(
        lineItemId = lineItem.lineItemId,
        quantity = lineItem.quantity,
        products = productInOrderRequestList,
        baseProduct = lineItem.productId,
        bundleId = lineItem.bundleId
    )
}

fun LineItem.toLineItemRequest(): LineItemRequestBody {

    val productInOrderRequestList = mutableListOf<ProductInOrderRequestBody>()

    // Base Product
    val baseProductModifierList = mutableListOf<ModifierSelectionRequestBody>()
    val baseProductModifiers = this.modifiers.filterKeys { it.product.productId == this.product.productId }

    for (productModifierGroup in baseProductModifiers.keys) {
        val modifierRequest = ModifierSelectionRequestBody(
            modifierGroupId = productModifierGroup.modifierGroup.modifierGroupId,
            items = modifiers[productModifierGroup]?.map { it.modifierId } ?: listOf())
        baseProductModifierList.add(modifierRequest)
    }

    val baseProductRequest = ProductInOrderRequestBody(
        productItemId = UUID.randomUUID().toString(),
        productId = this.product.productId,
        productGroupId = this.product.productId,
        modifierSelections = baseProductModifierList
    )

    productInOrderRequestList.add(baseProductRequest)


    // Products in bundle
    for (productGroup in this.productsInBundle.keys) {
        val products = productsInBundle[productGroup] ?: listOf()

        for (product in products) {

            val modifiers = this.modifiers.filterKeys { it.product.productId == product.productId }
            val modifierSelectionsRequest = mutableListOf<ModifierSelectionRequestBody>()

            for (productModifierGroup in modifiers.keys) {
                val modifierRequest = ModifierSelectionRequestBody(
                    modifierGroupId = productModifierGroup.modifierGroup.modifierGroupId,
                    items = modifiers[productModifierGroup]?.map { it.modifierId } ?: listOf())
                modifierSelectionsRequest.add(modifierRequest)
            }

            val productInOrderRequest = ProductInOrderRequestBody(
                productItemId = UUID.randomUUID().toString(),
                productId = product.productId,
                productGroupId = productGroup.productGroupId,
                modifierSelections = modifierSelectionsRequest
            )
            productInOrderRequestList.add(productInOrderRequest)
        }

    }

    return LineItemRequestBody(
        lineItemId = lineItemId,
        quantity = quantity,
        products = productInOrderRequestList,
        baseProduct = product.productId,
        bundleId = bundle?.bundleId
    )
}


fun GetOrderResponse.toBagSummary(): OrderSummary {
    return OrderSummary(
        lineItems = lineItems.map { it.toBagLineItem() },
        price = price,
        status = status.toOrderStatus(),
        orderId = orderId,
        storeId = storeId,
        storeName = storeName
    )
}

private fun String.toOrderStatus(): OrderStatus {
    return when (this) {
        "CREATED" -> OrderStatus.CREATED
        "CANCELLED" -> OrderStatus.CANCELLED
        "CHECKOUT" -> OrderStatus.CHECKOUT
        "PREPARING" -> OrderStatus.PREPARING
        "DELIVERING" -> OrderStatus.DELIVERING
        "DELIVERED" -> OrderStatus.DELIVERED
        "READY_FOR_PICK_UP" -> OrderStatus.READY_FOR_PICKUP
        "PICKED_UP" -> OrderStatus.PICKED_UP
        else -> OrderStatus.UNKNOWN
    }
}

fun GetOrderProductResponse.toEntity(lineItemId: String): LineItemProductEntity {
    return LineItemProductEntity(
        productItemId = productItemId,
        productId = productId,
        productGroupId = productGroupId,
        lineItemId = lineItemId,
        productName = productName
    )
}

fun GetOrderModifierResponse.toEntity(modifierGroupId: String): LineItemModifierInfoEntity {
    return LineItemModifierInfoEntity(
        id = UUID.randomUUID().toString(),
        modifierId = modifierId,
        modifierGroupId = modifierGroupId,
        modifierName = modifierName
    )
}

fun GetOrderModifierSelectionResponse.toEntity(productItemId: String): LineItemModifierGroupEntityWithModifiers {
    val id = UUID.randomUUID().toString()
    return LineItemModifierGroupEntityWithModifiers(
        modifierGroup = LineItemModifierGroupEntity(
            id = id,
            modifierGroupId = this.modifierGroupId,
            productItemId = productItemId
        ),
        modifierIds = this.modifiers.map { it.toEntity(id) }
    )
}

fun GetOrderResponse.toOrderEntity(): CurrentOrderEntityWithLineItems {
    val bagSummary = this.toBagSummary()
    return CurrentOrderEntityWithLineItems(
        order = CurrentOrderEntity(
            orderId = orderId,
            subtotal = bagSummary.subtotal,
            tax = bagSummary.tax,
            total = bagSummary.price,
            storeName = storeName,
            storeId = storeId
        ),
        lineItems = this.lineItems.map { it.toEntity() }
    )
}


fun GetOrderLineItemResponse.toEntity(): LineItemEntityWithProducts {
    val lineItemEntity = LineItemEntity(
        lineItemId = lineItemId,
        productId = baseProduct,
        bundleId = bundleId,
        quantity = quantity,
        lineItemName = lineItemName,
        price = price,
        currentOrderId = 0
    )
    val productEntityList = products.map { productResponse ->
        LineItemProductEntityWithModifiers(
            product = productResponse.toEntity(this.lineItemId),
            modifiers = productResponse.modifierSelections.map { it.toEntity(productResponse.productItemId) }
        )
    }
    return LineItemEntityWithProducts(
        lineItem = lineItemEntity,
        products = productEntityList
    )
}

fun GetOrderLineItemResponse.toBagLineItem(): OrderSummaryLineItem {

    val productsInBundle = HashMap(products.groupBy({ it.productGroupId }, { it.productId }))

    val modifierSelections = mutableMapOf<ProductIdModifierGroupIdKey, List<String>>()
    var modifiersDisplay = ""
    for (productResponse in products) {

        if (productResponse.modifierSelections.isEmpty() && bundleId != null) {
            modifiersDisplay += productResponse.productName + "\n"
        }

        for (modifierResponse in productResponse.modifierSelections) {
            val key = ProductIdModifierGroupIdKey(productResponse.productId, modifierResponse.modifierGroupId)
            val newList = modifierSelections.getOrElse(key) { listOf() }.toMutableList()
            newList.addAll(modifierResponse.modifiers.map { it.modifierId })
            modifierSelections[ProductIdModifierGroupIdKey(productResponse.productId, modifierResponse.modifierGroupId)] = newList


            if (modifierResponse.modifiers.isNotEmpty()) {
                modifiersDisplay += modifierResponse.modifiers.joinToString(",") { it.modifierName }.plus("\n")
            }
        }

    }

    return OrderSummaryLineItem(
        lineItemId = lineItemId,
        productId = baseProduct,
        bundleId = bundleId,
        lineItemName = lineItemName,
        price = price,
        productsInBundle = productsInBundle,
        modifiers = HashMap(modifierSelections),
        quantity = quantity,
        modifiersDisplay = modifiersDisplay.trim()
    )
}

fun LineItemEntityWithProducts.toDomain(): OrderSummaryLineItem {

    val productsInBundle = HashMap(products.groupBy({ it.product.productGroupId }, { it.product.productId }))

    val modifierSelections = mutableMapOf<ProductIdModifierGroupIdKey, List<String>>()
    var modifiersDisplay = ""

    this.products.forEach { productEntity ->

        if (productEntity.modifiers.isEmpty() && this.lineItem.bundleId != null) {
            modifiersDisplay += productEntity.product.productName + "\n"
        }

        productEntity.modifiers.forEach { modifierGroupEnity ->
            val key = ProductIdModifierGroupIdKey(productId = productEntity.product.productId, modifierGroupId = modifierGroupEnity.modifierGroup.modifierGroupId)
            modifierSelections[key] = modifierGroupEnity.modifierIds.map { it.modifierId }

            if (modifierGroupEnity.modifierIds.isNotEmpty()) {
                modifiersDisplay += modifierGroupEnity.modifierIds.joinToString(",") { it.modifierName }.plus("\n")
            }

        }
    }

    val lineItemEntity = this.lineItem
    return OrderSummaryLineItem(
        lineItemId = lineItemEntity.lineItemId,
        productId = lineItemEntity.productId,
        bundleId = lineItemEntity.bundleId,
        lineItemName = lineItemEntity.lineItemName,
        modifiersDisplay = modifiersDisplay.trim(),
        price = lineItemEntity.price,
        productsInBundle = productsInBundle,
        modifiers = HashMap(modifierSelections),
        quantity = lineItemEntity.quantity
    )
}

fun OrderSummaryLineItem.toDisplayModel(): BagItemRowDisplayModel {
    return BagItemRowDisplayModel(
        lineItemId = lineItemId,
        qty = quantity.toString(),
        itemName = lineItemName,
        itemModifier = modifiersDisplay,
        price = "$" + price.toTwoDigitString(),
        forRemoval = false
    )
}

fun ApiErrorResponse.toDomain(code: Int): AppError = AppError(apiErrorMessage = message, apiErrorCode = code)