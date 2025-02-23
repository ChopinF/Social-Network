package proiectmap.socialmap.domain.validators;

import proiectmap.socialmap.domain.Friendship;
import proiectmap.socialmap.domain.User;
import proiectmap.socialmap.repo.db.UserDBRepository;

import java.util.Optional;

public class FriendshipValidator implements Validator<Friendship> {
    private UserDBRepository repo;
    public FriendshipValidator(UserDBRepository repo) {
        this.repo = repo;
    }
    @Override
    public void validate(Friendship entity) throws ValidationException {

        Optional<User> u1 = repo.findOne(entity.getUser1Id());
        Optional<User> u2 = repo.findOne(entity.getUser2Id());

        if (entity.getUser1Id() == null || entity.getUser2Id() == null)
            throw new ValidationException("The id can't be null! ");
        if (u1.isEmpty() || u2.isEmpty())
            throw new ValidationException("The id doesn't exist! ");
    }
}
