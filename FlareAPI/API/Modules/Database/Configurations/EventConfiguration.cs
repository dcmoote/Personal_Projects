using API.Modules.Database.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace API.Modules.Database.Configurations;

public class EventConfiguration: IEntityTypeConfiguration<Event>
{
    public void Configure(EntityTypeBuilder<Event> builder)
    {
        builder
            .HasOne(e => e.Flare)
            .WithMany()
            .HasForeignKey(e => e.FlareId);

        builder
            .Property(e => e.Id)
            .HasColumnType("char(36)");

        builder
            .Property(e => e.FlareId)
            .HasColumnType("char(36)");

        builder
            .Property(e => e.Name)
            .IsRequired();

        var eventOne = new Event
        {
            Id = new Guid("00000000-0000-0000-0000-000000000001"),
            FlareId = new Guid("00000000-0000-0000-0000-000000000002"),
            Name = "Sample Event",
            Tag = "Sample Tag",
        };

        builder.HasData(eventOne);
    }
}
