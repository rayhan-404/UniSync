package com.example.ui.components

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BaseBackground
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MainText
import com.example.ui.theme.MutedText
import com.example.ui.theme.NeumorphicHighlightLight
import com.example.ui.theme.NeumorphicShadowDark
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TertiaryPurple

/**
 * Deep, realistic Neumorphic Raised modifier.
 * Creates an authentic extruded 3D surface using:
 * - Dual blurred directional shadows (Top-Left light specular highlight + Bottom-Right deep shadow)
 * - Secondary ambient occlusion soft blur for physical presence
 * - Subtle convex surface gradient for smooth tactile curvature
 * - Micro-bevel rim stroke to define crisp geometry
 */
fun Modifier.neumorphicRaised(
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 7.dp,
    backgroundColor: Color = BaseBackground,
    darkShadowColor: Color = NeumorphicShadowDark.copy(alpha = 0.65f),
    lightShadowColor: Color = NeumorphicHighlightLight.copy(alpha = 0.95f),
    shape: Shape = RoundedCornerShape(cornerRadius)
): Modifier = this
    .drawBehind {
        val radiusPx = cornerRadius.toPx()
        val elevPx = elevation.toPx()
        val blurRadius = (elevPx * 1.6f).coerceAtLeast(1f)
        val ambientBlur = (elevPx * 2.4f).coerceAtLeast(1f)

        drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas

            // 1. Ambient soft bottom-right diffusion
            val ambientPaint = Paint().apply {
                isAntiAlias = true
                color = darkShadowColor.copy(alpha = darkShadowColor.alpha * 0.45f).toArgb()
                maskFilter = BlurMaskFilter(ambientBlur, BlurMaskFilter.Blur.NORMAL)
            }
            val ambOffset = elevPx * 0.5f
            nativeCanvas.drawRoundRect(
                ambOffset,
                ambOffset,
                size.width + ambOffset,
                size.height + ambOffset,
                radiusPx,
                radiusPx,
                ambientPaint
            )

            // 2. Light specular highlight on Top-Left
            val lightPaint = Paint().apply {
                isAntiAlias = true
                color = lightShadowColor.toArgb()
                maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
            }
            val lightOffset = -elevPx * 0.9f
            nativeCanvas.drawRoundRect(
                lightOffset,
                lightOffset,
                size.width + lightOffset,
                size.height + lightOffset,
                radiusPx,
                radiusPx,
                lightPaint
            )

            // 3. Deep crisp shadow on Bottom-Right
            val darkPaint = Paint().apply {
                isAntiAlias = true
                color = darkShadowColor.toArgb()
                maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
            }
            val darkOffset = elevPx * 0.9f
            nativeCanvas.drawRoundRect(
                darkOffset,
                darkOffset,
                size.width + darkOffset,
                size.height + darkOffset,
                radiusPx,
                radiusPx,
                darkPaint
            )
        }

        // 4. Subtle convex 3D gradient surface fill (elevated extruded appearance)
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    backgroundColor,
                    backgroundColor.copy(alpha = 0.98f)
                ),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            ),
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(radiusPx, radiusPx)
        )

        // 5. Delicate micro-bevel rim for crisp edge definition
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    lightShadowColor.copy(alpha = 0.60f),
                    darkShadowColor.copy(alpha = 0.25f)
                ),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            ),
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(radiusPx, radiusPx),
            style = Stroke(width = 0.8.dp.toPx())
        )
    }
    .clip(shape)

/**
 * Deep Neumorphic Inset modifier (engraved / pressed groove).
 * Simulates an authentic hollowed cavity with deep directional inner shadowing,
 * ambient recession, and crisp highlight rim.
 */
fun Modifier.neumorphicInset(
    cornerRadius: Dp = 14.dp,
    depth: Dp = 3.5.dp,
    backgroundColor: Color = BaseBackground,
    darkShadowColor: Color = NeumorphicShadowDark.copy(alpha = 0.55f),
    lightHighlightColor: Color = NeumorphicHighlightLight.copy(alpha = 0.90f),
    shape: Shape = RoundedCornerShape(cornerRadius)
): Modifier = this
    .clip(shape)
    .drawBehind {
        val radiusPx = cornerRadius.toPx()
        val depthPx = depth.toPx()

        // 1. Recessed base fill
        drawRoundRect(
            color = backgroundColor,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(radiusPx, radiusPx)
        )

        // 2. Primary Top-Left Deep Inner Shadow Gradient
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    darkShadowColor,
                    darkShadowColor.copy(alpha = 0.40f),
                    Color.Transparent
                ),
                start = Offset.Zero,
                end = Offset(depthPx * 4f, depthPx * 4f)
            ),
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(radiusPx, radiusPx)
        )

        // 3. Top edge dark shade line
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(darkShadowColor.copy(alpha = 0.45f), Color.Transparent),
                startY = 0f,
                endY = depthPx * 2.5f
            ),
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(radiusPx, radiusPx)
        )

        // 4. Bottom-Right Inner Specular Highlight Gradient
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    lightHighlightColor.copy(alpha = 0.40f),
                    lightHighlightColor
                ),
                start = Offset(size.width - depthPx * 4f, size.height - depthPx * 4f),
                end = Offset(size.width, size.height)
            ),
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(radiusPx, radiusPx)
        )

        // 5. Crisp inner bevel rim stroke
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    darkShadowColor.copy(alpha = 0.40f),
                    lightHighlightColor.copy(alpha = 0.80f)
                ),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            ),
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(radiusPx, radiusPx),
            style = Stroke(width = 1.dp.toPx())
        )
    }

