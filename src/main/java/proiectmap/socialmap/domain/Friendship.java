package proiectmap.socialmap.domain;

import java.time.LocalDate;


//CREATE TABLE friendships (
//        id SERIAL PRIMARY KEY, -- A unique identifier for the friendship (optional, if needed)
//user1_id BIGINT NOT NULL, -- ID of the first user
//user2_id BIGINT NOT NULL, -- ID of the second user
//friends_since DATE NOT NULL, -- Date when the friendship was established
//status VARCHAR(50) NOT NULL, -- Status of the friendship (e.g., "Active", "Pending", etc.)
//CONSTRAINT unique_friendship UNIQUE (user1_id, user2_id), -- Ensure no duplicate friendships
//CONSTRAINT fk_user1 FOREIGN KEY (user1_id) REFERENCES users (id) ON DELETE CASCADE,
//CONSTRAINT fk_user2 FOREIGN KEY (user2_id) REFERENCES users (id) ON DELETE CASCADE
//);


public class Friendship extends Entity<Long> {
    private Long user1Id;
    private Long user2Id;
    private LocalDate friendsSince;
    private Status status;

    public boolean isRecipient(Long currentUserId) {
        return user2Id.equals(currentUserId);
    }

    public Friendship(Long user1Id, Long user2Id, LocalDate friendsSince, String status) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.friendsSince = friendsSince;
        this.status = Status.fromString(status);
    }

    public Long getUser1Id() {
        return user1Id;
    }

    public Long getUser2Id() {
        return user2Id;
    }

    public LocalDate getFriendsSince() {
        return friendsSince;
    }

    public void setFriendsSince(LocalDate curentDate){
        this.friendsSince = curentDate;
    }

    public String getStatus() {
        return status.getValue();
    }

    public void setStatus(String status) {
        this.status = Status.fromString(status);
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "id=" + getId() +
                ", user1Id=" + user1Id +
                ", user2Id=" + user2Id +
                ", friendsSince=" + friendsSince +
                ", status='" + status + '\'' +
                '}';
    }
}