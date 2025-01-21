//package cn.lifay.application
//
//import atlantafx.base.theme.PrimerDark
//import atlantafx.base.theme.PrimerLight
//import atlantafx.base.theme.Theme
//import cn.lifay.global.LerverConfig
//import cn.lifay.global.GlobalResource
//
//class AppManage(
//    val appLogPrefix: String = "client",
//    val appLogPath: String = GlobalResource.USER_DIR + "logs",
//    val appTheme: Theme? = null,
//    val appConfigPath: String = GlobalResource.USER_DIR + "lerver.yml",
//) {
//    companion object {
//
//        fun loadAppConfig(appManage: AppManage? = AppManage()) {
//            println("loadAppConfig")
//            if (appManage == null) {
//                return
//            }
////            LerverLog.InitLog(appManage.appLogPrefix, appManage.appLogPath)
////            GlobalConfig.InitConfigPath(appManage.appConfigPath)
//
//            //loadTheme
//            if (appManage.appTheme != null) {
//                GlobalResource.loadTheme(appManage.appTheme)
//            } else {
//                LerverConfig.ReadProperties("theme", PrimerLight().name)!!.let {
//                    if ("THEME_DARK" == it) {
//                        GlobalResource.loadTheme(PrimerDark())
//                    } else {
//                        GlobalResource.loadTheme()
//                    }
//                }
//            }
//
//        }
//
//        fun cancelApp() {
//
//        }
//    }
//}