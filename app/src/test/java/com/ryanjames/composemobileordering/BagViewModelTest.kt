@file:OptIn(FlowPreview::class)

package com.ryanjames.composemobileordering

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.ryanjames.composemobileordering.domain.OrderSummary
import com.ryanjames.composemobileordering.features.bag.BagItemRowDisplayModel
import com.ryanjames.composemobileordering.features.bag.BagScreenState
import com.ryanjames.composemobileordering.features.bag.BagViewModel
import com.ryanjames.composemobileordering.features.bag.ButtonState
import com.ryanjames.composemobileordering.mocks.MockVenueRepository
import com.ryanjames.composemobileordering.repository.MenuRepository
import com.ryanjames.composemobileordering.repository.OrderRepository
import com.ryanjames.composemobileordering.repository.VenueRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class BagViewModelTest {

    @Rule
    @JvmField
    val rule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()


    @Mock
    private lateinit var orderRepository: OrderRepository

    private val venueRepository = MockVenueRepository()

    @Before
    @Throws(Exception::class)
    fun setUp() {

    }

    @Test
    fun `test empty bag`() {
        Mockito.`when`(orderRepository.getBagSummaryFlow()).thenReturn(flow { emit(OrderSummary.EMPTY) })
        val bagViewModel = BagViewModel(orderRepository, venueRepository)

        runBlocking {
            bagViewModel.bagScreenState.test {
                val emission1 = awaitItem()
                assertThat(emission1.bagItems).isEqualTo(listOf<BagItemRowDisplayModel>())
            }
        }

    }
}