provider "docker" {
}
resource "docker_image" "ubuntu" {
  name = "ubuntu:${var.version}"
  keep_locally = true
}
resource "docker_container" "ubuntu" {
  name = "${var.name}"
  image = "${docker_image.ubuntu.latest}"

}