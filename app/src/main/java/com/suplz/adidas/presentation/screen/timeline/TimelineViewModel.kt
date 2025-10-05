package com.suplz.adidas.presentation.screen.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suplz.adidas.domain.entity.SmartTimeline
import com.suplz.adidas.domain.entity.TimelineItem
import com.suplz.adidas.domain.usecase.GetSmartTimelineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimelineUiState(
    val isLoading: Boolean = false,
    val timeline: SmartTimeline? = null,
    val error: String? = null,
    // ДОБАВЛЕНО: Поле для отслеживания выбранного элемента для BottomSheet
    val selectedItem: TimelineItem? = null
)

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val getSmartTimelineUseCase: GetSmartTimelineUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    init {
        loadTimeline()
    }

    fun loadTimeline() {
        viewModelScope.launch {



            _uiState.update { it.copy(isLoading = true, error = null) }

            delay(1000L)

            try {
                val timelineData = getSmartTimelineUseCase.invoke()
                _uiState.update {
                    it.copy(isLoading = false, timeline = timelineData)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Не удалось загрузить данные. Проверьте подключение.")
                }
            }
        }
    }

    // ДОБАВЛЕНО: Обработчик клика на карточку
    fun onItemClick(item: TimelineItem) {
        _uiState.update { it.copy(selectedItem = item) }
    }

    // ДОБАВЛЕНО: Обработчик закрытия BottomSheet
    fun onSheetDismissed() {
        _uiState.update { it.copy(selectedItem = null) }
    }
}