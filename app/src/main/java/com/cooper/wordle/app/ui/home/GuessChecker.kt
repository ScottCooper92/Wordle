package com.cooper.wordle.app.ui.home

import timber.log.Timber

object GuessChecker {

    fun checkGuess(guess: WordState, answer: String): WordState {
        if (guess.letterCount != answer.length)
            throw IllegalArgumentException("Guess isn't the same length as answer. Answer: $answer, Guess: $guess")

        if (guess.tileStates.any { it !is TileState.Foo })
            throw IllegalStateException("Not a valid guess $guess")

        val tiles = guess.tileStates.toMutableList()
        val result = tiles.mapIndexed { index, tileState ->

            // check if this tile is correct
            val correctTile = isTileCorrect(tileState, answer[index])
            if (correctTile != null) return@mapIndexed correctTile

            // check for any occurrences of this char in the answer
            val fooTile = tileState as TileState.Foo
            var occurrence = answer.indexOf(fooTile.char, ignoreCase = true)
            while (occurrence >= 0) {

                // check if the tile at this index is correct as that takes priority
                val tileAtSameIndex = tiles[occurrence]
                val checkedTile = isTileCorrect(tileAtSameIndex, answer[occurrence])
                if (checkedTile != null) {
                    // since we've checked this tile we might as well add it to the result
                    tiles[occurrence] = checkedTile
                    // check for any other occurrences
                    occurrence = answer.indexOf(fooTile.char, occurrence + 1, true)
                } else {
                    // there isn't a better match so this can be considered present
                    return@mapIndexed TileState.Present(fooTile.char)
                }
            }

            // incorrect and not present
            return@mapIndexed TileState.Absent(fooTile.char)
        }

        return WordState(result)
    }

    private fun isTileCorrect(tile: TileState, char: Char): TileState? {
        // check if the tile has already been checked
        if (tile is TileState.Present || tile is TileState.Correct) {
            Timber.d("Tile $tile already checked")
            return tile
        }

        val fooTile = tile as TileState.Foo
        return if (fooTile.char.equals(char, true)) {
            // tile is in the correct location
            TileState.Correct(tile.char)
        } else null
    }
}