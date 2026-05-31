package com.example.momentary.ui.home

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.momentary.data.model.Story
import com.example.momentary.data.model.UserSession
import com.example.momentary.data.repository.MomentaryRepository
import com.example.momentary.ui.screen.home.HomeViewModel
import com.example.momentary.ui.screen.home.model.StoryUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: MomentaryRepository

    @Mock
    private lateinit var context: Context

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when Get Stories Success - Should Not Null, Match Count, and Match First Item`() =
        runTest {
            // Prepare data dummy
            val dummyStories = listOf(
                StoryUiModel(
                    id = "1",
                    story = generateDummyStory("1"),
                    isOwnStory = false,
                    isVerified = false,
                    createdAt = "2026-05-30"
                ),
                StoryUiModel(
                    id = "2",
                    story = generateDummyStory("2"),
                    isOwnStory = false,
                    isVerified = false,
                    createdAt = "2026-05-30"
                )
            )

            val pagingData = PagingData.from(dummyStories)
            val expectedStoriesFlow = flowOf(pagingData)

            val dummySession =
                UserSession(token = "token123", name = "Romy", email = "romy@dicoding.com")

            `when`(repository.getSession()).thenReturn(flowOf(dummySession))
            `when`(repository.getPagedStories(any())).thenReturn(expectedStoriesFlow)

            // ViewModel Initialization
            val homeViewModel = HomeViewModel(repository, context)

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryDiffCallback(),
                updateCallback = noopListUpdateCallback,
                workerDispatcher = testDispatcher
            )

            val job = launch {
                homeViewModel.stories.collect { pagingData ->
                    differ.submitData(pagingData)
                }
            }

            this.advanceUntilIdle()

            /** Assertion – Scenario 1 **/
            // Ensure the data is `not null`
            Assert.assertNotNull(differ.snapshot())
            // Ensure the data count matches the `expected value`
            Assert.assertEquals(dummyStories.size, differ.snapshot().size)
            // Ensure the `first returned data item` is correct
            Assert.assertEquals(dummyStories[0], differ.snapshot()[0])

            job.cancel() // Cancel job collect to avoid memory leak
        }

    @Test
    fun `when No Stories Data - Should Return Zero Items`() = runTest {
        // Prepare empty data
        val emptyStories = emptyList<StoryUiModel>()
        val pagingData = PagingData.from(emptyStories)
        val expectedStoriesFlow = flowOf(pagingData)

        val dummySession =
            UserSession(token = "token123", name = "Romy", email = "romy@dicoding.com")

        `when`(repository.getSession()).thenReturn(flowOf(dummySession))
        `when`(repository.getPagedStories(any())).thenReturn(expectedStoriesFlow)

        // ViewModel initialization
        val homeViewModel = HomeViewModel(repository, context)

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback(),
            updateCallback = noopListUpdateCallback,
            workerDispatcher = testDispatcher
        )

        val job = launch {
            homeViewModel.stories.collect { pagingData ->
                differ.submitData(pagingData)
            }
        }

        this.advanceUntilIdle()

        /** Assertion – Scenario 2 **/
        // Assert that the returned data count is `zero`
        Assert.assertEquals(0, differ.snapshot().size)

        job.cancel() // Cancel job collect to avoid memory leak
    }

    private fun generateDummyStory(id: String): Story {
        return Story(
            id = id,
            name = "Name $id",
            description = "Description $id",
            photoUrl = "https://story-api.dicoding.dev/images/stories/photo-$id.png",
            createdAt = "2026-05-30T10:00:00Z",
            lat = null,
            lon = null
        )
    }
}

class StoryDiffCallback : DiffUtil.ItemCallback<StoryUiModel>() {
    override fun areItemsTheSame(oldItem: StoryUiModel, newItem: StoryUiModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StoryUiModel, newItem: StoryUiModel): Boolean {
        return oldItem == newItem
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
