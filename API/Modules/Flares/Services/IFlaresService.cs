using API.Modules.Database.Models;

namespace API.Modules.Flares.Services;

public interface IFlaresService
{
    Task<Flare> AddFlare(Flare flare);
    Task<Flare?> GetFlare(Guid id);
    Task<Flare?> UpdateFlare(Guid id, Flare flareUpdate);
    Task<List<FlareRecipient>> AddFlareRecipients(List<FlareRecipient> flareRecipients);
    Task<List<FlareRecipient>> GetFlareRecipients(Guid flareId);
    Task<List<FlareRecipient>> UpdateFlareRecipients(Guid flareId, List<FlareRecipient> flareRecipients);
    Task<List<Flare>> GetFlares(Guid id);
    Task<Flare?> DeleteFlare(Guid id);
    Task<FlareRecipient?> DeleteRecipient(Guid flareId, Guid recipientId);
    Task<Event> AddEvent(Event flareEvent);
}
