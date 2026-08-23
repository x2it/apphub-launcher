// 显式配置仓库，绕开沙箱/代理对 google() 别名的不稳定性
pluginManagement {
    repositories {
        maven { setUrl("https://dl.google.com/dl/android/maven2/") }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { setUrl("https://dl.google.com/dl/android/maven2/") }
        mavenCentral()
    }
}

rootProject.name = "AppHub"
include(":app")
