package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.web.server.ResponseStatusException
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        // null übergeben, weil wir keinen bestimmten Rang (rank) wollen
        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_correctTimeSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        // NEU: Sortiert nach bester (kürzester) Zeit zuerst!
        assertEquals(second, res[0]) // 10.0 s
        assertEquals(third, res[1])  // 15.0 s
        assertEquals(first, res[2])  // 20.0 s
    }

    @Test
    fun test_getLeaderboard_withValidRank_returnsNeighbors() {
        // Wir erstellen 10 Fake-Spieler, absteigend sortiert (Score 100 bis 91)
        val players = (1..10).map { GameResult(it.toLong(), "player$it", 101 - it, 10.0) }
        whenever(mockedService.getGameResults()).thenReturn(players)

        // Wir fragen nach Platz 5 (Index 4) -> Wir erwarten Platz 2 bis 8 (7 Spieler)
        val res = controller.getLeaderboard(5)

        assertEquals(7, res.size)
        assertEquals("player2", res.first().playerName)
        assertEquals("player8", res.last().playerName)
    }

    @Test
    fun test_getLeaderboard_withInvalidRank_tooSmall_throws400() {
        whenever(mockedService.getGameResults()).thenReturn(listOf(GameResult(1, "p", 10, 10.0)))

        assertThrows<ResponseStatusException> {
            controller.getLeaderboard(0) // 0 ist ungültig
        }
        assertThrows<ResponseStatusException> {
            controller.getLeaderboard(-5) // negativ ist ungültig
        }
    }

    @Test
    fun test_getLeaderboard_withInvalidRank_tooLarge_throws400() {
        whenever(mockedService.getGameResults()).thenReturn(listOf(GameResult(1, "p", 10, 10.0)))

        assertThrows<ResponseStatusException> {
            controller.getLeaderboard(2) // Platz 2 gibt es nicht, da nur 1 Spieler existiert
        }
    }
}