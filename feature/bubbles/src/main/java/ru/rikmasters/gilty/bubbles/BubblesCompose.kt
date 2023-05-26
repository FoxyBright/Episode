package ru.rikmasters.gilty.bubbles

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.jawnnypoo.physicslayout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.*
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.feature.bubbles.R
import kotlin.math.*
import kotlin.random.Random

private class PhysicsController: Physics.OnBodyCreatedListener {

    private val bodies = ArrayList<Body>()

    private val random = Random(System.currentTimeMillis())

    override fun onBodyCreated(view: View, body: Body) {
        bodies.add(body)
    }

    fun onHorizontalDrag(change: PointerInputChange, amount: Float) {
        change.consume()
        bodies.forEach {
            val force = amount / 5
            val addRandom = abs(force) > 5f
            it.applyForce(
                Vec2(
                    force + if(addRandom) randomForce(1, false) else 0f,
                    if(addRandom) randomForce(2, true) else 0f
                ),
                Vec2()
            )
        }
    }

    private fun randomForce(max: Int, negative: Boolean): Float =
        if(negative) (random.nextFloat() * 2f - 1f) * max
        else random.nextFloat() * max
}

private fun Float.ceilToInt() = ceil(this).roundToInt()

const val ROW_OFFSET_FACTOR = 0.2f // 0.2
const val ROW_SPACE_FACTOR = 0.0f // 01
const val COLUMN_OFFSET_FACTOR = 0.4f // 0.2
const val COLUMN_SPACE_FACTOR = 0.0f // 0.1

@Composable
@SuppressLint("InflateParams")
fun <T> Bubbles(
    data: List<T>,
    elementSize: Dp,
    modifier: Modifier = Modifier,
    factory: @Composable (T) -> Unit,
) {
    val layoutMutex = Mutex(true)
    var width = 0f
    var height = 0f
    val size = LocalDensity.current.run { elementSize.toPx() }
    var isStartFinished = false

    val controller = remember { PhysicsController() }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit, block = {
        isStartFinished = false
    })

    if(data.isNotEmpty()) AndroidView(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                width = it.size.width.toFloat()
                height = it.size.height.toFloat()
                if (layoutMutex.isLocked) layoutMutex.unlock()
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(onHorizontalDrag = controller::onHorizontalDrag)
            },

        factory = { context ->

            val inflater = LayoutInflater.from(context)
            val layout = inflater
                .inflate(R.layout.physics_layout, null) as PhysicsFrameLayout

            layout.physics.apply { globalConfig() }
                .setOnBodyCreatedListener(controller)

            scope.launch {
                layout.physics.setGravityX(4.0f)
            }

            scope.launch {
                layoutMutex.lock()

                val rowHeight = size * (1f + ROW_SPACE_FACTOR)
                val rowOffsetHeight = size * ROW_OFFSET_FACTOR
                val rows = ((height - rowOffsetHeight) / rowHeight).toInt()

                val columns = (data.size.toFloat() / rows).ceilToInt()
                val columnWidth = size * (1f + COLUMN_SPACE_FACTOR + COLUMN_OFFSET_FACTOR * rows)
                val actualWidth = columnWidth * columns
                val middleWidth = width / 2
                log.v("Width: $width Height: $height")
                log.v("Actual width: $actualWidth")
                log.v("Size: ${data.size} Rows: $rows Columns: $columns")
                log.v("Element size: $size")

                layout.createBounds(width, height, actualWidth)

                layout.createElements(data, factory, size, rows, (actualWidth - width) / 2)

                while (true){

                    for (i in 0 until layout.childCount) {
                        val view = layout.getChildAt(i)
                        if(view.id == data.size / 2 && !isStartFinished){ // Middle Item
                            // Method 2 - second time not working - closest
                            if(view.x - middleWidth in (-400.0..0.0)){
                                    layout.physics.setGravityX(-7f)
                                    delay(500L)
                                layout.physics.setGravityX(7f)
                                layout.physics.setGravityX(0f)
                                    isStartFinished = true
                            }

                            // Method 3 - Plays around
                            /*while((view.x - middleWidth) !in (-100.0 .. 100.0)){
                                if(view.x > middleWidth){
                                    layout.physics.setGravityX(-4f)
                                }
                                else {
                                    layout.physics.setGravityX(4f)
                                }
                                delay(200L)
                            }
                            if((view.x - middleWidth) in (-100.0 .. 100.0)){
                                layout.physics.setGravityX(0f)
                            }*/

                            // Method 1 - Not stable
                            /*if(view.x - middleWidth in (-240.0)..(-200.0)){
                                layout.physics.setGravityX(2f)
                            }else if (view.x - middleWidth in (160.0)..(240.0)){
                                layout.physics.setGravityX(-2f)
                            }else if(view.x - middleWidth in (-200.0)..(160.0)){
                                layout.physics.setGravityX(-8f)
                                delay(50L)
                                layout.physics.setGravityX(0f)
                            }*/
                        }

                        Log.d("Bubbles coordinates", "viewId: ${view.id} x: ${view.x} y: ${view.y} middle: $middleWidth")

                    }
                    delay(400L)
                }
            }

            return@AndroidView layout
        },
        update = {}
    )
}

