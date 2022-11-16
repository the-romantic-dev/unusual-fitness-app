CREATE TABLE "workouts" (
"id" INTEGER PRIMARY KEY,
"type" INTEGER,
"start_time" INTEGER,
"finish_time" INTEGER,
"distance" INTEGER,
"duration" INTEGER,
"snapshot" BLOB,
"route" TEXT,
"zoom" REAL,
"center" TEXT
);