/**
 * Deep Neumorphic Card container with rich tactile depth.
 */
@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 8.dp,
    backgroundColor: Color = BaseBackground,
    hasDecorations: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val accentColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .neumorphicRaised(
                cornerRadius = cornerRadius,
                elevation = elevation,
                backgroundColor = backgroundColor
            )
            .then(
                if (hasDecorations) {
                    Modifier.drawBehind {
                        val radPx = 75.dp.toPx()
                        // Top-right soft primary orb (.card::before)
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.06f),
                            radius = radPx,
                            center = Offset(size.width + 12.dp.toPx(), -12.dp.toPx())
                        )
                        // Bottom-left soft accent orb (.card::after)
                        drawCircle(
                            color = accentColor.copy(alpha = 0.05f),
                            radius = radPx * 0.75f,
                            center = Offset(-12.dp.toPx(), size.height + 12.dp.toPx())
                        )
                    }
                } else Modifier
            )
            .padding(contentPadding),
        content = content
    )
}


/**
 * Tactile Theme Toggle Switch matching the .theme-toggle & .theme-thumb in the CSS reference.
 * A 64x32dp neumorphic recessed track with an animated sliding 24x24dp thumb.
 */
@Composable
fun NeumorphicThemeToggle(
    isDark: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbOffset by animateFloatAsState(
        targetValue = if (isDark) 30f else 0f,
        label = "themeThumbOffset"
    )

    Box(
        modifier = modifier
            .size(width = 62.dp, height = 32.dp)
            .neumorphicInset(cornerRadius = 16.dp, depth = 2.5.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onToggle(!isDark) }
            )
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset.dp)
                .size(24.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                contentDescription = "Theme Mode",
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }

    }
}

/**
 * Neumorphic Stat Chip matching .stat-chip from the CSS reference.
 * Perfectly uniform and aligned with fixed vertical layout.
 */
