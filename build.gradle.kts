plugins {
    kotlin("multiplatform") version "1.6.21"
}

group = "org.laolittle"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://maven.aliyun.com/repository/central")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
   /* js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }*/

    linuxX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies{
                implementation("com.squareup.okio:okio:3.0.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
       // val jsMain by getting
       // val jsTest by getting
        val linuxX64Main by getting
        val linuxX64Test by getting
        val mingwX64Main by getting
        val mingwX64Test by getting
    }
}
