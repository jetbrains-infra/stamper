provider "docker" {
}
resource "docker_image" "nginx" {
  name = "nginx:1.11-alpine"
  keep_locally = true
}

resource "docker_container" "nginx" {
  name = "nginx-test"
  image = "${docker_image.nginx.latest}"
}

output "link" {
  value = "http://google.ru"
}