@Composable
fun NeumorphicStatChip(
    number: String,
    label: String,
    modifier: Modifier = Modifier,
    numberColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .neumorphicRaised(cornerRadius = 18.dp, elevation = 4.dp)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = number,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = numberColor,
                letterSpacing = (-0.5).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = SecondaryText,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Modern Neumorphic Toggle Switch with dynamic track color, smooth thumb sliding, and spring animations.
 */
@Composable
fun NeumorphicSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkedColor: Color = MaterialTheme.colorScheme.primary,
    uncheckedColor: Color = BaseBackground
) {
    val trackWidth = 52.dp
    val trackHeight = 30.dp
    val thumbSize = 22.dp

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 4.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioLowBouncy
        ),
        label = "thumbOffset"
    )

    val trackBgColor by animateColorAsState(
        targetValue = if (checked) checkedColor.copy(alpha = 0.18f) else uncheckedColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "trackBgColor"
    )

    val thumbColor by animateColorAsState(
        targetValue = if (checked) checkedColor else MutedText,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "thumbColor"
    )

    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .neumorphicInset(cornerRadius = 15.dp, depth = 2.5.dp, shape = RoundedCornerShape(15.dp))
            .background(trackBgColor, RoundedCornerShape(15.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .neumorphicRaised(cornerRadius = thumbSize / 2, elevation = 3.dp, shape = CircleShape)
                .background(thumbColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

/**
 * Categorization helper for academic tasks with distinct color palettes.
 */
fun getTaskCategoryColor(taskType: String): Color {
    return when (taskType.trim().lowercase()) {
        "exam", "semester exam", "final", "midterm" -> Color(0xFFE63946)
        "ct", "class test" -> Color(0xFF0077B6)
        "assignment" -> Color(0xFF7209B7)
        "lab", "lab report", "lab task" -> Color(0xFFE85D04)
        "quiz" -> Color(0xFF4361EE)
        "presentation" -> Color(0xFF2A9D8F)
        "project" -> Color(0xFF1D3557)
        "viva" -> Color(0xFFC71585)
        "class task" -> Color(0xFF4A6572)
        else -> Color(0xFF0077B6)
    }
}

/**
 * Returns a fitting Material Icon for each task category.
 */
fun getTaskCategoryIcon(taskType: String): ImageVector {
    return when (taskType.trim().lowercase()) {
        "exam", "semester exam", "final", "midterm" -> Icons.Default.School
        "ct", "class test" -> Icons.Default.FactCheck
        "assignment" -> Icons.AutoMirrored.Filled.Assignment
        "lab", "lab report", "lab task" -> Icons.Default.Biotech
        "quiz" -> Icons.Default.Quiz
        "presentation" -> Icons.Default.CoPresent
        "project" -> Icons.Default.Book
        "viva" -> Icons.Default.RecordVoiceOver
        "class task" -> Icons.Default.EditNote
        else -> Icons.Default.Description
    }
}


/**
 * Reusable Neumorphic Inset / Recessed Surface.
 */
@Composable
fun NeumorphicInsetSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 14.dp,
    depth: Dp = 3.dp,
    backgroundColor: Color = BaseBackground,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .neumorphicInset(
                cornerRadius = cornerRadius,
                depth = depth,
                backgroundColor = backgroundColor
            )
            .padding(contentPadding),
        content = content
    )
}

/**
 * Reusable Tactile Neumorphic Button with interactive press depression & vibrant gradient support.
 * Styled to match the signature "Add CT / Assignment" button across the entire app.
 */
@Composable
fun NeumorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 6.dp,
    isPrimary: Boolean = false,
    gradient: Brush? = null,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary,
    textColor: Color = if (isPrimary || gradient != null) Color.White else MainText,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 13.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        label = "pressScale"
    )

    val effectiveGradient = gradient ?: if (isPrimary) {
        Brush.horizontalGradient(
            listOf(primaryColor, secondaryColor)
        )
    } else null

    val cardModifier = if (isPressed) {
        Modifier.neumorphicInset(cornerRadius = cornerRadius, depth = 3.dp)
    } else {
        Modifier.neumorphicRaised(cornerRadius = cornerRadius, elevation = elevation)
    }

    Box(
        modifier = modifier
            .scale(scale)
            .then(cardModifier)
            .clip(RoundedCornerShape(cornerRadius))
            .then(
                if (effectiveGradient != null) {
                    Modifier.background(effectiveGradient)
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surface)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            val scope = object : RowScope {}
            scope.content()
        }
    }
}

interface RowScope

/**
 * Reusable Tactile Neumorphic Icon Button (Circular or Rounded Square).
 */
@Composable
fun NeumorphicIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    iconSize: Dp = 20.dp,
    tint: Color = MainText,
    isCircular: Boolean = true,
    cornerRadius: Dp = 12.dp,
    isActive: Boolean = false,
    activeColor: Color = PrimaryBlue
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = if (isCircular) CircleShape else RoundedCornerShape(cornerRadius)

    val mod = if (isPressed || isActive) {
        Modifier.neumorphicInset(
            cornerRadius = if (isCircular) size / 2 else cornerRadius,
            depth = 3.dp,
            shape = shape
        )
    } else {
        Modifier.neumorphicRaised(
            cornerRadius = if (isCircular) size / 2 else cornerRadius,
            elevation = 4.dp,
            shape = shape
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .then(mod)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) activeColor else tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Reusable Soft Tactile Badge / Pill.
 */
@Composable
fun NeumorphicBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = PrimaryBlue,
    backgroundColor: Color = color.copy(alpha = 0.12f),
    icon: ImageVector? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                letterSpacing = 0.2.sp
            )
        }
    }
}

/**
 * Reusable Neumorphic Text Field (Recessed Input Box).
 */
@Composable
fun NeumorphicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 5,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    cornerRadius: Dp = 14.dp,
    testTag: String? = null
) {
    androidx.compose.foundation.layout.Column(modifier = modifier.fillMaxWidth()) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SecondaryText,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )
        }

        val baseMod = Modifier
            .fillMaxWidth()
            .neumorphicInset(cornerRadius = cornerRadius, depth = 3.dp)
            .padding(horizontal = 14.dp, vertical = if (singleLine) 12.dp else 14.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)

        Box(
            modifier = baseMod,
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = MutedText,
                        modifier = Modifier
                            .padding(end = 10.dp, top = if (singleLine) 0.dp else 2.dp)
                            .size(18.dp)
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            color = MutedText,
                            style = LocalTextStyle.current
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = singleLine,
                        minLines = minLines,
                        maxLines = maxLines,
                        visualTransformation = visualTransformation,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            color = MainText,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(PrimaryBlue),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }
    }
}