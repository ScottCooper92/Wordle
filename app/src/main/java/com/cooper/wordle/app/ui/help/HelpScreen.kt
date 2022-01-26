package com.cooper.wordle.app.ui.help

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cooper.wordle.app.R
import com.cooper.wordle.app.data.TileState
import com.cooper.wordle.app.ui.components.BottomSheet
import com.cooper.wordle.app.ui.components.WordRow
import com.cooper.wordle.app.ui.theme.WordleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen() {

    val correctRow = remember {
        com.cooper.wordle.app.data.Row(
            listOf(
                TileState.Correct('W'),
                TileState.Foo('E'),
                TileState.Foo('A'),
                TileState.Foo('R'),
                TileState.Foo('Y'),
            )
        )
    }

    val presentRow = remember {
        com.cooper.wordle.app.data.Row(
            listOf(
                TileState.Foo('P'),
                TileState.Present('I'),
                TileState.Foo('L'),
                TileState.Foo('L'),
                TileState.Foo('S'),
            )
        )
    }

    val absentRow = remember {
        com.cooper.wordle.app.data.Row(
            listOf(
                TileState.Foo('V'),
                TileState.Foo('A'),
                TileState.Foo('G'),
                TileState.Absent('U'),
                TileState.Foo('E'),
            )
        )
    }


    BottomSheet(title = stringResource(id = R.string.how_to_play)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 34.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.instructions),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.examples),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // correct row
            WordRow(correctRow)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(id = R.string.correct_example))
            Spacer(modifier = Modifier.height(24.dp))

            // present row
            WordRow(presentRow)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(id = R.string.present_example))
            Spacer(modifier = Modifier.height(24.dp))

            // incorrect row
            WordRow(absentRow)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(id = R.string.incorrect_example))
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
private fun HelpScreenPreview() {
    WordleTheme {
        HelpScreen()
    }
}