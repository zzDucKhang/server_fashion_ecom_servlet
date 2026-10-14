# Stage 1: Build mã nguồn bằng Maven và JDK 17
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# Tải trước dependencies để cache
RUN mvn dependency:go-offline -B
COPY src ./src
# Đóng gói file .war (bỏ qua chạy test khi build để tiết kiệm thời gian)
RUN mvn clean package -DskipTests

# Stage 2: Chạy trên máy chủ Apache Tomcat 10 (hỗ trợ Jakarta EE)
FROM tomcat:10.1-jdk17-temurin
WORKDIR /usr/local/tomcat

# Xóa các webapp mặc định của Tomcat
RUN rm -rf webapps/*

# Copy file war từ Stage 1 vào làm ứng dụng mặc định (ROOT.war)
# Lưu ý: Thay "fashion_ecom-1.0-SNAPSHOT.war" bằng đúng tên file war sinh ra trong thư mục target của bạn
COPY --from=builder /app/target/*.war webapps/ROOT.war

# Render cấp cổng động qua biến $PORT, cấu hình Tomcat lắng nghe theo cổng đó
EXPOSE 8080
CMD ["catalina.sh", "run"]