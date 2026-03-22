using API.Modules.Database.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace API.Modules.Database.Configurations;

public class FriendConfiguration: IEntityTypeConfiguration<Friend>
{
    public void Configure(EntityTypeBuilder<Friend> builder)
    {
        builder
            .HasOne(f => f.User)
            .WithMany()
            .HasForeignKey(fr => fr.UserId);

        builder
            .HasOne(f => f.FriendUser)
            .WithMany()
            .HasForeignKey(fr => fr.FriendUserId);

        builder
            .Property(f => f.Id)
            .HasColumnType("char(36)");

        builder
            .Property(f => f.UserId)
            .HasColumnType("char(36)");

        builder
            .Property(f => f.FriendUserId)
            .HasColumnType("char(36)");

        builder
            .Property(f => f.FriendRequestConfirmed)
            .IsRequired();

        var friendOne = new Friend
        {
            Id = new Guid("00000000-0000-0000-0000-000000000001"),
            UserId = new Guid("00000000-0000-0000-0000-000000000001"),
            FriendUserId = new Guid("00000000-0000-0000-0000-000000000002"),
            FriendRequestConfirmed = false,
        };

        var friendTwo = new Friend
        {
            Id = new Guid("00000000-0000-0000-0000-000000000002"),
            UserId = new Guid("00000000-0000-0000-0000-000000000001"),
            FriendUserId = new Guid("00000000-0000-0000-0000-000000000003"),
            FriendRequestConfirmed = true,
        };

        var friendThree = new Friend
        {
            Id = new Guid("00000000-0000-0000-0000-000000000003"),
            UserId = new Guid("00000000-0000-0000-0000-000000000004"),
            FriendUserId = new Guid("00000000-0000-0000-0000-000000000001"),
            FriendRequestConfirmed = true,
        };

        var friendUnaccepted = new Friend
        {
            Id = new Guid("00000000-0000-0000-0000-000000000004"),
            UserId = new Guid("00000000-0000-0000-0000-000000000004"),
            FriendUserId = new Guid("00000000-0000-0000-0000-000000000004"),
            FriendRequestConfirmed = false,
        };

        builder.HasData(friendOne, friendTwo, friendThree, friendUnaccepted);
    }
}
