plugins {
    id("java")
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

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
    options.encoding = "UTF-8"
}