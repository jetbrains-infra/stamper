provider "docker" {
}
resource "docker_image" "ubuntu" {
  name = "ubuntu:${var.version}"
}
resource "docker_container" "ubuntu" {
  name = "${var.name}"
  image = "${docker_image.ubuntu.latest}"
}