# OpenGL ES 2.0 高斯模糊使用指南

## 核心组件

### 1. GaussianBlurRenderer
核心渲染器，实现分离式高斯模糊（Two-pass）。

### 2. BlurGLSurfaceView
自定义 GLSurfaceView，封装渲染器，提供简洁 API。

### 3. BlurHelper
工具类，管理 View 截图和模糊渲染。

### 4. BlurBackground (Compose)
Compose 集成组件，支持静态和动态模糊。

---

## 使用示例

### 示例 1：在 Activity 中使用（Java/Kotlin）

```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var blurView: BlurGLSurfaceView
    private lateinit var sourceView: View
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        sourceView = findViewById(R.id.sourceView)
        blurView = findViewById(R.id.blurView)
        
        // 设置模糊参数
        blurView.setBlurRadius(10.0f)        // 模糊半径 0-25
        blurView.setDownsampleFactor(4)       // 1/4 降采样
        
        // 捕获源 View 并更新模糊
        val bitmap = BlurHelper.captureView(sourceView, 0.25f)
        if (bitmap != null) {
            blurView.updateBitmap(bitmap)
        }
    }
    
    override fun onResume() {
        super.onResume()
        blurView.resumeBlur()
    }
    
    override fun onPause() {
        super.onPause()
        blurView.pauseBlur()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        blurView.release()
    }
}
```

**布局文件 (activity_main.xml)：**

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- 源 View（要模糊的内容） -->
    <ImageView
        android:id="@+id/sourceView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"
        android:src="@drawable/background" />
    
    <!-- 模糊渲染层 -->
    <com.notificationmcp.opengl.BlurGLSurfaceView
        android:id="@+id/blurView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
    
    <!-- 前景内容 -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:text="模糊背景上的文字"
        android:textColor="@android:color/white"
        android:textSize="24sp" />
    
</FrameLayout>
```

---

### 示例 2：在 Compose 中使用

```kotlin
@Composable
fun MyScreen() {
    var sourceView by remember { mutableStateOf<View?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 源 View（要模糊的内容）
        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    setImageResource(R.drawable.background)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    sourceView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // 模糊背景 + 前景内容
        BlurBackground(
            sourceView = sourceView,
            blurRadius = 10.0f,
            downsampleFactor = 4,
            modifier = Modifier.fillMaxSize()
        ) {
            // 前景内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "模糊背景上的文字",
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
        }
    }
}
```

---

### 示例 3：动态模糊（实时更新）

```kotlin
@Composable
fun DynamicBlurScreen() {
    var sourceView by remember { mutableStateOf<View?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 源 View（滚动内容）
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(100) { index ->
                Text(
                    text = "Item $index",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        
        // 动态模糊背景（实时更新）
        DynamicBlurBackground(
            sourceView = sourceView,
            blurRadius = 10.0f,
            downsampleFactor = 4,
            updateIntervalMs = 50L,  // 每 50ms 更新一次
            modifier = Modifier.fillMaxSize()
        ) {
            // 前景内容
            Text(
                text = "动态模糊背景",
                color = Color.White,
                fontSize = 24.sp
            )
        }
    }
}
```

---

### 示例 4：为 ImageView 设置模糊背景

```kotlin
// 在 Activity 中
val imageView = findViewById<ImageView>(R.id.imageView)
val sourceView = findViewById<View>(R.id.sourceView)

BlurHelper.setBlurBackground(
    imageView = imageView,
    sourceView = sourceView,
    blurRadius = 10.0f,
    downsampleFactor = 4
)
```

---

### 示例 5：创建动态模糊渲染器

```kotlin
// 在 Activity 中
val blurView = BlurHelper.createDynamicBlur(
    activity = this,
    sourceView = sourceView,
    blurRadius = 10.0f,
    downsampleFactor = 4
)

// 添加到布局
val container = findViewById<FrameLayout>(R.id.container)
container.addView(blurView, 0)  // 添加到最底层
```

---

## API 参考

### GaussianBlurRenderer

| 方法 | 参数 | 说明 |
|------|------|------|
| `setBlurRadius(radius: Float)` | 0.0 - 25.0 | 设置模糊半径 |
| `getBlurRadius(): Float` | - | 获取当前模糊半径 |
| `setDownsampleFactor(factor: Int)` | 1, 2, 4, 8 | 设置降采样因子 |
| `getDownsampleFactor(): Int` | - | 获取当前降采样因子 |
| `updateBitmap(bitmap: Bitmap?)` | - | 更新输入 Bitmap |
| `release()` | - | 释放所有 OpenGL 资源 |

### BlurGLSurfaceView

| 方法 | 参数 | 说明 |
|------|------|------|
| `setBlurRadius(radius: Float)` | 0.0 - 25.0 | 设置模糊半径 |
| `setDownsampleFactor(factor: Int)` | 1, 2, 4, 8 | 设置降采样因子 |
| `updateBitmap(bitmap: Bitmap?)` | - | 更新输入 Bitmap |
| `pauseBlur()` | - | 暂停渲染 |
| `resumeBlur()` | - | 恢复渲染 |
| `release()` | - | 释放所有资源 |

### BlurHelper

| 方法 | 参数 | 说明 |
|------|------|------|
| `captureView(view, scale)` | scale: 0.0-1.0 | 捕获 View 为 Bitmap |
| `captureAndUpdateBlur(sourceView, blurView, scale)` | - | 捕获并更新模糊 |
| `setBlurBackground(imageView, sourceView, radius, downsample)` | - | 设置模糊背景 |
| `createDynamicBlur(activity, sourceView, radius, downsample)` | - | 创建动态模糊渲染器 |
| `releaseBlurView(blurView)` | - | 释放渲染器资源 |

---

## 性能优化建议

1. **降采样因子**：默认 4（1/4 降采样），可显著提升性能
2. **模糊半径**：建议 5-15，过大影响性能
3. **更新频率**：动态模糊建议 50-100ms 间隔
4. **Bitmap 复用**：避免频繁创建新 Bitmap
5. **及时释放**：Activity 销毁时调用 `release()`

---

## 注意事项

1. **线程安全**：所有 OpenGL 操作在 GLThread 执行，不阻塞 UI 线程
2. **内存管理**：及时调用 `release()` 释放资源
3. **Bitmap 回收**：`updateBitmap()` 会自动回收旧 Bitmap
4. **透明度支持**：已启用混合模式，支持透明背景
5. **兼容性**：需要 OpenGL ES 2.0 支持（Android 2.2+）