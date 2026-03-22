using API.Modules.Database.Models;
using API.Modules.Flares.Dtos;

namespace API.Modules.Database.Extensions;

public static class EventExtension
{
    public static EventDto ToDto(this Event model, List<FlareRecipient> recipients)
    {
        return new EventDto
        {
            FlareDto = model.Flare.ToDto(model.Flare.Recipients),
            Name = model.Name
        };
    }

    public static Event ToModel(this EventDto dto)
    {
        return new Event
        {
            Flare = dto.FlareDto.ToModel(),
            Name = dto.Name
        };
    }
}