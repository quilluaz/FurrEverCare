package com.jis_citu.furrevercare.ui.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.theme.Background
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen

data class Article(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val authorAvatar: Int,
    val timestamp: String,
    val category: String,
    val imageRes: Int = R.drawable.dog
)

@Composable
fun ArticleScreen(navController: NavController, articleId: String) {
    val article = remember {
        Article(
            id = articleId,
            title = "How to Stop Your Cat from Scratching Furniture",
            content = """
                Cats scratch furniture for many reasons: to mark territory, to stretch their muscles, to shed old claw sheaths, or simply because it feels good. While this behavior is natural, it can be frustrating when your favorite couch becomes your cat's favorite scratching post.

                Here are some effective strategies to redirect your cat's scratching behavior:

                1. Provide appropriate scratching alternatives
                2. Make the scratching posts appealing
                3. Make furniture less appealing
                4. Use deterrent sprays
                5. Trim your cat's claws regularly
                6. Consider nail caps
                7. Reward good behavior

                Remember that patience is key. With consistency and positive reinforcement, you can protect your furniture while still allowing your cat to engage in natural scratching behavior.
            """.trimIndent(),
            author = "Dr. Sarah Johnson",
            authorAvatar = R.drawable.logo_icon_colored,
            timestamp = "2 days ago",
            category = "Behavior"
        )
    }

    var isBookmarked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background)
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { isBookmarked = !isBookmarked }) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Unbookmark" else "Bookmark"
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp)
            ) {

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                val computedLineHeight: TextUnit = if (MaterialTheme.typography.bodyLarge.lineHeight.isUnspecified)
                    MaterialTheme.typography.bodyLarge.fontSize * 1.5f
                else
                    MaterialTheme.typography.bodyLarge.lineHeight * 1.5f

                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = computedLineHeight
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArticleScreenPreview() {
    FurrEverCareTheme {
        ArticleScreen(navController = rememberNavController(), articleId = "1")
    }
}
