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
    EventHistoryDto getEventById(long eventId,String ownerId);
    List<EventHistoryDto> getEventByTitle(String ownerId, String title);
    List<EventHistoryDto> getEventsByDate(String ownerId, LocalDate from, LocalDate to);
    List<EventHistoryDto> getEventsBySentNetworks(String ownerId, List<String> networks);

}
