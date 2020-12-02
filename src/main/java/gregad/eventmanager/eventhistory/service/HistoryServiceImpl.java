package gregad.eventmanager.eventhistory.service;

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
    public EventHistoryDto getEventById(long id,String ownerId) {
        EventEntity eventEntity = eventRepo.findByIdAndOwnerId(id,ownerId).orElseThrow(()->{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Event id:"+id+" not found in user id:"+ownerId+" storage");
        });
        return toEventResponseDto(eventEntity);
    }
    private EventHistoryDto toEventResponseDto(EventEntity eventEntity) {
        EventHistoryDto eventHistoryDto = new EventHistoryDto();
        eventHistoryDto.setId(eventEntity.getId());
        eventHistoryDto.setOwner(eventEntity.getOwner());
        eventHistoryDto.setTitle(eventEntity.getTitle());
        eventHistoryDto.setDescription(eventEntity.getDescription());
        eventHistoryDto.setEventDate(eventEntity.getEventDate());
        eventHistoryDto.setEventTime(eventEntity.getEventTime());
        eventHistoryDto.setSentToNetworkConnections(eventEntity.getSentToNetworkConnections());
        eventHistoryDto.setInvited(eventEntity.getInvited());
        eventHistoryDto.setCorrespondences(eventEntity.getCorrespondences());
        return eventHistoryDto;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<EventHistoryDto> getEventByTitle(String ownerId, String title) {
        List<EventEntity>events=eventRepo.findAllByOwnerIdAndTitleContaining(ownerId,title).orElse(new ArrayList<>());
        return events.stream().map(this::toEventResponseDto).collect(Collectors.toList());
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<EventHistoryDto> getEventsByDate(String ownerId, LocalDate from, LocalDate to) {
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
    public List<EventHistoryDto> getEventsBySentNetworks(String ownerId, List<String> networks) {
        return eventRepo.findAllByOwnerId(ownerId).orElse(new ArrayList<>()).stream()
                .filter(e-> e.getSentToNetworkConnections().keySet().stream().anyMatch(networks::contains))
                .collect(Collectors.toSet())
                .stream()
                .map(this::toEventResponseDto)
                .collect(Collectors.toList());
    }
}
