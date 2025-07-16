FROM mcr.microsoft.com/dotnet/sdk:8.0 AS final
WORKDIR /app

COPY . .
RUN chmod +x docker-compose.migrations.sh

RUN dotnet tool install --global dotnet-ef
ENV PATH="$PATH:/root/.dotnet/tools"

RUN dotnet publish -c release -o /app/publish
WORKDIR /app/publish

CMD ["dotnet", "API.dll"]
