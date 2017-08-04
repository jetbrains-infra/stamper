provider "docker" {
}
resource "docker_image" "ubuntu" {
  name = "ubuntu:${var.version}"
}
resource "docker_container" "ubuntu" {
  name = "ubuntu-server"
  image = "${docker_image.ubuntu.latest}"
}