-- Creator:       MySQL Workbench 8.0.21/ExportSQLite Plugin 0.1.0
-- Author:        Desktop
-- Caption:       New Model
-- Project:       Name of the project
-- Changed:       2020-08-16 19:20
-- Created:       2020-08-16 19:12
PRAGMA foreign_keys = OFF;

CREATE TABLE "playlists"
(
    "id"    INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "owner" VARCHAR(64)                       NOT NULL,
    "name"  VARCHAR(256)                      NOT NULL
);
CREATE TABLE "player_settings"
(
    "uuid"  VARCHAR(64) PRIMARY KEY NOT NULL,
    "key"   VARCHAR(32)             NOT NULL,
    "value" VARCHAR(256),
    CONSTRAINT "unque"
        UNIQUE ("uuid", "key")
);
CREATE TABLE "playlist_song"
(
    "playlists_id" INTEGER PRIMARY KEY NOT NULL,
    "song_hash"    INTEGER             NOT NULL,
    CONSTRAINT "fk_playlist_song_playlists1"
        FOREIGN KEY ("playlists_id")
            REFERENCES "playlists" ("id")
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

PRAGMA foreign_keys = ON;
