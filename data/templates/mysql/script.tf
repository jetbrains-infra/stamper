provider "docker" {
}

resource "docker_image" "mysql" {
  name = "mysql/mysql-server:${var.version}"
}

resource "docker_container" "mysql" {
  image = "${docker_image.mysql.latest}"
  name = "${var.container_name}"
  hostname = "mysql"
  must_run = true
  ports {
    internal = 3306
    external = "${var.external_port}"
  }
  env = [
    "MYSQL_ROOT_PASSWORD=sa",
    "MYSQL_USER=mysql",
    "MYSQL_PASSWORD=mysql",
    "MYSQL_DATABASE=sushe"]
}

output "link" {
  value = "http://google.ru"
}