package proiectmap.socialmap.repo;
import proiectmap.socialmap.domain.Entity;
import proiectmap.socialmap.utils.paging.*;

import java.util.List;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
}