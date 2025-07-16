# Flare API
## Installation
1. Install Docker (MacOs and Windows only).
    * [Mac install](https://docs.docker.com/desktop/setup/install/mac-install/)
    * [Windows install](https://docs.docker.com/desktop/setup/install/windows-install/)

2. Clone the repo.
```
git clone https://github.com/JHerget/flare-api.git
```

3. Create a `.env` file in the root directory of the repo with this template:
```
JWT_SECRET=
MYSQL_ROOT_PASSWORD=
MYSQL_FLARE_ADMIN_PASSWORD=
```

4. Generate a random string for each `.env` variable and add it to the file.
```
openssl rand -hex 32
```

5. In the `flare-api/API/Properties` directory add the `JWT_SECRET` and `MYSQL_FLARE_ADMIN_PASSWORD` to the environment property of your launch settings. For example, you might have a profile called `http` that would then look like this:
```
"profiles": {
    "http": {
      "commandName": "Project",
      "dotnetRunMessages": true,
      "launchBrowser": true,
      "launchUrl": "swagger",
      "applicationUrl": "http://localhost:5285",
      "environmentVariables": {
        "ASPNETCORE_ENVIRONMENT": "Development",
        "MYSQL_PASSWORD": "<add-mysql-flare-admin-password>",
        "JwtSettings__Secret": "<add-jwt-secret>"
      }
    }
}
```

## Running the API
**Before each new Docker run you should do `docker compose down` first.**

1. Build the project and setup the database.
```
docker compose up -d --wait --build
```

2. The API is running when you see a message like this:
```
✔ api                         Built                                                       
✔ migrations                  Built                                                        
✔ Network flare-api_backend  Created                                                      
✔ Container mysql_container   Healthy                                                      
✔ Container migrations        Exited                                                       
✔ Container flare-api        Healthy    
```

*Note: Even if you run the API locally you will still need to use the Docker command to ensure the database is running. If this is the case, you can drop the `--build` flag.*

## Using the API
### Running Locally
When running the API locally, the base url will be whatever is in the `applicationUrl` property of your launch profile (as shown in the example above). 

*Note: The port cannot be `8081` since that is the port the Docker instance uses for the API.*

### Running in Docker
When running the API in Docker, the base url will be `http://localhost:8081`.

---

Once the API is running you can use the Swagger page (local only) to determine the endpoint urls and make test requests there or through Postman. Additionally, in the `herget-api/API/Http` directory there are sample requests (you will need to install the [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) extension in VS Code and setup the environment variables).

## Clearing the Database
If you want to clear the database and start fresh you can run these commands after running Docker.

```
docker exec -it flare-api dontet ef database drop -f --project /app/API
docker exec -it flare-api dontet ef database update --project /app/API
```

## Adding Migrations
Add migrations using:
```
docker exec -it flare-api dontet ef migrations add <name-the-migration> --project /app/API
```

To run migrations, restart Docker.
```
docker compose down
docker compose up -d --wait
```

## Debugging
When running the API in Docker you can view errors and logs using:

```
docker logs flare-api
```
