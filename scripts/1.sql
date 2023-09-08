DROP TABLE IF EXISTS "USER_INFO";
CREATE TABLE "USER_INFO" (
                             "id" integer NOT NULL,
                             "name" integer TEXT,
                             "type" TEXT,
                             "child" INTEGER,
                             "sex" TEXT,
                             CONSTRAINT "USER_INFO_pk" PRIMARY KEY ("id")
);

-- ----------------------------
-- Records of USER_INFO
-- ----------------------------
INSERT INTO "USER_INFO" VALUES (1, 121212, 'C', 1, '男');
INSERT INTO "USER_INFO" VALUES (55, '初始值5', 'B', 0, '男');
INSERT INTO "USER_INFO" VALUES (111, 33, 'B', 1, '男');
INSERT INTO "USER_INFO" VALUES (333, '初始值333', 'C', 1, '男');
INSERT INTO "USER_INFO" VALUES (444, '初始值44', 'D', 0, '中间');
INSERT INTO "USER_INFO" VALUES (555, '初始值55', 'C', 1, '男');
INSERT INTO "USER_INFO" VALUES (666, '初始值666', 'B', 0, '女');

