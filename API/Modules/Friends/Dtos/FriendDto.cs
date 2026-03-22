namespace API.Modules.Friends.Dtos;

public class FriendDto
{
    public Guid Id { get; set; }
    public Guid UserId { get; set; } // The id of the friend represented by this dto, not the id of the user they are friends with.
    public required string FirstName { get; set; }
    public required string LastName { get; set; }
    public required string Username { get; set; }
    public bool FriendRequestConfirmed { get; set; }
}
