package com.dataagent.examples.jpa;

import com.dataagent.jpa.annotation.DataAgent;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * @CreatedBy h.rasouli
 */

@DataAgent(
        description = "Example JPA entity Profile for demonstration purposes",
        discoverable = true
)
@Entity
@Table(name = "UM_PROFILE")
public class Profile {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private String id;
    private String nationalCode;
    private String birthPlace;
    private String birthday;
    private String fatherName;
    private String mobileNumber;
    private String personnelCode;

    @OneToOne(mappedBy = "profile")
    private User user;
    private String photoFileId;
}
