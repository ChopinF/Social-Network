package proiectmap.socialmap.repo.db;

import proiectmap.socialmap.repo.PagingRepository;
import proiectmap.socialmap.domain.Friendship;
import proiectmap.socialmap.utils.paging.Page;
import proiectmap.socialmap.utils.paging.Pageable;

public interface FriendshipRepo extends PagingRepository<Long, Friendship> {
    Page<Friendship> findAllOnPage(Pageable pageable);
}
