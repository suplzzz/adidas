package com.suplz.adidas.presentation.screen.timeline

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.suplz.adidas.domain.entity.TimelineItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimelineScreen(
    modifier: Modifier = Modifier,
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Отображение BottomSheet, если есть выбранный элемент
    uiState.selectedItem?.let { item ->
        TimelineDetailsSheet(
            item = item,
            onDismiss = viewModel::onSheetDismissed
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(), // Заполняем весь экран
        topBar = {
            TimelineTopAppBar(
                // Маленький индикатор в AppBar останется для обратной связи
                isLoading = uiState.isLoading,
                onRefresh = viewModel::loadTimeline
            )
        }
    ) { paddingValues ->
        TimelineContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onItemClicked = viewModel::onItemClick
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimelineTopAppBar(
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    TopAppBar(
        title = { Text("Умный Таймлайн 2ГИС") },
        actions = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Обновить"
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimelineContent(
    modifier: Modifier = Modifier,
    uiState: TimelineUiState,
    onItemClicked: (TimelineItem) -> Unit
) {
    // Surface остается, как и был
    Surface(modifier = modifier.fillMaxSize()) {
        val tabTitles = remember { listOf("Сегодня", "Завтра") }
        val pagerState = rememberPagerState { tabTitles.size }
        val coroutineScope = rememberCoroutineScope()

        // 1. Column, TabRow и Pager теперь находятся ВНЕ каких-либо условий.
        // Они будут отображены сразу.
        Column(modifier = Modifier.fillMaxSize()) {
            SecondaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(pagerState.currentPage)
                            .fillMaxHeight()
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                },
                divider = {}
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        selectedContentColor = MaterialTheme.colorScheme.onSurface,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = { Text(text = title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                // 2. Теперь логика `when` находится ВНУТРИ пейджера и решает,
                // что показывать на каждой странице.
                when {
                    // Ошибка имеет приоритет
                    uiState.error != null -> {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(text = uiState.error)
                        }
                    }

                    // Данные успешно загружены
                    uiState.timeline != null -> {
                        val items = if (page == 0) uiState.timeline.today else uiState.timeline.tomorrow
                        if (items.isEmpty()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text("На этот день событий нет")
                            }
                        } else {
                            // Вот ваш TimelineEventsList, он на месте
                            TimelineEventsList(
                                items = items,
                                onItemClicked = onItemClicked
                            )
                        }
                    }

                    // 3. Начальное состояние (загрузка). Ничего не показываем в контенте,
                    // так как индикатор есть в TopBar. Pager будет просто пустым.
                    else -> {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}


@Composable
fun TimelineEventsList(
    items: List<TimelineItem>,
    onItemClicked: (TimelineItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            TimelineItemCard(
                item = item,
                onClick = { onItemClicked(item) }
            )
        }
    }
}