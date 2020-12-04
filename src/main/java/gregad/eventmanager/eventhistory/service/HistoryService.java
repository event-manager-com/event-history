package gregad.eventmanager.eventhistory.service;

import gregad.eventmanager.eventhistory.dto.EventHistoryDto;
import gregad.eventmanager.eventhistory.model.EventEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Greg Adler
 */
public interface HistoryService {
    boolean addAllEvent(List<EventEntity> events);
    EventHistoryDto getEventById(long eventId,int ownerId);
    List<EventHistoryDto> getEventByTitle(int ownerId, String title);
    List<EventHistoryDto> getEventsByDate(int ownerId, LocalDate from, LocalDate to);
    List<EventHistoryDto> getEventsBySentNetworks(int ownerId, List<String> networks);

}
