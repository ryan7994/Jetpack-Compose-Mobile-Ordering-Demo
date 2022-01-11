package com.ryanjames.jetpackmobileordering.ui

import com.ryanjames.jetpackmobileordering.db.VenueCategoryEntity
import com.ryanjames.jetpackmobileordering.db.VenueEntity
import com.ryanjames.jetpackmobileordering.db.VenueEntityType
import com.ryanjames.jetpackmobileordering.db.VenueWithCategories
import com.ryanjames.jetpackmobileordering.db.model.BasicMenuWithCategories
import com.ryanjames.jetpackmobileordering.features.home.FeaturedRestaurantCardState
import com.ryanjames.jetpackmobileordering.features.home.RestaurantCardState
import com.ryanjames.jetpackmobileordering.features.venuedetail.CategoryViewState
import com.ryanjames.jetpackmobileordering.domain.*
import com.ryanjames.jetpackmobileordering.network.model.*
import com.ryanjames.jetpackmobileordering.toTwoDigitString
import com.ryanjames.jetpackmobileordering.ui.widget.MenuItemCardDisplayModel
import com.ryanjames.jetpackmobileordering.ui.widget.RestaurantDisplayModel

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
        featuredImage = featuredImage
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

fun Venue.toRestaurantHeaderState() = RestaurantDisplayModel(
    venueName = name,
    venueAddress = address ?: "",
    categories = categories,
    rating = rating.toTwoDigitString(),
    noOfReviews = "($numberOfRatings ratings)"
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
                    price = productEntity.price.toTwoDigitString(),
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
        price = "\$${price?.toTwoDigitString()}",
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
        type = type
    )
    val categoryEntities = categories?.map { VenueCategoryEntity(it) } ?: listOf()
    return Pair(venueEntity, categoryEntities)
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
        featuredImage = venueEntity.featuredImage
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
