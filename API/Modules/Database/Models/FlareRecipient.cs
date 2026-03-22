namespace API.Modules.Database.Models;

public class FlareRecipient
{
    public Guid Id { get; set; }
    public Guid FlareId { get; set; }
    public Guid RecipientId { get; set; }
    public User Recipient { get; set; }
    public Guid OwnerId { get; set; }
    public User Owner { get; set; }
    public required bool HasAccepted { get; set; }
    public bool IsDeleted { get; set; } = false;
}
