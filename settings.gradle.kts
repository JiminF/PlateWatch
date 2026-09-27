pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral() } }
kotlin
repositories { maven { url = uri("https://jitpack.io") } }
implementation("com.github.ernestp.AndroidUSBCamera:libausbc:3.6.0")
rootProject.name = "PlateWatch"
include(":app")
