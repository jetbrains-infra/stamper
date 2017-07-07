provider "docker" {
}
resource "docker_image" "ubuntu" {
  name = "ubuntu:precise"
}
resource "docker_container" "ubuntu" {
  name = "hello-server"
  image = "${docker_image.ubuntu.latest}"
}