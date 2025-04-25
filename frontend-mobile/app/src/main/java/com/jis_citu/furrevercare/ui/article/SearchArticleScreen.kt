package com.jis_citu.furrevercare.ui.article

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.Background
import com.jis_citu.furrevercare.theme.FurrEverCareTheme

@Composable
fun SearchScreen(navController: NavController) {
    val articles = remember {
        listOf(
            Article(
                id = "1",
                title = "How to Stop Your Cat from Scratching Furniture",
                content = "My cat has been scratching furniture a lot lately. Any advice on how to stop this behavior?",
                author = "Dr. Sarah Johnson",
                authorAvatar = R.drawable.logo_icon_colored,
                timestamp = "2 days ago",
                category = "Behavior",
                imageRes = R.drawable.logo_icon_colored
            ),
            Article(
                id = "2",
                title = "Best Food for Senior Dogs",
                content = "As dogs age, their nutritional needs change. Learn about the best food options for your senior dog.",
                author = "Dr. Michael Lee",
                authorAvatar = R.drawable.logo_icon_colored,
                timestamp = "1 week ago",
                category = "Nutrition",
                imageRes = R.drawable.logo_icon_colored
            ),
            Article(
                id = "3",
                title = "Puppy Vaccination Schedule",
                content = "A comprehensive guide to puppy vaccinations: what they need and when they need them.",
                author = "Dr. Sarah Johnson",
                authorAvatar = R.drawable.logo_icon_colored,
                timestamp = "2 weeks ago",
                category = "Health",
                imageRes = R.drawable.logo_icon_colored
            )
        )
    }

    val recentSearches = remember {
        mutableStateListOf("cat scratching", "dog food", "puppy care", "pet grooming")
    }

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var showRecentSearches by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Text(
                text = "Search Articles",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    showRecentSearches = it.isNotEmpty() && !isSearching
                },
                placeholder = { Text("Search pet care articles...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            showRecentSearches = false
                            isSearching = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotEmpty()) {
                            if (!recentSearches.contains(searchQuery)) {
                                recentSearches.add(0, searchQuery)
                                if (recentSearches.size > 5) {
                                    recentSearches.removeAt(recentSearches.size - 1)
                                }
                            }
                            isSearching = true
                            showRecentSearches = false
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp)
            )

            // Recent searches dropdown
            if (showRecentSearches) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        recentSearches.forEach { search ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        searchQuery = search
                                        isSearching = true
                                        showRecentSearches = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = search,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Article list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val filteredArticles = if (isSearching && searchQuery.isNotEmpty()) {
                    articles.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                                it.content.contains(searchQuery, ignoreCase = true) ||
                                it.category.contains(searchQuery, ignoreCase = true)
                    }
                } else {
                    articles
                }

                if (isSearching && filteredArticles.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No articles found for \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    items(filteredArticles) { article ->
                        ArticleItem(article) {
                            navController.navigate("${Routes.ARTICLE_DETAIL}/${article.id}")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleItem(article: Article, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = article.authorAvatar),
                    contentDescription = "Author Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = article.author,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = article.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    FurrEverCareTheme {
        SearchScreen(rememberNavController())
    }
}
