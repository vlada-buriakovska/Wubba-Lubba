import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.vladabur.wubbalubba.presentation.ui.theme.LightPrimaryRed

@Composable
fun <T> PaginatedLazyColumn(
    listState: LazyListState,
    items: List<T>,
    itemKey: (T) -> Any,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: (Int) -> Unit,
    pageSize: Int,
    total: Int,
    content: @Composable (T) -> Unit,
    placeHolder: (@Composable () -> Unit)? = null
) {

    val reachedBottom: Boolean by remember { derivedStateOf { !listState.canScrollForward } }

    LaunchedEffect(reachedBottom) {
        if (items.isNotEmpty() && items.size < total && reachedBottom && !isLoadingMore) {
            val nextPage = (listState.layoutInfo.totalItemsCount / pageSize) + 1
            onLoadMore(nextPage)
        }
    }
    LazyColumn(
        state = listState
    ) {
        if (isLoading) {
            items(pageSize) {
                placeHolder?.invoke()
            }
        } else {
            items(items = items, key = { item: T -> itemKey(item) }) { item ->
                content(item)
            }
        }
        item {
            if (isLoadingMore) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = LightPrimaryRed
                    )
                }
            }
        }
    }
}