package gregad.eventmanager.eventhistory.service;

import gregad.event_manager.loggerstarter.aspect.DoLogging;
import gregad.eventmanager.eventhistory.dao.EventDao;
import gregad.eventmanager.eventhistory.dto.EventHistoryDto;
import gregad.eventmanager.eventhistory.model.EventEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Greg Adler
 */
@Service
@DoLogging
public class HistoryServiceImpl implements HistoryService {
    private EventDao eventRepo;

    @Autowired
    public HistoryServiceImpl(EventDao eventRepo) {
        this.eventRepo = eventRepo;
    }

    @Override
    public boolean addAllEvent(List<EventEntity> events) {
        if (events==null || events.isEmpty()){
            return false;
        }
        List<EventEntity> eventEntities = eventRepo.saveAll(events);
        return !eventEntities.isEmpty();
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public EventHistoryDto getEventById(long id,int ownerId) {
        EventEntity eventEntity = eventRepo.findByIdAndOwnerId(id,ownerId).orElse(null);
        return toEventResponseDto(eventEntity);
    }
    private EventHistoryDto toEventResponseDto(EventEntity eventEntity) {
        if (eventEntity==null)return null;
        EventHistoryDto eventHistoryDto = new EventHistoryDto();
        eventHistoryDto.setId(eventEntity.getId());
        eventHistoryDto.setOwner(eventEntity.getOwner());
        eventHistoryDto.setTitle(eventEntity.getTitle());
        eventHistoryDto.setDescription(eventEntity.getDescription());
        eventHistoryDto.setEventDate(eventEntity.getEventDate());
        eventHistoryDto.setEventTime(eventEntity.getEventTime());
        eventHistoryDto.setInvited(eventEntity.getInvited());
        eventHistoryDto.setApprovedGuests(eventEntity.getApprovedGuests());
        eventHistoryDto.setCorrespondences(eventEntity.getCorrespondences());
        return eventHistoryDto;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<EventHistoryDto> getEventByTitle(int ownerId, String title) {
        List<EventEntity>events=eventRepo.findAllByOwnerIdAndTitleContaining(ownerId,title).orElse(new ArrayList<>());
        return events.stream().map(this::toEventResponseDto).collect(Collectors.toList());
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<EventHistoryDto> getEventsByDate(int ownerId, LocalDate from, LocalDate to) {
        return eventRepo.findAllByOwnerId(ownerId)
                .orElse(new ArrayList<>())
                .stream()
                .filter(e->(e.getEventDate().isAfter(from) || e.getEventDate().isEqual(from)) 
                        && (e.getEventDate().isBefore(to) || e.getEventDate().isEqual(to)))
                .map(this::toEventResponseDto)
                .collect(Collectors.toList());
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Override
public List<EventHistoryDto> getEventsByGuestName(int ownerId, String userInvited) {
    List<EventEntity> eventsFromRepo = eventRepo.findAllByOwnerId(ownerId)
            .orElse(new ArrayList<>())
            .stream()
            .filter(e -> e.getApprovedGuests().stream().anyMatch(u -> u.getName().equals(userInvited)))
            .collect(Collectors.toList());
    return eventsFromRepo.stream().map(this::toEventResponseDto).collect(Collectors.toList());
}
}
