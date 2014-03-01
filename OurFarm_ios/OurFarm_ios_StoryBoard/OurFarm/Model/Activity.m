//
//  Activity.m
//  OurFarm
//
//  Created by tian hao on 13-6-27.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "Activity.h"
#import "AFAppDotNetAPIClient.h"
#import "AFJSONRequestOperation.h"

@implementation Activity{
@private
    NSString *_avatarImageURLString;
}


- (id)initWithAttributes:(NSDictionary *)attributes {
    self = [super init];
    if (!self) {
        return nil;
    }
    
    //contanst
    _activityId = [attributes valueForKeyPath:@"activityId"];
    _name = [attributes valueForKeyPath:@"name"];
    _introduction = [attributes objectForKey:@"introduction"];
    _startTime = [attributes objectForKey:@"startTime"];
    _endTime = [attributes objectForKey:@"endTime"];
    _lat = [attributes valueForKeyPath:@"lat"];
    _lng = [attributes valueForKeyPath:@"lng"];
    _address = [attributes objectForKey:@"address"];
    _avatarImageURLString = [attributes valueForKeyPath:@"pic"];
    _pic = [attributes valueForKeyPath:@"pic"];
    _tel = [attributes valueForKeyPath:@"tel"];
    _www = [attributes valueForKeyPath:@"www"];
    _type = [attributes valueForKeyPath:@"type"];
    
    return self;
}
#pragma mark - get search概要信息

+ (void)getActivityWithBlock:(void (^)(NSArray *posts, NSError *error))block
                 requestParameter:param {
    
    [AFJSONRequestOperation addAcceptableContentTypes:[NSSet setWithObject:@"text/html"]];
    
    [[AFAppDotNetAPIClient sharedClient] postPath:@"web/Activity.php" parameters:param success:^(AFHTTPRequestOperation *operation, id JSON) {
        
        NSLog(@"JSON: %@", JSON);
        
        NSMutableArray *mutablePosts = [[NSMutableArray alloc] init];
        
        for (NSDictionary *attributes in JSON) {
            Activity *activity = [[Activity alloc] initWithAttributes:attributes];
            [mutablePosts addObject:activity];
        }
        if (block) {
            block([NSArray arrayWithArray:mutablePosts], nil);
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        if (block) {
            block([NSArray array], error);
        }
    }];
}


@end
