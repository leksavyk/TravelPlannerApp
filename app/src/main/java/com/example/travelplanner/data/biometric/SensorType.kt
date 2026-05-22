package com.example.travelplanner.data.biometric

/**
 * Types of scanners available on the device
**/
enum class SensorType {
    TOUCH_ID,
    FACE_ID,
    COMBINED,
    UNSUPPORTED;

    fun getReadableName(): String = when (this) {
        TOUCH_ID -> "Відбиток пальця"
        FACE_ID -> "Розпізнавання обличчя"
        COMBINED -> "Комбінована біометрія"
        UNSUPPORTED -> "Біометричний захист недоступний"
    }
}