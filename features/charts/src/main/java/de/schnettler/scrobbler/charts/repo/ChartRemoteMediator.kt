package de.schnettler.scrobbler.charts.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import de.schnettler.scrobbler.core.map.IndexedMapper
import de.schnettler.scrobbler.core.map.forPagedLists
import de.schnettler.scrobbler.model.Toplist
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ChartRemoteMediator<Input, Output : Toplist>(
    private val pageSize: Int = 50,
    private val mapper: IndexedMapper<Input, Output>,
    private val clear: suspend () -> Unit,
    private val insert: suspend (List<Output>) -> Unit,
    private val apiCall: suspend (Int, Int) -> List<Input>,
) : RemoteMediator<Int, Output>() {

    @Suppress("ReturnCount")
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Output>
    ): MediatorResult {
        return try {
            // 1. Decide which page should be loaded
            val page = when (loadType) {
                LoadType.REFRESH -> 1 // First Page
                LoadType.PREPEND -> {
                    // No Prepend, because only first page is refreshed
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    lastItem?.listing?.index?.div(pageSize)?.plus(2) ?: 1
                }
            }

            // 2. Load page from network
            val response = apiCall(page, pageSize)

            // 3. Remove items from previous pages
            val normalizedResponse = response.takeLast(pageSize)

            // 4. Map response
            val baseIndex = (page - 1) * pageSize
            val mappedResponse = mapper.forPagedLists(baseIndex)(normalizedResponse)

            // 5. Store mapped response in database
            if (loadType == LoadType.REFRESH) {
                clear.invoke()
            }
            insert.invoke(mappedResponse)

            Timber.d("$loadType, Page $page, BaseIndex: $baseIndex")

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}