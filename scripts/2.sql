-- ----------------------------
-- Table structure for USER_INFO1
-- ----------------------------
DROP TABLE IF EXISTS "USER_INFO1";
CREATE TABLE "USER_INFO1" (
                              "id" integer NOT NULL,
                              "name" text,
                              "type" text,
                              "child" text,
                              "sex" text,
                              CONSTRAINT "USER_INFO1_pk" PRIMARY KEY ("id")
);
