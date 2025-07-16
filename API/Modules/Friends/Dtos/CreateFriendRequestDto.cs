namespace API.Modules.Friends.Dtos;

public class CreateFriendRequestDto
{
    public Guid Id { get; set; }
    public Guid FriendUserId { get; set; }
}
