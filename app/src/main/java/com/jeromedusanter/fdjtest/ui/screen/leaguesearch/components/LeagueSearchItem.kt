package com.jeromedusanter.fdjtest.ui.screen.leaguesearch.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LeagueSearchItem(
    leagueName: String,
    sport: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = leagueName,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = sport,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
private fun LeagueSearchItemPreview() {
    LeagueSearchItem(
        leagueName = "French Ligue 1",
        sport = "Soccer",
        onClick = {}
    )
}
