-- Creator:       MySQL Workbench 8.0.21/ExportSQLite Plugin 0.1.0
-- Author:        Desktop
-- Caption:       New Model
-- Project:       Name of the project
-- Changed:       2020-09-08 20:59
-- Created:       2020-08-16 19:12
CREATE TABLE IF NOT EXISTS "playlists"
(
    "id"    INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "owner" VARCHAR(64)                       NOT NULL,
    "name"  VARCHAR(256)                      NOT NULL
);
CREATE TABLE IF NOT EXISTS "signs"
(
    "location" VARCHAR(256) PRIMARY KEY NOT NULL
);
CREATE TABLE IF NOT EXISTS "playlist_song"
(
    "playlists_id" INTEGER NOT NULL,
    "song_hash"    INTEGER NOT NULL,
    "pos"          INTEGER NOT NULL,
    PRIMARY KEY ("playlists_id", "song_hash"),
    CONSTRAINT "fk_playlist_song_playlists1"
        FOREIGN KEY ("playlists_id")
            REFERENCES "playlists" ("id")
            ON DELETE CASCADE
            ON UPDATE CASCADE
);