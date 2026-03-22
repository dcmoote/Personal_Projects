namespace API.Modules.Database.Models;

public class Friend
{
    public Guid Id { get; set; }
    public Guid UserId { get; set; }
    public User User { get; set; }
    public Guid FriendUserId { get; set; }
    public User FriendUser { get; set; }
    public bool FriendRequestConfirmed { get; set; }
    public bool IsDeleted { get; set; } = false;
}
