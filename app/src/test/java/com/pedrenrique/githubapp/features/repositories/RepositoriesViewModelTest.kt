package com.pedrenrique.githubapp.features.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.pedrenrique.githubapp.core.data.PaginatedData
import com.pedrenrique.githubapp.core.data.Repository
import com.pedrenrique.githubapp.core.data.User
import com.pedrenrique.githubapp.core.domain.LoadRepositories
import com.pedrenrique.githubapp.core.exceptions.EmptyResultException
import com.pedrenrique.githubapp.core.exceptions.NoMoreResultException
import com.pedrenrique.githubapp.features.common.DataState
import com.pedrenrique.githubapp.features.common.adapter.model.RepositoryModelHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RepositoriesViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: RepositoriesViewModel

    @Mock
    private lateinit var listRepositories: ListRepositories
    @Mock
    private lateinit var loadMoreRepositories: LoadRepositories
    @Mock
    private lateinit var stateObserver: Observer<DataState>
    @Mock
    private lateinit var pageObserver: Observer<Int>

    @Before
    fun setUp() {
        viewModel = RepositoriesViewModel(listRepositories, loadMoreRepositories)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `should list repositories`() {
        runBlocking {
            // Arrange
            val content = listOf(
                Repository(
                    "repository", "user/repository",
                    "description", 1, 1, User("", "")
                )
            )
            val data = content.map { RepositoryModelHolder(it) }

            `when`(listRepositories.invoke())
                .thenReturn(PaginatedData(1, content))

            viewModel.state.observeForever(stateObserver)
            viewModel.page.observeForever(pageObserver)

            // Act
            viewModel.load()

            // Assert
            verify(pageObserver).onChanged(0)
            verify(pageObserver).onChanged(1)

            verify(stateObserver).onChanged(DataState.Pending)
            verify(stateObserver).onChanged(DataState.Loaded(data))

            verify(listRepositories).invoke()

            verifyNoMoreInteractions(stateObserver, pageObserver, listRepositories)
            verifyZeroInteractions(loadMoreRepositories)
        }
    }

    @Test
    fun `should list repositories once`() {
        // Arrange
        val content = listOf(
            Repository(
                "repository", "user/repository",
                "description", 1, 1, User("", "")
            )
        )
        val data = content.map { RepositoryModelHolder(it) }

        viewModel.state.value = DataState.Loaded(data)
        viewModel.page.value = 1

        viewModel.state.observeForever(stateObserver)
        viewModel.page.observeForever(pageObserver)

        // Act
        viewModel.load()

        // Assert
        verify(pageObserver).onChanged(1)

        verify(stateObserver).onChanged(DataState.Loaded(data))

        verifyNoMoreInteractions(stateObserver, pageObserver)
        verifyZeroInteractions(listRepositories, loadMoreRepositories)
    }

    @Test
    fun `should load more repositories`() {
        runBlocking {
            // Arrange
            val repository1 = RepositoryModelHolder(
                Repository(
                    "repository1", "user1/repository1",
                    "description1", 1, 1, User("1", "1")
                )
            )
            val repository2 = RepositoryModelHolder(
                Repository(
                    "repository2", "user2/repository2",
                    "repository2", 2, 2, User("2", "2")
                )
            )

            `when`(loadMoreRepositories.invoke(LoadRepositories.Params(1)))
                .thenReturn(PaginatedData(2, listOf(repository2.repo)))

            viewModel.state.value = DataState.Loaded(listOf(repository1))
            viewModel.page.value = 1

            viewModel.state.observeForever(stateObserver)
            viewModel.page.observeForever(pageObserver)

            // Act
            viewModel.loadMore()

            // Assert
            verify(loadMoreRepositories).invoke(LoadRepositories.Params(1))

            verify(pageObserver).onChanged(1)
            verify(pageObserver).onChanged(2)

            verify(stateObserver).onChanged(DataState.Loaded(listOf(repository1)))

            verify(stateObserver).onChanged(DataState.NextPending(listOf(repository1)))
            verify(stateObserver).onChanged(DataState.Loaded(listOf(repository1, repository2)))

            verifyNoMoreInteractions(
                stateObserver,
                pageObserver,
                loadMoreRepositories
            )
            verifyZeroInteractions(listRepositories)
        }
    }

    @Test
    fun `should fail when try to load repositories`() {
        runBlocking {
            // Arrange
            val exception = Exception("error!")
            `when`(listRepositories.invoke())
                .thenThrow(exception)

            viewModel.state.observeForever(stateObserver)
            viewModel.page.observeForever(pageObserver)

            // Act
            viewModel.load()

            // Assert
            verify(listRepositories).invoke()

            verify(stateObserver).onChanged(DataState.Pending)
            verify(stateObserver).onChanged(DataState.Failed(exception))

            verify(pageObserver).onChanged(0)

            verifyNoMoreInteractions(
                stateObserver,
                pageObserver,
                listRepositories,
                loadMoreRepositories
            )
        }
    }

    @Test
    fun `should fail when try to load more repositories`() {
        runBlocking {
            // Arrange
            val repository1 = RepositoryModelHolder(
                Repository(
                    "repository1", "user1/repository1",
                    "description1", 1, 1, User("1", "1")
                )
            )

            val exception = Exception("error!")
            `when`(loadMoreRepositories.invoke(LoadRepositories.Params(1)))
                .thenThrow(exception)

            viewModel.state.value = DataState.Loaded(listOf(repository1))
            viewModel.page.value = 1

            viewModel.state.observeForever(stateObserver)
            viewModel.page.observeForever(pageObserver)

            // Act
            viewModel.loadMore()

            // Assert
            verify(loadMoreRepositories).invoke(LoadRepositories.Params(1))

            verify(pageObserver).onChanged(1)

            verify(stateObserver).onChanged(DataState.Loaded(listOf(repository1)))
            verify(stateObserver).onChanged(DataState.NextPending(listOf(repository1)))
            verify(stateObserver).onChanged(DataState.NextFailed(exception, listOf(repository1)))

            verifyNoMoreInteractions(
                stateObserver,
                pageObserver,
                loadMoreRepositories
            )
            verifyZeroInteractions(listRepositories)
        }
    }

    @Test
    fun `should return empty repositories`() {
        runBlocking {
            // Arrange
            val exception = EmptyResultException()
            `when`(listRepositories.invoke())
                .thenThrow(exception)

            viewModel.state.observeForever(stateObserver)
            viewModel.page.observeForever(pageObserver)

            // Act
            viewModel.load()

            // Assert
            verify(listRepositories).invoke()

            verify(pageObserver).onChanged(0)

            verify(stateObserver).onChanged(DataState.Pending)
            verify(stateObserver).onChanged(DataState.Empty)

            verifyNoMoreInteractions(stateObserver, pageObserver, listRepositories)
            verifyZeroInteractions(loadMoreRepositories)
        }
    }

    @Test
    fun `should reach the end of repository list`() {
        runBlocking {
            // Arrange
            val repository1 = RepositoryModelHolder(
                Repository(
                    "repository1", "user1/repository1",
                    "description1", 1, 1, User("1", "1")
                )
            )
            val repository2 = RepositoryModelHolder(
                Repository(
                    "repository2", "user2/repository2",
                    "repository2", 2, 2, User("2", "2")
                )
            )

            val exception = NoMoreResultException()
            `when`(loadMoreRepositories.invoke(LoadRepositories.Params(2)))
                .thenThrow(exception)

            viewModel.state.value =

            viewModel.state.observeForever(stateObserver)
            viewModel.page.observeForever(pageObserver)

            // Act
            viewModel.loadMore()

            // Assert
            verify(loadMoreRepositories).invoke(LoadRepositories.Params(1))

            verify(pageObserver).onChanged(1)

            verify(stateObserver).onChanged(DataState.Loaded(listOf(repository1, repository2)))

            verify(stateObserver).onChanged(DataState.NextPending(listOf(repository1, repository2)))
            verify(stateObserver).onChanged(DataState.Completed(listOf(repository1, repository2)))

            verifyNoMoreInteractions(
                stateObserver,
                pageObserver,
                listRepositories,
                loadMoreRepositories
            )
        }
    }
}