package org.example.workspace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.workspace.common.ApplicationConstant;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_contents")
public class Contents extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100)
    @Column(name = "contents_name", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String contentsName;

    @Size(max = 255)
    @Column(name = "contents_og_name", nullable = false)
    private String contentsOgName;

    @Size(max = 255)
    @Column(name = "contents_path", nullable = false)
    private String contentsPath;

    @Column(name = "contents_size", nullable = false)
    private Long contentsSize;

    @Column(name = "contents_type", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String contentsType;

    public static Contents create(String fileName, String ogFileName, String contentsPath, Long fileSize, String contentType) {
        return Contents.builder()
                .contentsName(fileName)
                .contentsOgName(ogFileName)
                .contentsPath(contentsPath)
                .contentsSize(fileSize)
                .contentsType(contentType)
                .build();
    }
}
