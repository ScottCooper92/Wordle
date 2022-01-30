package com.cooper.wordle.app.ui.game.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cooper.wordle.game.GridState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordGrid(
    gridState: GridState,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(gridState.length),
        userScrollEnabled = false,
        modifier = modifier
    ) {
        gridState.grid.forEach { row ->
            row.tileStates.forEach { tile ->
                item {
                    LetterTile(tileState = tile, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}