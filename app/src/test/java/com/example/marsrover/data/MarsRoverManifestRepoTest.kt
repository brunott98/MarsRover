package com.example.marsrover.data

import com.example.marsrover.MainCoroutineRule
import com.example.marsrover.domain.model.RoverManifestUiModel
import com.example.marsrover.domain.model.RoverManifestUiState
import com.example.marsrover.service.MarsRoverManifestService
import com.example.marsrover.service.model.ManifestPhotoRemoteModel
import com.example.marsrover.service.model.PhotoManifestRemoteModel
import com.example.marsrover.service.model.RoverManifestRemoteModel
import io.mockk.coEvery
import io.mockk.mockkClass
import junit.framework.Assert.assertEquals

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeoutException

class MarsRoverManifestRepoTest {

    private val marsRoverManifestService = mockkClass(MarsRoverManifestService::class)

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Test
    fun `should emit success when manifest service is successful`() =
        runTest(coroutineRule.testDispatcher) {

            //Given
            val roverManifestRemoteModel = RoverManifestRemoteModel(
                photoManifest = PhotoManifestRemoteModel(
                    landingDate = "2021-02-18",
                    launchDate = "2020-07-30",
                    maxDate = "2023-05-19",
                    maxSol = "799",
                    name = "Perseverance",
                    photos = listOf(
                        ManifestPhotoRemoteModel(
                            cameras = listOf(
                                "Camera1", "Camera2"
                            ),
                            earthDate = "2021-02-18",
                            sol = 0,
                            totalPhotos = 54
                        ),

                        ManifestPhotoRemoteModel(
                            cameras = listOf(
                                "Camera2", "Camera3"
                            ),
                            earthDate = "2021-02-19",
                            sol = 1,
                            totalPhotos = 201
                        ),

                        ),
                    status = "active",
                    totalPhotos = 156687
                )
            )

            coEvery {
                marsRoverManifestService.getMarsRoverManifest("perseverance")

            } returns roverManifestRemoteModel

            //when

            val marsRoverManifestRepo = MarsRoverManifestRepo(marsRoverManifestService)
            val result = marsRoverManifestRepo
                .getMarsRoverManifest("Perseverance").toList()

            //then

            val expectedResult = RoverManifestUiState.Success(
                listOf(
                    RoverManifestUiModel(
                        sol = "1",
                        earthDate = "2021-02-19",
                        photoNumber = "201"
                    ),

                    RoverManifestUiModel(
                        sol = "0",
                        earthDate = "2021-02-18",
                        photoNumber = "54"
                    ),
                )
            )


            assertEquals(1, result.size)
            assertEquals(expectedResult, result[0])

        }


    @Test
    fun `should emit error when manifest service throw timeout exception`() =
        runTest(coroutineRule.testDispatcher) {

            //Given
            coEvery {
                marsRoverManifestService.getMarsRoverManifest("perseverance")
            } throws TimeoutException()

            //When
            val marsRoverManifestRepo = MarsRoverManifestRepo(marsRoverManifestService)
            val result = marsRoverManifestRepo
                .getMarsRoverManifest("Perseverance").toList()


            //Then
            assertEquals(1, result.size)
            assertEquals(RoverManifestUiState.Error, result[0])

        }

}



