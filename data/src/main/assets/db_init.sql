CREATE TABLE "workouts" (
"id" INTEGER PRIMARY KEY,
"type" INTEGER NOT NULL,
"start_time" INTEGER NOT NULL,
"finish_time" INTEGER NOT NULL,
"distance" INTEGER NOT NULL,
"duration" INTEGER NOT NULL,
"snapshot" BLOB NOT NULL
);

CREATE TABLE "points" (
"train_id" INTEGER NOT NULL,
"latitude" REAL NOT NULL,
"longitude" REAL NOT NULL
);