# -Messenger


# Описание работы приложения:

Вначале пользователю дается возможность зарегистрироваться. После успешной регистрации его данные сохраняются в базе данных (PostgreSQL), 
и на его почту отправляется письмо для подтверждения регистрации. Затем он может в любой момент выполнить вход в систему, 
после чего ему выдается Refresh token. Refresh token является HTTP ONLY COOKIE и имеет срок действия 5 дней.
Он также сохраняется в базе данных для списка доверенных токенов. Кроме того, пользователю выдается Access Token с сроком действия 10 минут,
который используется для получения доступа к закрытым ресурсам. Access Token может быть обновлен запросом, пока Refresh token не истек.

Пользователь может в любой момент выполнить выход из системы, и его Refresh token станет недействительным.
Залогиненные пользователи могут отправлять сообщения другим пользователям с использованием WebSocket(сохраняются в базе данных (MongoDB)). 
Также пользователь имеет возможность обновить все свои данные, удалить свой аккаунт( перевод профиля в статус “Не активен” ), добавлять других пользователей в друзья,
просматривать историю общения с другим пользователем и список своих друзей.



# Стек технологий:
Spring Boot 3.1.4,
Java 17,
Spring Data JPA,
Spring Security,
PostgreSQL,
Lombok,
JWT,
Java Mail,
Spring Data MongoDB,
Spring Boot Starter Validation,
Spring Boot Starter Websocket,
Springdoc OpenAPI

# Примеры запросов и ответов:

# К AuthController:

/auth/reg (POST)

Запрос:

POST /auth/reg
Content-Type: application/json

{ 
    "username": "Noy1234",
    "email": "karsva666@gmail.com",
    "password": "Password123",
    "name":  "karapet",
    "surname": "svarian"
} 
Ответ:

200 OK
Content-Type: application/json

{
  "message": "You have successfully registered. Please confirm your email."
}
или при неправильно указанном email:

400 Bad Request
Content-Type: application/json

{
  "message": "Invalid email"
}
/auth/login (POST)

Запрос:

POST /auth/login
Content-Type: application/json

{
  "username": "existingUser",
  "password": "Password123"
}
Ответ:

200 OK
Content-Type: application/json

{
  "token": "authToken",
  "refreshToken":"refreshToken"
}
или при неправильно введенных учетных данных:

400 Bad Request
Content-Type: application/json

{
  "message": "Incorrect username or password"
}
/auth/confirm (GET)

Запрос:

GET /auth/confirm?token=verificationToken
Ответ:

200 OK
Content-Type: application/json

{
  "message": "Email confirmed successfully."
}
или при неправильном токене:

400 Bad Request
Content-Type: application/json

{
  "message": "Email Token Not Found."
}
/auth/logout (POST)

Запрос:

POST /auth/logout
Content-Type: application/json

{
  "username": "existingUser"
}
Ответ:

200 OK
"Logged out successfully."
или если пользователь не найден:

401 Unauthorised

/auth/refresh (GET)

Запрос:

GET /auth/refresh
Authorization: Bearer refreshToken
Ответ:

200 OK
Content-Type: application/json

{
  "token": "newAuthToken",
  "refreshToken":"newRefreshToken"
}
или при неправильном или истекшем токене обновления:

401 Unauthorised
Content-Type: application/json

{"message":"Invalid or expired refresh token"}



# К FriendController:


/friend/add (POST)

Запрос:

POST /friend/add
Content-Type: application/json

{
"friendUsername": "usernameToBeAdded"
}
Ответ:

200 OK
Content-Type: application/json

{
"message": "User with username:usernameToBeAdded added to your friends"
}
или при неправильном имени пользователя:

400 Bad Request
Content-Type: application/json

{
"message": "User with username:usernameToBeAdded not found"
}

/friend/get_all (GET)

Запрос:

GET /friend/get_all
Ответ:

200 OK
Content-Type: application/json

[
{
"username": "friend1",
и т.д.
},
{
"username": "friend2",
и т.д.
}
]
или если друзей нет:

200 OK
Content-Type: application/json
[]




# К ProfileController:

/profile/update (PUT)

Запрос:

PUT /profile/update
Content-Type: application/json

{
{ 
    "username": "Noy1234",
    "name":  "karapet",
    "surname": "svarian"
} 
}
Ответ:

200 OK
Content-Type: application/json

{
"message": "Profile updated successfully"
}
или при ошибке в запросе:

400 Bad Request
Content-Type: application/json

{
"errors": "Ошибка валидации данных"
}
/profile/change-password (PUT)

Запрос:

PUT /profile/change-password
Content-Type: application/json

{
"password": "OldPassword123",
"newPassword": "NewPassword123"
}

