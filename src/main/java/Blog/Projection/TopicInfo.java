package Blog.Projection;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * A Projection for the {@link Blog.model.Topic} entity
 */
public interface TopicInfo {
    Long getId();

    String getTitolo();

    String getTesto();

    LocalDateTime getCreatedAt();

    LocalDateTime getModifiedAt();

    String getAnteprima();

    UserInfo getUser();

    /**
     * A Projection for the {@link Blog.model.User} entity
     */
    interface UserInfo {
        Long getId();

        String getUsername();

        Collection<RoleInfo> getRoles();

        /**
         * A Projection for the {@link Blog.model.Role} entity
         */
        interface RoleInfo {
            Long getId();

            String getName();
        }
    }
}