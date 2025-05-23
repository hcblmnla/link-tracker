package backend.academy.scrapper.link.service.orm.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLinkId {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "link_id")
    private Long linkId;
}
