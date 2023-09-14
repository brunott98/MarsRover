package com.example.marsrover.ui.photolsit

import com.example.marsrover.MainCoroutineRule
import com.example.marsrover.data.MarsRoverPhotoRepo
import com.example.marsrover.domain.model.RoverPhotoUiModel
import com.example.marsrover.domain.model.RoverPhotoUiState
import com.example.marsrover.ui.photolist.MarsRoverPhotoViewModel
import io.mockk.coEvery

import io.mockk.mockkClass
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MarsRoverPhotoViewModelTest {

    private val marsRoverPhotoRepo = mockkClass(MarsRoverPhotoRepo::class)

    @get:Rule
    val coroutineRule = MainCoroutineRule()


    @Test
    fun `should emit success when repository emit success`() = runTest(
        coroutineRule
            .testDispatcher
    ) {

        //Given

        val expectedResult = RoverPhotoUiState.Success(

            roverPhotoUiModelList = listOf(
                RoverPhotoUiModel(

                    id = 1,
                    roverName = "perseverance",
                    imgSrc = "https://example.com/photo1",
                    sol = "0",
                    earthDate = "2022-03-10",
                    cameraFullName = "camera one",
                    isSaved = true

                ),

                RoverPhotoUiModel(

                    id = 2,
                    roverName = "perseverance",
                    imgSrc = "https://example.com/photo2",
                    sol = "0",
                    earthDate = "2022-03-10",
                    cameraFullName = "camera two",
                    isSaved = false

                )

            )


        )
        coEvery {
            marsRoverPhotoRepo.getMarsRoverPhoto("perseverance", "0")
        } returns flowOf(expectedResult)

        //When

        val marsRoverPhotoViewModel = MarsRoverPhotoViewModel(
            marsRoverPhotoRepo,
            coroutineRule.testDispatcher
        )

        marsRoverPhotoViewModel.getMarsRoverPhoto("perseverance", "0")

        val result = marsRoverPhotoViewModel.roverPhotoUiState.first()

        //Then

        assertEquals(expectedResult, result)


    }


    @Test
    fun `should emit error when repository emit error`() = runTest(
        coroutineRule
            .testDispatcher
    ) {

        //Given

        coEvery {
            marsRoverPhotoRepo.getMarsRoverPhoto("perseverance", "0")
        } returns flowOf(RoverPhotoUiState.Error)

        //When

        val marsRoverPhotoViewModel = MarsRoverPhotoViewModel(
            marsRoverPhotoRepo,
            coroutineRule.testDispatcher
        )

        marsRoverPhotoViewModel.getMarsRoverPhoto("perseverance", "0")

        val result = marsRoverPhotoViewModel.roverPhotoUiState.first()

        //Then

        assertEquals(RoverPhotoUiState.Error, result)


    }

    @Test
    fun `should remove photo when photo is saved and change save status is called`() = runTest(
        coroutineRule
            .testDispatcher
    ) {

        //Given

        val roverPhotoUiModel = RoverPhotoUiModel(
            id = 1,
            roverName = "perseverance",
            imgSrc = "http://example.com/photo1",
            sol = "0",
            earthDate = "2022-03-10",
            cameraFullName = "camera one",
            isSaved = true
        )

        coEvery {
            marsRoverPhotoRepo.removePhoto(roverPhotoUiModel)
        } returns Unit

        //When

        val marsRoverPhotoViewModel = MarsRoverPhotoViewModel(
            marsRoverPhotoRepo,
            coroutineRule.testDispatcher
        )

        marsRoverPhotoViewModel.changeSaveStatus(roverPhotoUiModel)


        //Then

        coEvery { marsRoverPhotoRepo.removePhoto(roverPhotoUiModel) }


    }


    @Test
    fun `should save photo when photo is not saved and change save status is called`() = runTest(
        coroutineRule
            .testDispatcher
    ) {

        //Given

        val roverPhotoUiModel = RoverPhotoUiModel(
            id = 1,
            roverName = "perseverance",
            imgSrc = "http://example.com/photo1",
            sol = "0",
            earthDate = "2022-03-10",
            cameraFullName = "camera one",
            isSaved = false
        )

        coEvery {
            marsRoverPhotoRepo.savePhoto(roverPhotoUiModel)
        } returns Unit

        //When

        val marsRoverPhotoViewModel = MarsRoverPhotoViewModel(
            marsRoverPhotoRepo,
            coroutineRule.testDispatcher
        )

        marsRoverPhotoViewModel.changeSaveStatus(roverPhotoUiModel)


        //Then

        coEvery { marsRoverPhotoRepo.savePhoto(roverPhotoUiModel) }


    }

}