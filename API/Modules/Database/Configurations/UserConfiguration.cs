using API.Modules.Database.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace API.Modules.Database.Configurations;

public class UserConfiguration: IEntityTypeConfiguration<User>
{
    public void Configure(EntityTypeBuilder<User> builder)
    {
        builder
            .Property(u => u.Id)
            .HasColumnType("char(36)");

        builder
            .Property(u => u.Username)
            .IsRequired();

        builder
            .Property(u => u.Email)
            .IsRequired();

        builder
            .Property(u => u.FirstName)
            .IsRequired();

        builder
            .Property(u => u.LastName)
            .IsRequired();

        builder
            .Property(u => u.BirthDate)
            .IsRequired();

        builder
            .Property(u => u.Password)
            .IsRequired();

        var admin = new User
        {
            Id = new Guid("00000000-0000-0000-0000-000000000001"),
            Username = "admin",
            Email = "admin@email.com",
            Password = "admin",
            FirstName = "Admin",
            LastName = "Admin",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds(),
        };

        var userOne = new User
        {
            Id = new Guid("00000000-0000-0000-0000-000000000002"),
            Username = "userOne",
            Email = "userone@email.com",
            Password = "userone",
            FirstName = "User",
            LastName = "One",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds(),
        };

        var userTwo = new User
        {
            Id = new Guid("00000000-0000-0000-0000-000000000003"),
            Username = "userTwo",
            Email = "usertwo@email.com",
            Password = "usertwo",
            FirstName = "User",
            LastName = "Two",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds(),
        };
        
        var userThree = new User
        {
            Id = new Guid("00000000-0000-0000-0000-000000000004"),
            Username = "userThree",
            Email = "userthree@email.com",
            Password = "userthree",
            FirstName = "User",
            LastName = "Three",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds(),
        };

        builder.HasData(admin, userOne, userTwo, userThree);
    }
}
