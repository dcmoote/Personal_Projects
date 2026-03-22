namespace API.Modules.Flares.Dtos;

public class EventDto
{
    public FlareDto FlareDto { get; set; }
    public required string Name { get; set; }
}