package com.ponthaitay.listmovie.kotlin.ui.movie

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.ponthaitay.listmovie.kotlin.JsonMockUtility
import com.ponthaitay.listmovie.kotlin.RxSchedulersOverrideRule
import com.ponthaitay.listmovie.kotlin.service.model.MovieDao
import com.ponthaitay.listmovie.kotlin.service.repository.MovieApi
import io.reactivex.Observable
import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockitoAnnotations
import retrofit2.Response

class MainViewModelTest {

    @Rule @JvmField val rxSchedulerRule = RxSchedulersOverrideRule()

    @Rule @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private var mockAPIs = mock<MovieApi> {}

    private var jsonUtil = JsonMockUtility()
    private var mainViewModel = MainViewModel(mockAPIs)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun requestMovieSuccess() {
        val mockResult = jsonUtil.getJsonToMock("movie_success.json", MovieDao::class.java)
        assertEquals(mockResult.result.size, 20)
        val mockResponse = Response.success(mockResult)
//        val mockResponse1 = Response.success(MovieDao(1,1,1, mutableListOf()))
        val mockObservable = Observable.just(mockResponse)
        whenever(mockAPIs.getMovie(anyString(), anyInt())).thenReturn(mockObservable)
        mainViewModel.getListMovie("")
        val testObserver = mockObservable.test()
        testObserver.awaitTerminalEvent()
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValueCount(1)
        testObserver.assertResult(mockResponse)
        testObserver.assertValue { response ->
            assertThat(response.body(), `is`(mockResponse.body()))
            true
        }
    }

    @Test
    fun requestMovieError() {
        val throwable = Throwable("error")
        val mockObservable = Observable.error<Response<MovieDao>>(throwable)
        whenever(mockAPIs.getMovie(anyString(), anyInt())).thenReturn(mockObservable)
        mainViewModel.getListMovie("sort_by")
        val testObserver = mockObservable.test()
        testObserver.awaitTerminalEvent()
        testObserver.assertSubscribed()
        testObserver.assertNotComplete()
        testObserver.assertError(throwable)
    }

    @Test
    fun requestMovieEmpty() {
        val mockResult = jsonUtil.getJsonToMock("movie_empty.json", MovieDao::class.java)
        assertEquals(mockResult.result.size, 0)
        val mockResponse = Response.success(mockResult)
        val mockObservable = Observable.just(mockResponse)
        whenever(mockAPIs.getMovie(anyString(), anyInt())).thenReturn(mockObservable)
        mainViewModel.getListMovie("sort_by")
        val testObserver = mockObservable.test()
        testObserver.awaitTerminalEvent()
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertResult(mockResponse)
    }

    @Test
    fun requestMovieInvalidAPI() {
        val mockResult = jsonUtil.getJsonToMock("movie_invalid_api.json", MovieDao::class.java)
        val mockResponse = Response.success(mockResult)
        val mockObservable = Observable.just(mockResponse)
        whenever(mockAPIs.getMovie(anyString(), anyInt())).thenReturn(mockObservable)
        mainViewModel.getListMovie("sort_by")
        val testObserver = mockObservable.test()
        testObserver.awaitTerminalEvent()
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertResult(mockResponse)
    }
}