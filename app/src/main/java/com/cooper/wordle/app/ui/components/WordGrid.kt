package com.cooper.wordle.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cooper.wordle.app.ui.home.WordState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordGrid(
    wordLength: Int,
    wordStates: List<WordState>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(wordLength),
        modifier = modifier
    ) {
        wordStates.forEach { wordState ->
            wordState.tileStates.forEach { tileState ->
                item {
                    LetterTile(tileState = tileState, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}