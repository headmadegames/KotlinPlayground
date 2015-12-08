package headmade.kotlinplayground.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import headmade.kotlinplayground.KotlinPlayground

object DesktopLauncher {
    @JvmStatic fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.width = 480
        config.height = 800
        config.samples = 4
        LwjglApplication(KotlinPlayground(), config)
    }
}
