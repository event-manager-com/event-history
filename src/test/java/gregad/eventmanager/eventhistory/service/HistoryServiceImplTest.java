package gregad.eventmanager.eventhistory.service;

import gregad.eventmanager.eventhistory.EventHistoryApplication;
import gregad.eventmanager.eventhistory.dao.EventDao;
import gregad.eventmanager.eventhistory.dto.EventHistoryDto;
import gregad.eventmanager.eventhistory.model.EventEntity;
import gregad.eventmanager.eventhistory.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.mockito.Mockito.when;

/**
 * @author Greg Adler
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EventHistoryApplication.class)
class HistoryServiceImplTest {
    private EventDao eventRepo;
    private HistoryService historyService;
    private static List<EventEntity>eventEntityList;
    
    @BeforeAll
    public static void initEvents(){
        List<User>users=generateUsers(20);
        eventEntityList=generateEvents(20,false,users);
        eventEntityList.addAll(generateEvents(20,true,users));
    }

    private static List<EventEntity> generateEvents(int amount,boolean isRepeated,List<User>users) {
        List<EventEntity>res=new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setId(isRepeated?20+i:i);
            eventEntity.setOwner(users.get(i));
            eventEntity.setTitle("Event of:"+i);
            eventEntity.setDescription("Some Description");
            eventEntity.setEventDate(LocalDate.of(2020,isRepeated?10:12,12));
            eventEntity.setEventTime(LocalTime.of(12,01));
            if (isRepeated) {
                eventEntity.setSentToNetworkConnections(Map.of("facebook", users, "twitter", users));
            } else {
                eventEntity.setSentToNetworkConnections(Map.of("facebook", users));
            }
            eventEntity.setInvited(Map.of("facebook",users,"twitter",users));
            eventEntity.setCorrespondences(new HashMap<>());
            res.add(eventEntity);
        }
        return res;
    }

    private static List<User> generateUsers(int amount) {
        List<User>res=new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            res.add(new User(i,"name"+i));
        }
        return res;
    }


    @BeforeEach
    public void init(){
        eventRepo= Mockito.mock(EventDao.class);
        historyService=new HistoryServiceImpl(eventRepo);
    }

    @Test
     void addAllEvent() {
        when(eventRepo.saveAll(eventEntityList)).thenReturn(eventEntityList);

        Assert.assertTrue(historyService.addAllEvent(eventEntityList));
        Assert.assertFalse(historyService.addAllEvent(null));
        Assert.assertFalse(historyService.addAllEvent(new ArrayList<>()));
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void getEventById() {
        when(eventRepo.findByIdAndOwnerId(0,0)).
                thenReturn(java.util.Optional.ofNullable(eventEntityList.get(0)));

        EventHistoryDto result = historyService.getEventById(0, 0);
        Assert.assertEquals(0,result.getId());
        Assert.assertEquals(0,result.getOwner().getId());
        Assert.assertEquals("name0",result.getOwner().getName());

        try {
            historyService.getEventById(0,2);
        } catch (ResponseStatusException e) {
            Assert.assertEquals("Event id:0 not found in user id:2 storage",e.getReason());
        }
        try {
            historyService.getEventById(2,0);
        } catch (ResponseStatusException e) {
            Assert.assertEquals("Event id:2 not found in user id:0 storage",e.getReason());
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void getEventByTitle() {
        when(eventRepo.findAllByOwnerIdAndTitleContaining(0,"Event0")).
                thenReturn(Optional.of(List.of(eventEntityList.get(0),eventEntityList.get(20))));

        List<EventHistoryDto> result = historyService.getEventByTitle(0, "Event0");
        Assert.assertEquals(2,result.size());
        Assert.assertEquals("name0",result.get(0).getOwner().getName());
        Assert.assertEquals(0,result.get(0).getOwner().getId());
        
        Assert.assertEquals(0,historyService.getEventByTitle(1,"Event0").size());

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void getEventsByDate() {
        when(eventRepo.findAllByOwnerId(0)).thenReturn(Optional.of(List.of(eventEntityList.get(0),eventEntityList.get(20))));

        List<EventHistoryDto> result = historyService.getEventsByDate(0, 
                LocalDate.of(2020, 11, 2), LocalDate.of(2020, 12, 12));
        Assert.assertEquals(1,result.size());
        Assert.assertTrue(result.get(0).getEventDate().isAfter( LocalDate.of(2020, 11, 2)));
        Assert.assertTrue(result.get(0).getEventDate().isEqual( LocalDate.of(2020, 12, 12)));

        List<EventHistoryDto> anotherResult = historyService.getEventsByDate(0,
                LocalDate.of(2020, 11, 2), LocalDate.of(2020, 12, 13));
        Assert.assertTrue(anotherResult.get(0).getEventDate().isBefore( LocalDate.of(2020, 12, 13)));
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void getEventsBySentNetworks() {
        when(eventRepo.findAllByOwnerId(0)).thenReturn(Optional.of(List.of(eventEntityList.get(0),eventEntityList.get(20))));

        List<EventHistoryDto> result = historyService.getEventsBySentNetworks(0, List.of("twitter"));
        
        Assert.assertEquals(1,result.size());
        Assert.assertTrue(result.get(0).getSentToNetworkConnections().containsKey("twitter"));

        List<EventHistoryDto> anotherResult = historyService.getEventsBySentNetworks(0, List.of("facebook"));
        Assert.assertEquals(2,anotherResult.size());
    }
}