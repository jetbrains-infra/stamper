provider "docker" {
}
resource "docker_image" "nginx" {
  name = "nginx:${var.version}"
}

resource "docker_container" "nginx" {
  name = "${var.name}"
  image = "${docker_image.nginx.latest}"

  ports {
    internal = 3306
    external = "${var.external_port}"
  }

}

output "link" {
  value = "http://localhost:${var.external_port}"
}

output "haha" {
  value = "Hi hi!"
}