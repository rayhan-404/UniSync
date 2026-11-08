package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CommunityPostEntity
import com.example.ui.components.NeumorphicBadge
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicIconButton
import com.example.ui.components.NeumorphicInsetSurface
import com.example.ui.components.NeumorphicTextField
import com.example.ui.components.StarRatingBar
import com.example.ui.components.neumorphicInset
import com.example.ui.components.neumorphicRaised
import com.example.ui.theme.BaseBackground
import com.example.ui.theme.MainText
import com.example.ui.theme.MutedText
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TertiaryPurple
import com.example.ui.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommunityScreen(viewModel: MainViewModel) {
    val allPosts by viewModel.allCommunityPosts.collectAsState()
    var showPostDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .neumorphicInset(cornerRadius = 18.dp, depth = 2.dp, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Academic Community",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MainText,
                            letterSpacing = (-0.2).sp
                        )
                        Text(
                            text = "Ask questions, share problem solutions, and rate peers",
                            fontSize = 11.sp,
                            color = SecondaryText,
                            lineHeight = 15.sp
                        )
                    }
                }

                NeumorphicButton(
                    onClick = { showPostDialog = true },
                    isPrimary = true,
                    cornerRadius = 14.dp,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
                    modifier = Modifier.testTag("ask_question_header_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Ask",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Posts Feed
            if (allPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    NeumorphicCard(
                        cornerRadius = 20.dp,
                        elevation = 4.dp,
                        contentPadding = PaddingValues(28.dp),
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .neumorphicInset(cornerRadius = 25.dp, depth = 3.dp, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = MutedText,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No community questions yet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MainText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Be the first student to ask a question or discuss an exam topic!",
                                fontSize = 11.sp,
                                color = SecondaryText,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(allPosts) { post ->
                        CommunityPostCardItem(
                            post = post,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }

        // Tactile Raised Floating Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            NeumorphicButton(
                onClick = { showPostDialog = true },
                isPrimary = true,
                cornerRadius = 28.dp,
                elevation = 8.dp,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .size(56.dp)
                    .testTag("create_post_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ask Question",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    if (showPostDialog) {
        CreatePostDialog(
            onDismiss = { showPostDialog = false },
            onConfirm = { title, category, content ->
                viewModel.createCommunityPost(title, category, content)
                showPostDialog = false
            }
        )
    }
}

@Composable
fun CommunityPostCardItem(
    post: CommunityPostEntity,
    viewModel: MainViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    var showReplyDialog by remember { mutableStateOf(false) }

    val dateStr = remember(post.createdAt) {
        SimpleDateFormat("MMM dd, hh:mm a", Locale.ENGLISH).format(Date(post.createdAt))
    }

    NeumorphicCard(
        cornerRadius = 20.dp,
        elevation = 5.dp,
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("community_card_${post.id}")
    ) {
        Column {
            // Author row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .neumorphicInset(cornerRadius = 19.dp, depth = 2.dp, shape = CircleShape)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue.copy(alpha = 0.85f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = post.authorName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MainText
                        )
                        Text(
                            text = "${post.authorRole} • $dateStr",
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    }
                }

                NeumorphicBadge(
                    text = post.categoryTag,
                    color = TertiaryPurple
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = post.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MainText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = post.content,
                fontSize = 12.sp,
                color = SecondaryText,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action row (Upvotes, Solutions, Reply)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tactile Upvote Pill
                    Box(
                        modifier = Modifier
                            .neumorphicRaised(cornerRadius = 12.dp, elevation = 2.5.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { viewModel.upvoteCommunityPost(post.id) }
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Upvote",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${post.upvotes}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }

                    // Solutions Toggle Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { expanded = !expanded }
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Solutions",
                                tint = SecondaryText,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${post.solutionCount} Solutions",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SecondaryText
                            )
                        }
                    }
                }

                NeumorphicButton(
                    onClick = { showReplyDialog = true },
                    cornerRadius = 12.dp,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("reply_btn_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Reply",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Give Solution",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    NeumorphicInsetSurface(
                        cornerRadius = 12.dp,
                        depth = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "💬 Peer Solutions & Peer Ratings:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Click 'Give Solution' to post your answer and rate peer helpers with stars!",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }
                    }
                }
            }
        }
    }

    if (showReplyDialog) {
        AddReplyDialog(
            onDismiss = { showReplyDialog = false },
            onConfirm = { replyText, rating ->
                viewModel.addReplyToPost(post.id, replyText, rating)
                showReplyDialog = false
            }
        )
    }
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("CSE-2101") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseBackground,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Ask Question / Post Problem",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MainText
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NeumorphicTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Question Title / Summary",
                    placeholder = "e.g. Help with AVL tree rotation",
                    singleLine = true,
                    testTag = "input_post_title"
                )
                NeumorphicTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = "Subject Tag (e.g. CSE-2101, Math)",
                    placeholder = "CSE-2101",
                    singleLine = true
                )
                NeumorphicTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = "Problem Details & Specific Questions",
                    placeholder = "Describe where you are stuck...",
                    singleLine = false
                )
            }
        },
        confirmButton = {
            NeumorphicButton(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onConfirm(title, category, content)
                    }
                },
                isPrimary = true,
                cornerRadius = 14.dp,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                modifier = Modifier.testTag("confirm_create_post_btn")
            ) {
                Text("Post Question", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryText, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun AddReplyDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Float) -> Unit
) {
    var solutionText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5.0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseBackground,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Provide Solution & Rate Peer",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MainText
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NeumorphicTextField(
                    value = solutionText,
                    onValueChange = { solutionText = it },
                    label = "Your Explanation / Solution",
                    placeholder = "Type your answer or steps here...",
                    singleLine = false
                )

                Text(
                    text = "Rate Peer Helper (1 to 5 Stars):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Box(
                    modifier = Modifier
                        .neumorphicInset(cornerRadius = 12.dp, depth = 2.dp)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    StarRatingBar(
                        rating = rating,
                        onRatingChanged = { rating = it }
                    )
                }
            }
        },
        confirmButton = {
            NeumorphicButton(
                onClick = {
                    if (solutionText.isNotBlank()) {
                        onConfirm(solutionText, rating)
                    }
                },
                isPrimary = true,
                cornerRadius = 14.dp,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("Submit Solution", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryText, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}