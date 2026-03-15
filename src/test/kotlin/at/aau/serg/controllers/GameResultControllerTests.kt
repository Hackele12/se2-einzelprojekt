package at.aau.serg.controllers
import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever

class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        // Wir "mocken" (simulieren) den Service, da wir nur den Controller testen wollen
        mockedService = mock<GameResultService>()
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getGameResult_callsServiceAndReturnsResult() {
        val expectedResult = GameResult(1, "TestPlayer", 150, 45.5)
        whenever(mockedService.getGameResult(1L)).thenReturn(expectedResult)

        val actualResult = controller.getGameResult(1L)

        verify(mockedService).getGameResult(1L)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun test_getAllGameResults_callsServiceAndReturnsList() {
        val expectedList = listOf(GameResult(1, "TestPlayer", 150, 45.5))
        whenever(mockedService.getGameResults()).thenReturn(expectedList)

        val actualList = controller.getAllGameResults()

        verify(mockedService).getGameResults()
        assertEquals(1, actualList.size)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun test_addGameResult_passesObjectToService() {
        val newResult = GameResult(0, "NewPlayer", 100, 30.0)

        controller.addGameResult(newResult)

        // Wir prüfen, ob der Controller die Methode des Services korrekt aufgerufen hat
        verify(mockedService).addGameResult(newResult)
    }

    @Test
    fun test_deleteGameResult_passesIdToService() {
        controller.deleteGameResult(5L)

        // Wir prüfen, ob der Controller die Lösch-Anweisung mit der richtigen ID weitergibt
        verify(mockedService).deleteGameResult(5L)
    }
}