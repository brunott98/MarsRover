package com.example.marsrover

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description


//Preparando  cenário para teste em escopo de coroutines
@OptIn(ExperimentalCoroutinesApi::class)
class MainCoroutineRule(

    //Agenda e controla o tempo da execução da coroutine de teste
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()




):TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }


}