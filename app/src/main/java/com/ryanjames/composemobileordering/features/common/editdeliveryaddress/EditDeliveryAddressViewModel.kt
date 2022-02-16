package com.ryanjames.composemobileordering.features.common.editdeliveryaddress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.composemobileordering.repository.AbsOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDeliveryAddressViewModel @Inject constructor(private val orderRepository: AbsOrderRepository) : ViewModel() {

    private val _deliveryAddressState = MutableStateFlow(DeliveryAddressState(""))
    val deliveryAddressState = _deliveryAddressState.asStateFlow()

    init {
        viewModelScope.launch {
            val initialAddress = orderRepository.getDeliveryAddressFlow().first() ?: ""
            _deliveryAddressState.value = _deliveryAddressState.value.copy(deliveryAddressInput = initialAddress)
        }
    }

    fun onDeliveryAddressInputChange(newValue: String) {
        _deliveryAddressState.value = _deliveryAddressState.value.copy(deliveryAddressInput = newValue)
    }

    fun updateDeliveryAddress() {
        viewModelScope.launch {
            orderRepository.updateDeliveryAddress(_deliveryAddressState.value.deliveryAddressInput)
        }
    }
}