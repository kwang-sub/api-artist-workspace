package org.example.workspace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user_verification")
public class UserVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @Column(name = "verification_code", nullable = false, length = 100)
    private String verificationCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static UserVerification create(User user) {
        return UserVerification.builder()
                .user(user)
                .verificationCode(generateSixDigitNumber())
                .build();
    }


    private static String generateSixDigitNumber() {
        long currentTimeMillis = System.currentTimeMillis();
        int randomNumber = (int) ((currentTimeMillis % 900000) + 100000);
        return String.valueOf(randomNumber);
    }

    public void updateCode() {
        this.verificationCode = generateSixDigitNumber();
    }
}