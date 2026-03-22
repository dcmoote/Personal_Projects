namespace API.Modules.Flares.Dtos;

public class FlareRecipientDto
{
    public Guid UserId { get; set; }
    public bool HasAccepted { get; set; }
}
