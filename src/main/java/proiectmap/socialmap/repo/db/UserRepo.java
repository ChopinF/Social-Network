package proiectmap.socialmap.repo.db;

import proiectmap.socialmap.repo.PagingRepository;
import proiectmap.socialmap.domain.User;
import proiectmap.socialmap.utils.paging.Page;
import proiectmap.socialmap.utils.paging.Pageable;

import java.util.List;

public interface UserRepo extends PagingRepository<Long, User> {
    Page<User> findAllOnPage(Pageable pageable);
}
