import org.gradle.kotlin.dsl.invoke
import kotlin.collections.addAll

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
	id("com.google.protobuf") version "0.9.4"

}

group = "me.camwalford"
version = "0.0.1-SNAPSHOT"


java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	google()
	maven("https://packages.confluent.io/maven/")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("com.google.protobuf:protobuf-kotlin:3.25.1")
	implementation("com.google.protobuf:protobuf-java-util:3.14.0")

	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("io.finnhub:kotlin-client:2.0.20")
	implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")
	implementation ("io.confluent:kafka-protobuf-serializer:7.5.1")
	implementation("org.springframework.retry:spring-retry")



	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("io.mockk:mockk:1.13.12")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	jvmArgs(
		"-XX:+EnableDynamicAgentLoading",
		"-Djdk.instrument.traceUsage=false"
	)
}


protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.25.1"
	}
	generateProtoTasks {
		all().forEach { task ->
			task.builtins {
				create("kotlin")
			}
		}
	}
}