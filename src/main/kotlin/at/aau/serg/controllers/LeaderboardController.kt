package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import kotlin.math.max
import kotlin.math.min

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult> {

        // --- Lösung zu Aufgabe 2.2.1: Sortierung ---
        // -it.score sortiert absteigend (höchster Score zuerst)
        // it.timeInSeconds sortiert aufsteigend (kürzeste Zeit ist der Tiebreaker)
        val sortedLeaderboard = gameResultService.getGameResults()
            .sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))

        // --- Lösung zu Aufgabe 2.2.2: Zusätzliche Abfrage ---
        // Wenn kein rank übergeben wurde, das gesamte (sortierte) Leaderboard zurückgeben
        if (rank == null) {
            return sortedLeaderboard
        }

        // Wenn rank ungültig ist (zu groß oder <= 0), mit HTTP 400 (Bad Request) antworten
        if (rank <= 0 || rank > sortedLeaderboard.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungültiger Rank")
        }

        // Den Index in der Liste berechnen (Rank 1 ist Index 0)
        val targetIndex = rank - 1

        // Oberes und unteres Limit für die 3 Spieler davor und danach berechnen.
        // max() und min() verhindern, dass wir über den Rand der Liste hinauslesen (IndexOutOfBounds).
        val startIndex = max(0, targetIndex - 3)
        val endIndex = min(sortedLeaderboard.size - 1, targetIndex + 3)

        // Den entsprechenden Teil der Liste zurückgeben
        return sortedLeaderboard.slice(startIndex..endIndex)
    }
}