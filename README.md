# [@TableBot](https://t.me/Table116bot)

#### Build requirements
- Java 8
- Gradle

#### Deployment requirements
- Docker


### Deployment

1. Clone the repository

2.1 Change token, botname, namesOfTables and SPREADSHEET_ID (take from your google tables link) in the file along the path: ``` tableBot/src/main/java/Bot.java```

2.2 Change sendingPeriod value in milliseconds in the file along the path: ```tableBot/src/main/java/Main.java``` 

3. Open https://developers.google.com/sheets/api/quickstart/java, click Enable Google Sheets API and download client config. Replace ```tableBot/src/main/resources/credentials.json``` file with the file you just downloaded.

4. Create a docker image with this command in the root directory  

    ```shell script
    gradle buildImage
    ```           

5. Start the image using this command

    ```shell script
    docker run --network="host" YOUR IMAGE ID
    ``` 
6. Follow the link and provide access to view and edit tables from your Google account.
