# DeusBot

### Настройка рабочего места

1. Создать в [ресурсах](./src/main/resources) файл `.env`, добавить и заполнить следующими переменными окружения:

#### secrets

```dotenv
ANILIST_OAUTH2_ID=
ANILIST_OAUTH2_SECRET=
DATABASE_PASSWORD=[application-local.yaml]
DATABASE_USERNAME=[application-local.yaml]
DISCORD_OAUTH2_ID=
DISCORD_OAUTH2_SECRET=
DISCORD_TOKEN=
GOOGLE_OAUTH2_ID=
GOOGLE_OAUTH2_SECRET=
KEY_STORE_PASSWORD=
SHIKIMORI_OAUTH2_ID=
SHIKIMORI_OAUTH2_SECRET=
YOUTUBE_REFRESH_TOKEN=
```

#### vars

```dotenv
ANILIST_GRAPHQL_URL=
ANILIST_URL=
APP_URL=
ARTING_URL=
DATABASE_MAX_POOL=10
DATABASE_URL=[application-local.yaml]
DEVS_DISCORD_IDS=
DEUS_URL=
DISCORD_URL=
FREE_STEAM_URL=
GOOGLE_URL=
REVERSE1999_POOLS=
REVERSE1999_WIKI_URL=
SHIKIMORI_GRAPHQL_PATH=
SHIKIMORI_URL=
```

### Настройка инфраструктуры

#### Общие

```bash
docker run -it --restart always --name n8n -p 5678:5678 -e N8N_SECURE_COOKIE=false -d -v n8n_data:/home/node/.n8n docker.n8n.io/n8nio/n8n
```

#### Production

```bash
docker run --restart always --name deus-bot-db -p 5432:5432 -e POSTGRES_USER=deus-bot-user -e POSTGRES_PASSWORD=<password> -e POSTGRES_DB=deus-bot -d -v "/db/deus-bot-db":/var/lib/postgresql/data postgres:16.3-alpine
```

#### PreProduction

```bash
docker run --restart always --name deus-test-bot-db -p 5433:5432 -e POSTGRES_USER=deus-test-bot-user -e POSTGRES_PASSWORD=<password> -e POSTGRES_DB=deus-test-bot -d -v "/db/deus-test-bot-db":/var/lib/postgresql/data postgres:16.3-alpine
```