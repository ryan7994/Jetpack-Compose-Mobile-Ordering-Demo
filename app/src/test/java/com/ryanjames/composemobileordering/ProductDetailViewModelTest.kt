package com.ryanjames.composemobileordering

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.features.productdetail.ModifierOptionDisplayModel
import com.ryanjames.composemobileordering.features.productdetail.ProductDetailViewModel
import com.ryanjames.composemobileordering.repository.MenuRepository
import com.ryanjames.composemobileordering.repository.OrderRepository
import com.ryanjames.composemobileordering.repository.VenueRepository
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ProductDetailViewModelTest {


    @Rule
    @JvmField
    val rule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var menuRepository: MenuRepository

    @Mock
    private lateinit var venueRepository: VenueRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()


    @Before
    @Throws(Exception::class)
    fun setUp() {
        runBlocking {
            `when`(venueRepository.getCurrentVenueId()).thenReturn("BUSGT")
        }

    }

    private fun createViewModel(productId: String = "B1000", lineItemId: String? = null): ProductDetailViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "productId" to productId,
                "lineItemId" to lineItemId
            )
        )
        return ProductDetailViewModel(savedStateHandle, menuRepository, orderRepository, venueRepository)
    }

    @Test
    fun `test button label - new item`() {
        val viewModel = createViewModel(lineItemId = null)
        assertEquals(viewModel.productDetailScreenState.value.btnLabel.id, R.string.add_to_bag)
    }

    @Test
    fun `test button label - modifying item`() {
        val viewModel = createViewModel(lineItemId = "asdsdsdsds")
        assertEquals(viewModel.productDetailScreenState.value.btnLabel.id, R.string.update_item)
    }


    @Test
    fun `test loading product details successfully`() {
        runBlocking {
            val product = PRODUCT_COKE

            `when`(menuRepository.getProductById("B1000")).thenReturn(flow {
                emit(Resource.Loading)
                delay(10)
                emit(Resource.Success(product))
            })
            `when`(orderRepository.getLineItems()).thenReturn(listOf())

            val viewModel = createViewModel()
            viewModel.productDetailScreenState.test {
                val emission1 = awaitItem()
                assertThat(emission1.loadingProductDetail).isEqualTo(true)
                assertThat(emission1.product).isEqualTo(null)

                coroutineRule.advanceTimeBy(10)
                val emission2 = awaitItem()
                assertThat(emission2.loadingProductDetail).isEqualTo(false)
                assertThat(emission2.product).isEqualTo(PRODUCT_COKE)
                assertThat(emission2.quantity).isEqualTo("1")
                assertThat(emission2.price).isEqualTo("$10.00")
                assertThat(emission2.modifierOptions).isEqualTo(listOf<ModifierOptionDisplayModel>())
            }

        }
    }


}
