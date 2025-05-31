package com.example.cligenerator.model;

import lombok.Getter;

import java.util.Objects;

/**
 *
 */
@Getter
public final class DatabaseConfig {
    private final DatabaseType type;
    private final String url;
    private final String username;
    private final String password;
    private final String driverClassName;

    /**
     * @param type Getters
     */
    public DatabaseConfig(DatabaseType type, String url,
                          String username, String password, String driverClassName) {
        this.type = type;
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
    }

    public DatabaseType getType() {
        return this.type;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    @Getter
    public enum DatabaseType {
        H2("h2", "H2 Database (In-memory)", "com.h2database:h2"),
        MYSQL("mysql", "MySQL", "mysql:mysql-connector-java"),
        POSTGRESQL("postgresql", "PostgreSQL", "org.postgresql:postgresql"),
        MARIADB("mariadb", "MariaDB", "org.mariadb.jdbc:mariadb-java-client"),
        ORACLE("oracle", "Oracle Database", "com.oracle.database.jdbc:ojdbc8");

        private final String springInitializrName;
        private final String displayName;
        private final String mavenDependency;

        DatabaseType(String springInitializrName, String displayName, String mavenDependency) {
            this.springInitializrName = springInitializrName;
            this.displayName = displayName;
            this.mavenDependency = mavenDependency;
        }

        public String getSpringInitializrName() {
            return this.springInitializrName;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getMavenDependency() {
            return this.mavenDependency;
        }
    }

    public static DatabaseConfig createDefaultConfig(DatabaseType type, String projectName) {
        return switch (type) {
            case H2 -> new DatabaseConfig(
                    type,
                    "jdbc:h2:mem:testdb",
                    "sa",
                    "password",
                    "org.h2.Driver");
            case MYSQL -> new DatabaseConfig(
                    type,
                    "jdbc:mysql://localhost:3306/" + projectName.toLowerCase().replace("-", "_"),
                    "root",
                    "password",
                    "com.mysql.cj.jdbc.Driver");
            case POSTGRESQL -> new DatabaseConfig(
                    type,
                    "jdbc:postgresql://localhost:5432/" + projectName.toLowerCase().replace("-", "_"),
                    "postgres",
                    "password",
                    "org.postgresql.Driver");
            case MARIADB -> new DatabaseConfig(
                    type,
                    "jdbc:mariadb://localhost:3306/" + projectName.toLowerCase().replace("-", "_"),
                    "root",
                    "password",
                    "org.mariadb.jdbc.Driver");
            case ORACLE -> new DatabaseConfig(
                    type,
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "system",
                    "password",
                    "oracle.jdbc.OracleDriver");
            default -> throw new IllegalArgumentException("Unsupported database type: " + type);
        };
    }

    public String generateApplicationProperties() {
        StringBuilder properties = new StringBuilder();

        if (type == DatabaseType.H2) {
            properties.append("# H2 Database Configuration\n");
            properties.append("spring.datasource.url=").append(url).append("\n");
            properties.append("spring.datasource.username=").append(username).append("\n");
            properties.append("spring.datasource.password=").append(password).append("\n");
            properties.append("spring.datasource.driver-class-name=").append(driverClassName).append("\n");
            properties.append("spring.h2.console.enabled=true\n");
            properties.append("spring.h2.console.path=/h2-console\n");
        } else {
            properties.append("# ").append(type.getDisplayName()).append(" Database Configuration\n");
            properties.append("# Please update the connection details below according to your database setup\n");
            properties.append("spring.datasource.url=").append(url).append("\n");
            properties.append("spring.datasource.username=").append(username).append("\n");
            properties.append("spring.datasource.password=").append(password).append("\n");
            properties.append("spring.datasource.driver-class-name=").append(driverClassName).append("\n");
        }

        properties.append("\n");
        properties.append("# JPA/Hibernate Configuration\n");
        properties.append("spring.jpa.hibernate.ddl-auto=update\n");
        properties.append("spring.jpa.show-sql=true\n");
        properties.append("spring.jpa.properties.hibernate.format_sql=true\n");

        if (type == DatabaseType.MYSQL || type == DatabaseType.MARIADB) {
            properties.append("spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect\n");
        } else if (type == DatabaseType.POSTGRESQL) {
            properties.append("spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect\n");
        } else if (type == DatabaseType.ORACLE) {
            properties.append("spring.jpa.database-platform=org.hibernate.dialect.Oracle12cDialect\n");
        }

        properties.append("\n");
        properties.append("# Server Configuration\n");
        properties.append("server.port=8080\n");

        return properties.toString();
    }

    public DatabaseType type() {
        return type;
    }

    public String url() {
        return url;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String driverClassName() {
        return driverClassName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DatabaseConfig) obj;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.url, that.url) &&
                Objects.equals(this.username, that.username) &&
                Objects.equals(this.password, that.password) &&
                Objects.equals(this.driverClassName, that.driverClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, url, username, password, driverClassName);
    }

    @Override
    public String toString() {
        return "DatabaseConfig[" +
                "type=" + type + ", " +
                "url=" + url + ", " +
                "username=" + username + ", " +
                "password=" + password + ", " +
                "driverClassName=" + driverClassName + ']';
    }


}
