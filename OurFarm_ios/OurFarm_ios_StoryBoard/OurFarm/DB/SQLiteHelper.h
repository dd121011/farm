//
//  MyViewController.h
//  OurFarm_ios
//
//  Sqlite操作辅助类
//
//  Created by 李 凤勇 on 13-5-28.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "sqlite3.h"

@interface SQLiteHelper : NSObject
{
    // 数据库连接
    sqlite3 *database;
}
@property (nonatomic) sqlite3 *database;

// 数据库文件路径
- (NSString *) sqliteDBFilePath;
// 初始化数据库连接，打开连接，并返回数据库连接(存放在database中)
- (void) initDatabaseConnection;
// 关闭数据库连接
- (void) closeDatabase;

// 将数据库文件复制为可写数据库
- (void) createEditableCopyOfDatabaseIfNeeded;
@end
