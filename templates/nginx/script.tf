provider "docker" {
}
resource "docker_image" "nginx" {
  name = "nginx:1.11-alpine"
}

resource "docker_container" "nginx" {
  name = "nginx-server"
  image = "${docker_image.nginx.latest}"
}