Ответ:

200 OK
Content-Type: application/json

{
"message": "Password changed successfully"
}

или при неправильно введенном текущем пароле:

400 Bad Request
Content-Type: application/json

{
"message": "Incorrect password"
}

/profile/change-email (PUT)

Запрос:

PUT /profile/change-email
Content-Type: application/json

{
"email": "usernew@example.com"
}

Ответ:

200 OK
Content-Type: application/json

{
"message": "Email changed successfully"
}

или при неправильно указанном текущем email:

400 Bad Request
Content-Type: application/json

{
"message": "Invalid email"
}

/profile/delete (DELETE)

Запрос:

DELETE /profile/delete
Content-Type: application/json

{
"username": "userToDelete"
}

Ответ:

200 OK
Content-Type: application/json

{
"message": "Profile deleted successfully"
}






# Swagger:
{
  "openapi": "3.0.1",
  "info": {
    "title": "Messenger",
    "contact": {
      "name": "Svarian Karapet",
      "email": "ksvarian@mail.ru"
    },
    "version": "1.0.0"
  },
  "servers": [
    {
      "url": "http://localhost:8080",
      "description": "Generated server url"
    }
  ],
  "tags": [
    {
      "name": "Контроллер сообщений(Message Controller)",
      "description": "В этом контроллере описаны методы обработки и отправки сообщений (This controller describes the messaging management and sending methods)"
    },
    {
      "name": "Контроллер друзей(Friend Controller)",
      "description": "В этом контроллере описаны методы для управление друзьями: добавление и получение друзей(This controller describes methods for managing friends: adding and getting friends)"
    },
    {
      "name": "Контроллера авторизации(Authorization controller)",
      "description": "В этом контроллере описаны методы авторизации,входа и выхода (This controller describes the authorization, login and logout methods)"
    },
    {
      "name": "Контроллер профиля(Profile Controller)",
      "description": "В этом контроллере описаны методы для работы с профилем пользователя(This controller describes the methods for working with a user profile)"
    }
  ],
  "paths": {
    "/profile/update": {
      "put": {
        "tags": [
          "Контроллер профиля(Profile Controller)"
        ],
        "summary": "Обновление профиля(Profile update)",
        "description": "Позволяет пользователю обновить свой профиль(Allows the user to update their profile)",
        "operationId": "updateProfile",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/ProfileRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/profile/change-password": {
      "put": {
        "tags": [
          "Контроллер профиля(Profile Controller)"
        ],
        "summary": "Изменение пароля(Change password)",
        "description": "Позволяет пользователю изменить свой пароль(Allows the user to change their password)",
        "operationId": "changePassword",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/PasswordChangeRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/profile/change-email": {
      "put": {
        "tags": [
          "Контроллер профиля(Profile Controller)"
        ],
        "summary": "Изменение электронной почты(Change email)",
        "description": "Позволяет пользователю изменить свой адрес электронной почты(Allows the user to change their email address)",
        "operationId": "changeEmail",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/EmailChangeRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/friend/add": {
      "post": {
        "tags": [
          "Контроллер друзей(Friend Controller)"
        ],
        "summary": "Добавление друга(Add friend)",
        "description": "Позволяет добавлять друга по имени пользователя(Allows you to add a friend by username)",
        "operationId": "addFriend",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/AddFriendRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/auth/reg": {
      "post": {
        "tags": [
          "Контроллера авторизации(Authorization controller)"
        ],
        "summary": "Регистрация пользователя(User registration)",
        "description": "Позволяет зарегистрировать пользователя(Allows you to register a user)",
        "operationId": "registration",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/RegistrationRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/auth/logout": {
      "post": {
        "tags": [
          "Контроллера авторизации(Authorization controller)"
        ],
        "summary": "Выход(Logout)",
        "description": "Позволяет  пользователя выйти из своего аккаунта(Allows the user to log out of their account)",
        "operationId": "logOut",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/LogOutRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/auth/login": {
      "post": {
        "tags": [
          "Контроллера авторизации(Authorization controller)"
        ],
        "summary": "Вход(Login)",
        "description": "Позволяет  пользователям войти в свой аккаунт(Allows users to log into their account)",
        "operationId": "logIn",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/LoginRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/friend/get_all": {
      "get": {
        "tags": [
          "Контроллер друзей(Friend Controller)"
        ],
        "summary": "Получение всех друзей(Get all friends)",
        "description": "Позволяет получить список всех друзей(Allows you to get a list of all friends)",
        "operationId": "getFriends",
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/chat-history": {
      "get": {
        "tags": [
          "Контроллер сообщений(Message Controller)"
        ],
        "summary": "История чата(Chat History)",
        "description": "Позволяет получать историю сообщений между пользователями(Allows users to get the message history between them)",
        "operationId": "getChatHistory",
        "parameters": [
          {
            "name": "senderUsername",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "recipientUsername",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/auth/refresh": {
      "get": {
        "tags": [
          "Контроллера авторизации(Authorization controller)"
        ],
        "summary": "Обновлениение токена доступа(Access Token Refresh)",
        "description": "Позволяет  обновлять токены досупа(Allows access tokens to be updated)",
        "operationId": "refreshAccessToken",
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/auth/confirm": {
      "get": {
        "tags": [
          "Контроллера авторизации(Authorization controller)"
        ],
        "summary": "Подтверждение электонной почты(Email confirmation)",
        "description": "Позволяет пользователям подтверждать свои электронные почти(Allows users to verify their email accounts)",
        "operationId": "confirm",
        "parameters": [
          {
            "name": "token",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/profile/delete": {
      "delete": {
        "tags": [
          "Контроллер профиля(Profile Controller)"
        ],
        "summary": "Удаление профиля(Profile delete)",
        "description": "Позволяет пользователю удалить свой профиль(Allows the user to delete their profile)",
        "operationId": "deleteProfile",
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    }
  },
  "components": {
    "schemas": {
      "ProfileRequest": {
        "required": [
          "name",
          "surname",
          "username"
        ],
        "type": "object",
        "properties": {
          "name": {
            "type": "string",
            "description": "Имя (Name)",
            "example": "Oleg"
          },
          "username": {
            "type": "string",
            "description": "Имя пользователя (Username)",
            "example": "Oleggg"
          },
          "surname": {
            "type": "string",
            "description": "Фамилия (Surname)",
            "example": "Olegov"
          }
        },
        "description": "Модель запроса для профиля пользователя(Profile request model)"
      },
      "PasswordChangeRequest": {
        "required": [
          "newPassword",
          "username"
        ],
        "type": "object",
        "properties": {
          "username": {
            "type": "string",
            "description": "Имя пользователя (Username)",
            "example": "User123"
          },
          "password": {
            "minLength": 6,
            "type": "string",
            "description": "Старый пароль (Old password)",
            "example": "password123"
          },
          "newPassword": {
            "maxLength": 2147483647,
            "minLength": 6,
            "type": "string",
            "description": "Новый пароль (New password)",
            "example": "newPassword123"
          }
        },
        "description": "Модель запроса для изменения пароля (Password change request model)"
      },
      "EmailChangeRequest": {
        "required": [
          "email"
        ],
        "type": "object",
        "properties": {
          "email": {
            "type": "string",
            "description": "Новый адрес электронной почты(New email)",
            "example": "example@gmail.com"
          }
        }
      },
      "AddFriendRequest": {
        "type": "object",
        "properties": {
          "friendUsername": {
            "type": "string",
            "description": "Имя другка (Friend's name)",
            "example": "Oleg"
          }
        }
      },
      "RegistrationRequest": {
        "required": [
          "email",
          "name",
          "password",
          "surname",
          "username"
        ],
        "type": "object",
        "properties": {
          "name": {
            "type": "string",
            "description": "Имя пользователя(Name)",
            "example": "Ivan"
          },
          "username": {
            "type": "string",
            "description": "Уникальное имя пользователя(Unique Username)",
            "example": "User123"
          },
          "surname": {
            "type": "string",
            "description": "Фамилия пользователя(Surname)",
            "example": "Ivanov"
          },
          "password": {
            "maxLength": 2147483647,
            "minLength": 6,
            "type": "string",
            "description": "Пароль(Password)",
            "example": "password123"
          },
          "email": {
            "type": "string",
            "description": "Электронная почта(Email)",
            "example": "user@example.com"
          }
        },
        "description": "Модель запроса для регистрации(Registration request model)"
      },
      "LogOutRequest": {
        "required": [
          "username"
        ],
        "type": "object",
        "properties": {
          "username": {
            "type": "string",
            "description": "Имя пользователя (Username)",
            "example": "User123"
          }
        },
        "description": "Модель запроса для выхода из системы(Logout request model)"
      },
      "LoginRequest": {
        "required": [
          "password",
          "username"
        ],
        "type": "object",
        "properties": {
          "username": {
            "type": "string",
            "description": "Имя пользователя (Username)",
            "example": "User123"
          },
          "password": {
            "maxLength": 2147483647,
            "minLength": 6,
            "type": "string",
            "description": "Пароль (Password)",
            "example": "password123"
          }
        },
        "description": "Модель запроса для логина(Login request model)"
      }
    }
  }
}
