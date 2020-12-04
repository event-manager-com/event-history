package gregad.eventmanager.eventhistory.dao;

import gregad.eventmanager.eventhistory.model.EventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Greg Adler
 */
@Repository
public interface EventDao extends MongoRepository<EventEntity,Long> {
    
    Optional<EventEntity> findByIdAndOwnerId(long id,int ownerId);

    Optional<List<EventEntity>> findAllByOwnerIdAndTitleContaining(int ownerId, String title);

    Optional<List<EventEntity>> findAllByOwnerId(int ownerId);
}
