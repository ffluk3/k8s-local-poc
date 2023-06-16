plugins {
    id("java")
    id("idea")
    id("org.springframework.boot") version "2.1.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
    id("com.google.cloud.tools.jib") version "3.3.2"
}

repositories {
    mavenCentral()
}

jib {
    to.image = "localhost:5000/hello"
    to.tags = setOf("1.0.0")
}

//sourceCompatibility = 1.8
//targetCompatibility = 1.8

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}