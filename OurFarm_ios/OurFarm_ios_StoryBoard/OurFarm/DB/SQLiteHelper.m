//
//  MyViewController.h
//  OurFarm_ios
//
//  Sqlite操作辅助类
//
//  Created by 李 凤勇 on 13-5-28.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import "SQLiteHelper.h"

static NSString *kSQLiteFileName = @"ourfarm.db";

@implementation SQLiteHelper

@synthesize database;

// 数据库文件路径
- (NSString *) sqliteDBFilePath
{	
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *path = [documentsDirectory stringByAppendingPathComponent:kSQLiteFileName];
	NSLog(@"path = %@", path);
	
	return path;
}

// 初始化数据库连接，打开连接，并返回数据库连接(存放在database中)
- (void) initDatabaseConnection
{	
    if (sqlite3_open([[self sqliteDBFilePath] UTF8String], &database) != SQLITE_OK)
	{
        sqlite3_close(database);
        NSAssert1(0, @"Failed to open database with message '%s'.", sqlite3_errmsg(database));
    }
}

// 关闭数据库连接
- (void) closeDatabase
{
    if (sqlite3_close(database) != SQLITE_OK)
	{
        NSAssert1(0, @"Error: failed to close database with message '%s'.", sqlite3_errmsg(database));
    }
}

// Creates a writable copy of the bundled default database in the application Documents directory.
- (void) createEditableCopyOfDatabaseIfNeeded
{
    // First, test for existence.
    NSFileManager *fileManager = [NSFileManager defaultManager];
	
    NSString *writableDBPath = [self sqliteDBFilePath];
    // NSLog(@"%@", writableDBPath);
    BOOL success = [fileManager fileExistsAtPath: writableDBPath];
    if (success)
	{
		return;
	}
	
    // The writable database does not exist, so copy the default to the appropriate location.
    NSString *defaultDBPath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:kSQLiteFileName];
    NSError *error;
    success = [fileManager copyItemAtPath:defaultDBPath toPath:writableDBPath error:&error];
    
	if (!success)
	{
        NSAssert1(0, @"Failed to create writable database file with message '%@'.", [error localizedDescription]);
    }
}


@end