private fun PhysicsFrameLayout.createBounds(width: Float, height: Float, actualWidth: Float) {

    val overflow = max(0f, actualWidth - width)

    log.v("Overflow: $overflow")

    val horizontalParams = ViewGroup.LayoutParams((width + overflow).ceilToInt(), 1)
    val verticalParams = ViewGroup.LayoutParams(1, height.ceilToInt())

    val top = 0f
    val start = -overflow / 2
    val end = width + overflow / 2

    log.v("Top: $top Bottom: $height Start: $start End: $end")

    createBoundView(horizontalParams, start, top) // Top horizontal
    createBoundView(horizontalParams, start, height) // Bottom horizontal
    createBoundView(verticalParams, start, top) // Start vertical
    createBoundView(verticalParams, end, top) // End vertical
}

private fun PhysicsFrameLayout.createBoundView(
    params: ViewGroup.LayoutParams,
    xPos: Float, yPos: Float,
) = View(context).apply {
    layoutParams = params
    x = xPos
    y = yPos
    Physics.setPhysicsConfig(this, PhysicsConfig(
        bodyDef = BodyDef().apply { type = BodyType.STATIC }
    ))
    addView(this)
}

private suspend fun <T> PhysicsFrameLayout.createElements(
    data: List<T>, factory: @Composable (T) -> Unit,
    elementSize: Float,
    rows: Int,
    invisibleWidthSpace: Float,
) {
    this.physics.giveRandomImpulse()
    data.chunked(rows)
        .forEachIndexed { columnIndex, elements ->
            elements.forEachIndexed { rowIndex, element ->
                val view = createBubbleView(context, factory, element)

                view.x = -invisibleWidthSpace + (columnIndex * (elementSize + COLUMN_SPACE_FACTOR * elementSize) +
                        (if(rowIndex % 2 == 0) 20f else 0f))

                view.y = (rowIndex * (elementSize + ROW_SPACE_FACTOR * elementSize) +
                        (if(columnIndex % 2 == 0) 0f else ROW_OFFSET_FACTOR) * elementSize)
                view.id = rows * columnIndex + rowIndex
                addView(view)
            }
        }
}

private fun <T> createBubbleView(
    context: Context,
    factory: @Composable (T) -> Unit,
    element: T,
) = ComposeView(context).apply {

    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    )
    setContent {
        Box {
            factory(element)
        }
    }

    Physics.setPhysicsConfig(this, bubbleConfig())
}

private fun Physics.globalConfig() {
    isFlingEnabled = false
    hasBounds = false

}

private fun bubbleConfig() = PhysicsConfig(
    shape = Shape.CIRCLE,
    fixtureDef = FixtureDef().apply {
        friction = 0.2f // 0.2f
        restitution = 0.2f // 0.2f
        density = 0.2f // 0.2f
    },
    bodyDef = BodyDef().apply {
        type = BodyType.DYNAMIC
        fixedRotation = true
        linearDamping = 0.6f // 0.6f
    }
)
