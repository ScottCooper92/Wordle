package com.cooper.wordle.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.cooper.wordle.app.R

@Composable
fun WordleAppBar(onActionClicked: (AppBarAction) -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.wordle).uppercase())
        },
        actions = {
            IconButton(onClick = { onActionClicked(AppBarAction.HELP) }) {
                Icon(imageVector = Icons.Outlined.Help, contentDescription = "")
            }
        }
    )
}

enum class AppBarAction {
    HELP
}