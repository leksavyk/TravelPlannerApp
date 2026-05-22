package com.example.travelplanner.data.websocket

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class SocketManager (
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    private val messageIntervalRange: LongRange = 10_000L..20_000L
) : ISocketManager {
    private val _state = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val state: StateFlow<ConnectionState> = _state.asStateFlow()

    private val _messages = MutableSharedFlow<String>()
    override val messages: SharedFlow<String> = _messages.asSharedFlow()

    private var activeJob: Job? = null      // Керує процесом підключення/retry
    private var simulationJob: Job? = null  // Керує генерацією сповіщень

    override fun connect(url: String) {
        val current = _state.value
        if (current == ConnectionState.Connected || current == ConnectionState.Connecting) return

        _state.value = ConnectionState.Connecting

        activeJob?.cancel()
        activeJob = scope.launch {
            delay(1500) // Simulates network latency
            _state.value = ConnectionState.Connected
            startNotificationSimulation()
        }
    }

    override fun disconnect() {
        activeJob?.cancel()
        activeJob = null
        simulationJob?.cancel()
        simulationJob = null
        _state.value = ConnectionState.Disconnected
    }

    override fun send(message: String) {
        if (_state.value != ConnectionState.Connected) return

        // Simulate the server's response to the data submission
        scope.launch {
            val echoJson = """
                {
                    "type": "${NotificationType.SYSTEM}",
                    "title": "Система",
                    "message": "Ми отримали ваш запит: $message",
                    "timestamp": ${System.currentTimeMillis()}
                }
            """.trimIndent()
            _messages.emit(echoJson)
        }
    }

    /**
     * Simulated connection loss with automatic recovery
     * */
    fun simulateNetworkLoss() {
        if (_state.value != ConnectionState.Connected) return

        simulationJob?.cancel()
        simulationJob = null
        _state.value = ConnectionState.Reconnecting

        activeJob?.cancel()
        activeJob = scope.launch {
            delay(5000)
            _state.value = ConnectionState.Connecting
            delay(1000)
            _state.value = ConnectionState.Connected
            startNotificationSimulation()
        }
    }

    private fun startNotificationSimulation() {
        simulationJob?.cancel()
        simulationJob = scope.launch {
            var count = 0
            while (isActive && _state.value == ConnectionState.Connected && count < 20) {
                val interval = Random.nextLong(messageIntervalRange.first, messageIntervalRange.last + 1)
                delay(interval)

                if (_state.value == ConnectionState.Connected) {
                    val notificationJson = generateRandomNotificationJson()
                    _messages.emit(notificationJson)
                    count++
                }
            }
        }
    }

    private fun generateRandomNotificationJson(): String {
        val type = NotificationType.entries.random()
        val tripId = (100..999).random()

        val (title, message) = when (type) {
            NotificationType.TRIP_START_REMINDER ->
                "Час в дорогу!" to "Ваша подорож починається за 3 дні. Перевірте список речей"
            NotificationType.TRIP_COMPLETED ->
                "Вітаємо!" to "Ви відвідали всі локації у вашій подорожі"
            NotificationType.INACTIVITY_REMINDER ->
                "Куди далі?" to "Ви давно не планували нових пригод. Подивіться цікаві місця поруч!"
            NotificationType.SYSTEM ->
                "Оновлення" to "Дані успішно синхронізовано з хмарою"
        }

        return """
            {
                "type": "$type",
                "title": "$title",
                "message": "$message",
                "timestamp": ${System.currentTimeMillis()},
                "tripId": "$tripId"
            }
        """.trimIndent()
    }
}
