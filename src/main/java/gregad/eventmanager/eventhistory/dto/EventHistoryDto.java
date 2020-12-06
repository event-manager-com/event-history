package gregad.eventmanager.eventhistory.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import gregad.eventmanager.eventhistory.model.Message;
import gregad.eventmanager.eventhistory.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * @author Greg Adler
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventHistoryDto {
    private long id;
    private User owner;
    private String title;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;
    @JsonFormat(pattern = "KK:mm a")
    private LocalTime eventTime;
    private String imageUrl;
    private String telegramChannelRef;
    private List<User> invited;
    private List<Message> correspondences;
}
