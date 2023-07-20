package de.hsos.swa.jonas.theater.security.entity;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.transaction.Transactional;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@UserDefinition
public class User extends PanacheEntity {
    @Username @Size(min=3, max=25)
    @Pattern(regexp = "^[a-zA-z ]*$")
    public String username;
    @Password
    public String password;
    @Roles
    public String role;

    @Transactional(Transactional.TxType.MANDATORY)
    public static void add(String username, String password, String role) {
        User.find("username", username).firstResultOptional().ifPresentOrElse(
                user -> {
                    throw new RuntimeException("User already exists");
                },
                () -> {
                    User user = new User();
                    user.username = username;
                    user.password = BcryptUtil.bcryptHash(password);
                    user.role = role;
                    user.persist();
                }
        );
    }
}
