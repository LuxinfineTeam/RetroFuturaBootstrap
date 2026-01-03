plugins {
  `java-library`
  id("com.github.gmazzo.buildconfig") version "5.6.5"
}

group = "com.gtnewhorizons.retrofuturabootstrap"

repositories {
  maven { url = uri("https://libraries.minecraft.net/") }
  maven {
    url = uri("https://files.prismlauncher.org/maven")
    metadataSources { artifact() }
  }
  mavenCentral()
}

val asmVersion = "9.9.1"

dependencies {
  //testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
  //testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  compileOnly("me.eigenraven.java8unsupported:java-8-unsupported-shim:1.0.0")
  compileOnlyApi("org.jetbrains:annotations:24.1.0")
  api("net.sf.jopt-simple:jopt-simple:4.5")
  api("org.ow2.asm:asm-commons:${asmVersion}")
  api("org.ow2.asm:asm-tree:${asmVersion}")
  api("org.ow2.asm:asm-util:${asmVersion}")
  api("org.ow2.asm:asm-analysis:${asmVersion}")
  compileOnly("org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209")
  api("org.apache.logging.log4j:log4j-core:2.0-beta9-fixed")
  api("org.apache.logging.log4j:log4j-api:2.0-beta9-fixed")
}

version = "1.0.13"

buildConfig {
  buildConfigField("String", "VERSION", provider { "\"${project.version}\"" })
  className("BuildConfig")
  packageName("com.gtnewhorizons.retrofuturabootstrap")
  useJavaOutput()
}

lateinit var java9: SourceSet

// Apply a specific Java toolchain to ease working on different environments.
java {
  sourceSets {
    // Stub classes, not actually included in the jar
    main {}
    java9 =
        create("java9") {
          compileClasspath +=
              this@sourceSets.main.get().output + files(configurations.compileClasspath)
        }
  }
}

tasks.withType<JavaCompile>() {
  options.encoding = "UTF-8"
  options.release = 8
}

tasks.named<JavaCompile>(java9.compileJavaTaskName) { options.release = 9 }

tasks.jar {
  into("META-INF/versions/9") { from(java9.output) }
  manifest.attributes["Multi-Release"] = "true"
  manifest.attributes["Specification-Title"] = "launchwrapper"
  manifest.attributes["Specification-Version"] = "1.12"
  manifest.attributes["Specification-Vendor"] = "Minecraft"
  manifest.attributes["Implementation-Title"] = "RetroFuturaBootstrap"
  manifest.attributes["Implementation-Version"] = project.version.toString()
  manifest.attributes["Implementation-Vendor"] = "GTNewHorizons"
}

tasks.processResources {
  inputs.property("version", project.version.toString())
  filesMatching("**/*.properties") { expand("version" to project.version.toString()) }
}
