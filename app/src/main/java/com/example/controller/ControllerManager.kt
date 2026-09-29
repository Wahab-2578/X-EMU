package com.example.controller

import android.content.Context
import android.hardware.input.InputManager
import android.view.InputDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ControllerInfo(
    val id: Int,
    val name: String,
    val isConnected: Boolean,
    val isGamepad: Boolean,
    val vendorId: Int = 0,
    val productId: Int = 0
)

class ControllerManager(private val context: Context) : InputManager.InputDeviceListener {

    private val inputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager

    private val _connectedControllers = MutableStateFlow<List<ControllerInfo>>(emptyList())
    val connectedControllers: StateFlow<List<ControllerInfo>> = _connectedControllers.asStateFlow()

    private val _isControllerConnected = MutableStateFlow(false)
    val isControllerConnected: StateFlow<Boolean> = _isControllerConnected.asStateFlow()

    init {
        inputManager.registerInputDeviceListener(this, null)
        refreshControllers()
    }

    fun refreshControllers() {
        val deviceIds = InputDevice.getDeviceIds()
        val controllers = mutableListOf<ControllerInfo>()

        for (id in deviceIds) {
            val device = InputDevice.getDevice(id) ?: continue
            val sources = device.sources

            // Check if device is a gamepad or joystick
            val isGamepad = (sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) ||
                    (sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK)

            if (isGamepad) {
                controllers.add(
                    ControllerInfo(
                        id = id,
                        name = device.name ?: "Generic Gamepad",
                        isConnected = true,
                        isGamepad = true,
                        vendorId = device.vendorId,
                        productId = device.productId
                    )
                )
            }
        }

        _connectedControllers.value = controllers
        _isControllerConnected.value = controllers.isNotEmpty()
    }

    override fun onInputDeviceAdded(deviceId: Int) {
        refreshControllers()
    }

    override fun onInputDeviceRemoved(deviceId: Int) {
        refreshControllers()
    }

    override fun onInputDeviceChanged(deviceId: Int) {
        refreshControllers()
    }

    fun unregister() {
        inputManager.unregisterInputDeviceListener(this)
    }
}
