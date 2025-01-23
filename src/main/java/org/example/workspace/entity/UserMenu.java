package org.example.workspace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.workspace.entity.code.MenuType;

@Getter
@Entity
@Table(name = "tbl_user_menu")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contents_id", nullable = false)
    private Contents contents;

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_type", nullable = false, length = 100)
    private MenuType menuType;

    public static UserMenu create(User user, Contents contents, MenuType menuType) {
        return UserMenu.builder()
                .user(user)
                .contents(contents)
                .menuType(menuType)
                .build();
    }

    public void update(Contents contents, MenuType menuType) {
        this.contents = contents;
        this.menuType = menuType;
    }
}