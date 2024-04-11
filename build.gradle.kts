plugins {
    id("java")
    id("application")
    id("java-library")
    id("org.graalvm.buildtools.native") version "0.10.1"
}

group = "com.github.professorsam"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("args4j:args4j:2.37")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("fileshare")
            mainClass.set("com.github.professorsam.filesharecli.FileShareCli")
        }
    }
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
    options.encoding = "UTF-8"
}