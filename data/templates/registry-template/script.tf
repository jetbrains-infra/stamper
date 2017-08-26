provider "docker" {
}
resource "docker_image" "tomcat" {
  name = "docker-registry.labs.intellij.net/base/alpine-jdk-tomcat:${var.version}"
}

resource "docker_container" "tomcat" {
  name = "${var.name}"
  image = "${docker_image.tomcat.latest}"

  ports {
    internal = 8080
    external = "${var.external_port}"
  }

}

output "link" {
  value = "http://localhost:${var.external_port}"
}