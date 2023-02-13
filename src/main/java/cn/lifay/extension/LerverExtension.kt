package cn.lifay.extension

import cn.lifay.ui.LoadingUI
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


inline fun asyncTask(
    owner: Stage,
    msg: String = "请耐心等待...",
    animation: Boolean = false,
    crossinline block: () -> Unit
) {
    val loadingUI = LoadingUI(owner, animation = animation, msg = msg)
    CoroutineScope(Dispatchers.Default).launch {
        try {
            loadingUI.show()
            //执行任务
            block()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            loadingUI.closeStage()
        }
    }
}