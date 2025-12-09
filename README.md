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
REVERSE1999_DROPS=
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

<details>  
<summary>Workflow DeuS для n8n</summary>  

```json
{
  "name": "DeuS",
  "nodes": [
    {
      "parameters": {
        "httpMethod": "POST",
        "path": "voice",
        "options": {
          "allowedOrigins": "*"
        }
      },
      "type": "n8n-nodes-base.webhook",
      "typeVersion": 2,
      "position": [
        -1760,
        448
      ],
      "id": "da03bc0b-6b1b-4009-af8a-95ee3612e14e",
      "name": "Webhook",
      "webhookId": "3ef70859-cab4-4797-bd2e-382c74e40488"
    },
    {
      "parameters": {
        "model": "tngtech/deepseek-r1t2-chimera:free",
        "options": {}
      },
      "type": "@n8n/n8n-nodes-langchain.lmChatOpenRouter",
      "typeVersion": 1,
      "position": [
        -400,
        624
      ],
      "id": "cf56be31-edc1-4bbb-a090-615f9f629da5",
      "name": "OpenRouter Chat Model",
      "credentials": {
        "openRouterApi": {
          "id": "Vca5VayD7wKXesZo",
          "name": "OpenRouter account"
        }
      }
    },
    {
      "parameters": {
        "sessionIdType": "customKey",
        "sessionKey": "=1 {{ $('Webhook').item.json.query.userId }}",
        "contextWindowLength": 15
      },
      "type": "@n8n/n8n-nodes-langchain.memoryBufferWindow",
      "typeVersion": 1.3,
      "position": [
        -256,
        624
      ],
      "id": "594f2c92-83c2-47af-9281-7398565e5d0d",
      "name": "Simple Memory"
    },
    {
      "parameters": {
        "promptType": "define",
        "text": "={{ $json.text }}",
        "options": {}
      },
      "type": "@n8n/n8n-nodes-langchain.agent",
      "typeVersion": 1.8,
      "position": [
        -336,
        448
      ],
      "id": "4846f989-513f-4093-8bce-0c1b6316f63b",
      "name": "AI Agent",
      "alwaysOutputData": false,
      "onError": "continueErrorOutput"
    },
    {
      "parameters": {
        "method": "POST",
        "url": "https://api.assemblyai.com/v2/upload",
        "authentication": "genericCredentialType",
        "genericAuthType": "httpHeaderAuth",
        "sendBody": true,
        "contentType": "binaryData",
        "inputDataFieldName": "data",
        "options": {}
      },
      "type": "n8n-nodes-base.httpRequest",
      "typeVersion": 4.2,
      "position": [
        -1328,
        448
      ],
      "id": "ccfa5d3a-4c79-4d57-9079-ab8091e62e53",
      "name": "HTTP Request",
      "alwaysOutputData": false,
      "credentials": {
        "httpHeaderAuth": {
          "id": "cTJ3Z0jRyqoApIsJ",
          "name": "Header Auth account"
        }
      },
      "onError": "continueErrorOutput"
    },
    {
      "parameters": {
        "method": "POST",
        "url": "https://api.assemblyai.com/v2/transcript",
        "authentication": "genericCredentialType",
        "genericAuthType": "httpHeaderAuth",
        "sendBody": true,
        "specifyBody": "json",
        "jsonBody": "={\n    \"audio_url\": \"{{ $json.upload_url }}\",\n    \"language_code\": \"ru\"\n} ",
        "options": {}
      },
      "type": "n8n-nodes-base.httpRequest",
      "typeVersion": 4.2,
      "position": [
        -1104,
        448
      ],
      "id": "430ce2f3-3ec0-4b84-89dc-59903fd6eecc",
      "name": "HTTP Request1",
      "alwaysOutputData": false,
      "executeOnce": false,
      "credentials": {
        "httpHeaderAuth": {
          "id": "cTJ3Z0jRyqoApIsJ",
          "name": "Header Auth account"
        }
      },
      "onError": "continueErrorOutput"
    },
    {
      "parameters": {
        "url": "=https://api.assemblyai.com/v2/transcript/{{ $json.id }}",
        "authentication": "genericCredentialType",
        "genericAuthType": "httpHeaderAuth",
        "options": {}
      },
      "type": "n8n-nodes-base.httpRequest",
      "typeVersion": 4.2,
      "position": [
        -576,
        688
      ],
      "id": "344e96e1-e456-463f-b139-d04ef6f232d2",
      "name": "HTTP Request3",
      "alwaysOutputData": false,
      "credentials": {
        "httpHeaderAuth": {
          "id": "cTJ3Z0jRyqoApIsJ",
          "name": "Header Auth account"
        }
      },
      "onError": "continueErrorOutput"
    },
    {
      "parameters": {
        "amount": 1.5
      },
      "type": "n8n-nodes-base.wait",
      "typeVersion": 1.1,
      "position": [
        -736,
        688
      ],
      "id": "5cc0c2a6-9edc-4b73-9c88-6f6fb81b1541",
      "name": "Wait",
      "webhookId": "8cdb9511-f5e1-4d9e-b82f-c964ea2752f3",
      "onError": "continueErrorOutput"
    },
    {
      "parameters": {
        "mode": "combine",
        "fieldsToMatchString": "id",
        "joinMode": "keepEverything",
        "options": {}
      },
      "type": "n8n-nodes-base.merge",
      "typeVersion": 3.1,
      "position": [
        -880,
        448
      ],
      "id": "e1c2ec8a-a41e-462f-9093-0caa2010be5d",
      "name": "Merge",
      "onError": "continueErrorOutput"
    },
    {
      "parameters": {
        "conditions": {
          "options": {
            "caseSensitive": true,
            "leftValue": "",
            "typeValidation": "strict",
            "version": 2
          },
          "conditions": [
            {
              "id": "0040f141-67da-4bc9-9fee-261d68d826e7",
              "leftValue": "={{ $json.status }}",
              "rightValue": "completed",
              "operator": {
                "type": "string",
                "operation": "equals",
                "name": "filter.operator.equals"
              }
            }
          ],
          "combinator": "and"
        },
        "options": {}
      },
      "type": "n8n-nodes-base.if",
      "typeVersion": 2.2,
      "position": [
        -736,
        448
      ],
      "id": "9c6feaa4-6cbd-4c88-b003-cc26c20a7e8d",
      "name": "If",
      "onError": "continueErrorOutput"
    },
    {
      "parameters": {
        "operation": "toBinary",
        "sourceProperty": "body.audio",
        "options": {}
      },
      "type": "n8n-nodes-base.convertToFile",
      "typeVersion": 1.1,
      "position": [
        -1568,
        448
      ],
      "id": "58b95bd9-0c70-4128-8668-b2dc1ad23f0d",
      "name": "Convert to File",
      "alwaysOutputData": false,
      "onError": "continueErrorOutput"
    },
    {
      "parameters": {
        "method": "POST",
        "url": "={{ $('Webhook').item.json.body.callback }}",
        "sendBody": true,
        "specifyBody": "json",
        "jsonBody": "={{ $json.answer }}",
        "options": {
          "allowUnauthorizedCerts": true
        }
      },
      "type": "n8n-nodes-base.httpRequest",
      "typeVersion": 4.2,
      "position": [
        304,
        448
      ],
      "id": "67334c39-57d6-4bef-816d-9c14c747407c",
      "name": "HTTP Request2",
      "retryOnFail": false
    },
    {
      "parameters": {
        "numberInputs": 8
      },
      "type": "n8n-nodes-base.merge",
      "typeVersion": 3.1,
      "position": [
        0,
        0
      ],
      "id": "1134a8aa-cfb6-4bd8-a4a0-fc8ba9f5b329",
      "name": "Merge1"
    },
    {
      "parameters": {
        "method": "POST",
        "url": "={{ $('Webhook').item.json.body.callback }}",
        "sendBody": true,
        "specifyBody": "json",
        "jsonBody": "={{ $json.error }}",
        "options": {
          "allowUnauthorizedCerts": true
        }
      },
      "type": "n8n-nodes-base.httpRequest",
      "typeVersion": 4.2,
      "position": [
        400,
        112
      ],
      "id": "97ec3c7d-e065-4c5b-89db-4975b4339986",
      "name": "HTTP Request4",
      "retryOnFail": false
    },
    {
      "parameters": {
        "errorType": "errorObject",
        "errorObject": "={{ $('Merge1').item.json.error }}"
      },
      "type": "n8n-nodes-base.stopAndError",
      "typeVersion": 1,
      "position": [
        624,
        112
      ],
      "id": "89577767-33d5-4a7c-a390-7db9ac7301c3",
      "name": "Stop and Error"
    },
    {
      "parameters": {
        "jsCode": "return ({\n  error: JSON.stringify({\n    channelId: $('Webhook').first().json.body.channelId,\n    message: {\n      type: \"ERROR\",\n      title: \"Произошла ошибка\",\n      description: `При обработке аудио-запроса произошла ошибка\\n\\`${$input.first().json.error.name}: ${$input.first().json.error.message}\\``\n    }\n  })\n});"
      },
      "type": "n8n-nodes-base.code",
      "typeVersion": 2,
      "position": [
        208,
        112
      ],
      "id": "17a21ccb-3d20-441e-9d64-019d2e35170c",
      "name": "Code"
    },
    {
      "parameters": {
        "jsCode": "return ({\n  answer: JSON.stringify({\n    channelId: $('Webhook').first().json.body.channelId,\n    message: {\n      type: \"INFO\",\n      title: \"Ответ DeuS'а\",\n      description: `<@${$('Webhook').first().json.body.userId}>:\\n> ${$('HTTP Request3').first().json.text}\\n\\nDeuS:\\n>>> ${$input.first().json.output || '-'}`\n    }\n  })\n});"
      },
      "type": "n8n-nodes-base.code",
      "typeVersion": 2,
      "position": [
        80,
        448
      ],
      "id": "11a6103a-9e87-4b78-82c0-6b1e85f3610f",
      "name": "Code1"
    }
  ],
  "pinData": {},
  "connections": {
    "OpenRouter Chat Model": {
      "ai_languageModel": [
        [
          {
            "node": "AI Agent",
            "type": "ai_languageModel",
            "index": 0
          }
        ]
      ]
    },
    "Simple Memory": {
      "ai_memory": [
        [
          {
            "node": "AI Agent",
            "type": "ai_memory",
            "index": 0
          }
        ]
      ]
    },
    "Webhook": {
      "main": [
        [
          {
            "node": "Convert to File",
            "type": "main",
            "index": 0
          }
        ]
      ]
    },
    "AI Agent": {
      "main": [
        [
          {
            "node": "Code1",
            "type": "main",
            "index": 0
          }
        ],
        [
          {
            "node": "Merge1",
            "type": "main",
            "index": 7
          }
        ]
      ]
    },
    "HTTP Request": {
      "main": [
        [
          {
            "node": "HTTP Request1",
            "type": "main",
            "index": 0
          }
        ],
        [
          {
            "node": "Merge1",
            "type": "main",
            "index": 1
          }
        ]
      ]
    },
    "HTTP Request1": {
      "main": [
        [
          {
            "node": "Merge",
            "type": "main",
            "index": 0
          }
        ],
        [
          {
            "node": "Merge1",
            "type": "main",
            "index": 2
          }
        ]
      ]
    },
    "HTTP Request3": {
      "main": [
        [
          {
            "node": "Merge",
            "type": "main",
            "index": 1
          }
        ],
        [
          {
            "node": "Merge1",
            "type": "main",
            "index": 6
          }
        ]
      ]
    },
    "Wait": {
      "main": [
        [
          {
            "node": "HTTP Request3",
            "type": "main",
            "index": 0
          }
        ],
        [
          {
            "node": "Merge1",
            "type": "main",
            "index": 5
          }
        ]
      ]
    },
    "Merge": {
      "main": [
        [
          {
            "node": "If",
            "type": "main",
            "index": 0
          }
        ],
        [
          {
            "node": "Merge1",
            "type": "main",
            "index": 3
          }
        ]
      ]
    },
    "If": {
      "main": [
        [
          {
            "node": "AI Agent",
            "type": "main",
            "index": 0
          }
        ],
        [
          {
            "node": "Wait",
            "type": "main",
            "index": 0
          }
        ],
        [
          {
            "node": "Merge1",
            "type": "main",
            "index": 4
          }
        ]
      ]
    },
    "Convert to File": {
      "main": [
        [
          {
            "node": "HTTP Request",
            "type": "main",
            "index": 0
          }
        ],
        [
          {
            "node": "Merge1",
            "type": "main",
            "index": 0
          }
        ]
      ]
    },
    "Merge1": {
      "main": [
        [
          {
            "node": "Code",
            "type": "main",
            "index": 0
          }
        ]
      ]
    },
    "HTTP Request4": {
      "main": [
        [
          {
            "node": "Stop and Error",
            "type": "main",
            "index": 0
          }
        ]
      ]
    },
    "Code": {
      "main": [
        [
          {
            "node": "HTTP Request4",
            "type": "main",
            "index": 0
          }
        ]
      ]
    },
    "Code1": {
      "main": [
        [
          {
            "node": "HTTP Request2",
            "type": "main",
            "index": 0
          }
        ]
      ]
    }
  },
  "active": false,
  "settings": {
    "executionOrder": "v1"
  },
  "versionId": "55fcf450-0994-4d60-8778-93758b9f3177",
  "meta": {
    "templateCredsSetupCompleted": true,
    "instanceId": "05a7d67db5218c0e528489b6a078b05365dbd33e35fc4d2371fe937db9bc46df"
  },
  "id": "R5xVrUpnaaCblDth",
  "tags": []
}
```
</details> 

#### Production

```bash
docker run --restart always --name deus-bot-db -p 5432:5432 -e POSTGRES_USER=deus-bot-user -e POSTGRES_PASSWORD=<password> -e POSTGRES_DB=deus-bot -d -v "/db/deus-bot-db":/var/lib/postgresql/data postgres:16.3-alpine
```

#### PreProduction

```bash
docker run --restart always --name deus-test-bot-db -p 5433:5432 -e POSTGRES_USER=deus-test-bot-user -e POSTGRES_PASSWORD=<password> -e POSTGRES_DB=deus-test-bot -d -v "/db/deus-test-bot-db":/var/lib/postgresql/data postgres:16.3-alpine
```