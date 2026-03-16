package io.hypersistence.utils.hibernate.type.array;

import io.hypersistence.utils.hibernate.util.AbstractPostgreSQLIntegrationTest;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests that EnumArrayType correctly persists enums by their name()
 * even when toString() returns a different value.
 */
public class PostgreSQLEnumArrayTypeNameTest extends AbstractPostgreSQLIntegrationTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
                UserAccount.class
        };
    }

    @Override
    protected void beforeInit() {
        executeStatement("DROP TYPE IF EXISTS user_role_name;");
        executeStatement("CREATE TYPE user_role_name AS ENUM ('ROLE_ADMIN', 'ROLE_USER');");
    }

    @Test
    public void testRoundTripWithCustomToString() {
        UserRole[] userRoles = {UserRole.ROLE_ADMIN, UserRole.ROLE_USER};

        doInJPA(entityManager -> {
            UserAccount account = new UserAccount();
            account.setUsername("vladmihalcea.com");
            account.setRoles(userRoles);
            entityManager.persist(account);
        });

        doInJPA(entityManager -> {
            UserAccount singleResult = entityManager
                .createQuery(
                    "select ua " +
                    "from UserAccountNameTest ua " +
                    "where ua.username = :username", UserAccount.class)
                .setParameter("username", "vladmihalcea.com")
                .getSingleResult();

            assertArrayEquals(userRoles, singleResult.getRoles());
        });
    }

    public enum UserRole {
        ROLE_ADMIN,
        ROLE_USER;

        @Override
        public String toString() {
            return name().replace("ROLE_", "");
        }
    }

    @Entity(name = "UserAccountNameTest")
    @Table(name = "users_name_test")
    public static class UserAccount {

        @Id
        @GeneratedValue
        private Long id;

        private String username;

        @Type(
            value = EnumArrayType.class,
            parameters = @org.hibernate.annotations.Parameter(
                name = "sql_array_type",
                value = "user_role_name"
            )
        )
        @Column(
            name = "roles",
            columnDefinition = "user_role_name[]"
        )
        private UserRole roles[];

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public UserRole[] getRoles() {
            return roles;
        }

        public void setRoles(UserRole[] roles) {
            this.roles = roles;
        }
    }
}
